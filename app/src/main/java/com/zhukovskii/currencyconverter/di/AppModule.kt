package com.zhukovskii.currencyconverter.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.zhukovskii.currencyconverter.config.Config
import com.zhukovskii.currencyconverter.model.api.ExchangeRateApi
import com.zhukovskii.currencyconverter.model.db.ExchangeRateDatabase
import com.zhukovskii.currencyconverter.model.repository.ExchangeRateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(): ExchangeRateApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/${Config.API_KEY}/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ExchangeRateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDb(app: Application): ExchangeRateDatabase {
        return Room.databaseBuilder(
            app,
            ExchangeRateDatabase::class.java,
            "currencyconverter.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(
        api: ExchangeRateApi,
        db: ExchangeRateDatabase
    ) = ExchangeRateRepository(api, db)
}