package com.zhukovskii.currencyconverter.model.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ExchangeRate::class],
    version = 1
)
abstract class ExchangeRateDatabase : RoomDatabase() {
    abstract val dao: ExchangeRateDao
}