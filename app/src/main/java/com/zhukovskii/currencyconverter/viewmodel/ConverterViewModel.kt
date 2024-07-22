package com.zhukovskii.currencyconverter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhukovskii.currencyconverter.model.repository.ExchangeRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: ExchangeRateRepository
): ViewModel() {

    private val _conversionResultFlow: MutableStateFlow<ConversionResult> =
        MutableStateFlow(ConversionResult.Empty)

    val conversionResultFlow: StateFlow<ConversionResult> =
        _conversionResultFlow.asStateFlow()

    private val _errorFlow: MutableSharedFlow<Exception> = MutableSharedFlow()

    val errorFlow: SharedFlow<Exception> = _errorFlow.asSharedFlow()

    /**
     * Try to produce a currency conversion
     *
     * Conversion is emitted to the `conversionResultFlow` if successful
     *
     * If an error occurs while fetching data from network, the exception
     * is emitted to the `errorFlow`
     *
     * If the exchange rate for given currencies could not be retrieved, the
     * conversion is deemed unsuccessful and `NoExchangeRateException` is
     * emitted to the `errorFlow`
     *
     * Both currencies must be strings formatted as `"<CODE> (<NAME>)"`,
     * where `CODE` is the three-letter currency code and `NAME` is the full
     * name of the currency
     */
    fun makeConversion(
        fromCurrency: String,
        toCurrency: String,
        amount: BigDecimal
    ) {
        val (baseCode, baseName) = fromCurrency.split(" ", limit = 2)
        val (targetCode, targetName) = toCurrency.split(" ", limit = 2)

        viewModelScope.launch {

            try {
                repository.fetchFromApi(baseCode)
            } catch (e: Exception) {
                _errorFlow.emit(e)
            }

            repository.getExchangeRate(baseCode, targetCode)?.let {
                _conversionResultFlow.value =
                    ConversionResult.Success(
                        baseCode,
                        targetCode,
                        baseName,
                        targetName,
                        amount,
                        it.exchangeRate,
                        amount * it.exchangeRate.toBigDecimal()
                    )
            } ?: _errorFlow.emit(NoExchangeRateException())
        }
    }

    /**
     * Invalidate the result of the previous conversion
     */
    fun invalidateResult() {
        _conversionResultFlow.value = ConversionResult.Empty
    }
}