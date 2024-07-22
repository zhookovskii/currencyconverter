package com.zhukovskii.currencyconverter.model.api

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {

    @GET("latest/{baseCode}")
    suspend fun getConversionRates(
        @Path("baseCode") baseCode: String
    ): ApiResponse
}