package com.zhukovskii.currencyconverter.model.db

import androidx.room.Entity

@Entity(primaryKeys = ["baseCode", "targetCode"])
data class ExchangeRate(
    val baseCode: String,
    val targetCode: String,
    val exchangeRate: Double
)