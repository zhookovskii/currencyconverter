package com.zhukovskii.currencyconverter

import com.zhukovskii.currencyconverter.model.db.ExchangeRate
import com.zhukovskii.currencyconverter.model.repository.Repository
import com.zhukovskii.currencyconverter.viewmodel.ConversionResult
import com.zhukovskii.currencyconverter.viewmodel.ConverterViewModel
import com.zhukovskii.currencyconverter.viewmodel.NoExchangeRateException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException
import java.math.BigDecimal

class ViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    companion object {
        private const val USD_TO_EUR = 0.9183
        private const val USD_TO_RUB = 89.0370
    }

    private val mockRepository = object : Repository {

        var exception: Exception? = null

        fun failIfExceptionIsSet() {
            exception?.let { throw(it) }
        }

        val mockData = mutableSetOf<ExchangeRate>()

        override suspend fun fetchFromApi(baseCode: String) {

            failIfExceptionIsSet()

            mockData.addAll(mockDataFromApi)
        }

        override suspend fun getExchangeRate(
            baseCode: String,
            targetCode: String
        ): ExchangeRate? {
            return mockData.find { it.baseCode == baseCode && it.targetCode == targetCode }
        }

    }

    private val mockDataFromApi = setOf(
        ExchangeRate("USD", "USD", 1.0),
        ExchangeRate("USD", "EUR", USD_TO_EUR),
        ExchangeRate("USD", "RUB", USD_TO_RUB)
    )

    @Test
    fun producesSuccessfulConversion() {

        val viewModel = ConverterViewModel(mockRepository)

        val amount = BigDecimal.ONE

        val result = runBlocking {

            // request a conversion
            viewModel.makeConversion("USD (US Dollar)", "EUR (Euro)", amount)

            // make sure the conversion completes
            delay(100)

            // observe the flow value
            val value = viewModel.conversionResultFlow.value

            // return a set of conditions as a result
            value is ConversionResult.Success
                    && value.amount == amount
                    && value.exchangeRate == USD_TO_EUR
                    && value.resultAmount == amount * USD_TO_EUR.toBigDecimal()
                    && value.baseCode == "USD"
                    && value.targetCode == "EUR"
                    && value.baseName == "(US Dollar)"
                    && value.targetName == "(Euro)"
        }

        // assert the result of the conversion
        Assert.assertTrue(result)
    }

    @Test
    fun producesErrorOnNetworkFailure() {

        val networkException = IOException()

        // set the exception to be thrown during network call
        mockRepository.exception = networkException

        // add the data manually to emulate database persistence
        mockRepository.mockData.addAll(mockDataFromApi)

        val viewModel = ConverterViewModel(mockRepository)

        val amount = BigDecimal.ONE

        val result = runBlocking {

            launch {
                // exception should be emitted swiftly and be of the expected type
                withTimeout(500) {
                    viewModel.errorFlow.take(1).collectLatest {
                        Assert.assertTrue(it is IOException)
                    }
                }
            }

            // request a conversion
            viewModel.makeConversion(
                "USD (US Dollar)",
                "RUB (Russian Ruble)",
                amount
            )

            // make sure the conversion completes
            delay(100)

            // observe the flow value
            val value = viewModel.conversionResultFlow.value

            // the conversion is expected to be successful still because the data was stored
            value is ConversionResult.Success
                    && value.amount == amount
                    && value.exchangeRate == USD_TO_RUB
                    && value.resultAmount == amount * USD_TO_RUB.toBigDecimal()
                    && value.baseCode == "USD"
                    && value.targetCode == "RUB"
                    && value.baseName == "(US Dollar)"
                    && value.targetName == "(Russian Ruble)"
        }

        Assert.assertTrue(result)
    }

    @Test
    fun failsOnUnknownExchangeRate() {

        val viewModel = ConverterViewModel(mockRepository)

        val amount = BigDecimal.ONE

        val result = runBlocking {

            launch {
                // exception should be emitted swiftly and be of the expected type
                withTimeout(500) {
                    viewModel.errorFlow.take(1).collectLatest {
                        Assert.assertTrue(it is NoExchangeRateException)
                    }
                }
            }

            // request a conversion with unknown exchange rate
            viewModel.makeConversion(
                "USD (US Dollar)",
                "RSD (Serbian Dinar)",
                amount
            )

            // make sure the conversion completes
            delay(100)

            // no conversion should be emitted
            viewModel.conversionResultFlow.value is ConversionResult.Empty
        }

        Assert.assertTrue(result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class MainDispatcherRule(
        private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}