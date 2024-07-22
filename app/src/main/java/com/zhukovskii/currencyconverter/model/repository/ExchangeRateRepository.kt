package com.zhukovskii.currencyconverter.model.repository

import com.zhukovskii.currencyconverter.model.api.ExchangeRateApi
import com.zhukovskii.currencyconverter.model.db.ExchangeRate
import com.zhukovskii.currencyconverter.model.db.ExchangeRateDatabase

class ExchangeRateRepository(
    private val api: ExchangeRateApi,
    private val db: ExchangeRateDatabase
) {

    /**
     * Fetches fresh data from the API and writes it to the app database
     */
    suspend fun fetchFromApi(baseCode: String) {

        val response = api.getConversionRates(baseCode)

        when (response.result) {

            "success" -> {
                response.conversion_rates
                    ?.map { (targetCode, rate) ->
                        ExchangeRate(baseCode, targetCode, rate)
                    }?.let { db.dao.upsertExchangeRates(it) }
            }

            "error" -> response.error_type?.let {
                throw ApiException(it)
            }
        }
    }

    /**
     * Retrieves the exchange rate for given currencies from the app database
     */
    suspend fun getExchangeRate(baseCode: String, targetCode: String): ExchangeRate? {
        return db.dao.getExchangeRate(baseCode, targetCode)
    }
}