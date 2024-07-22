package com.zhukovskii.currencyconverter.model.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchangerate WHERE baseCode = :baseCode AND targetCode = :targetCode LIMIT 1")
    suspend fun getExchangeRate(baseCode: String, targetCode: String): ExchangeRate?

    @Upsert
    suspend fun upsertExchangeRates(exchangeRates: List<ExchangeRate>)
}