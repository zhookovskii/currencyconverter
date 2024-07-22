package com.zhukovskii.currencyconverter.model.repository

import com.zhukovskii.currencyconverter.model.db.ExchangeRate

interface Repository {

    suspend fun fetchFromApi(baseCode: String)

    suspend fun getExchangeRate(baseCode: String, targetCode: String): ExchangeRate?
}