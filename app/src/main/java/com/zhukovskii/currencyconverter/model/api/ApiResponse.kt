package com.zhukovskii.currencyconverter.model.api

import com.squareup.moshi.JsonClass

/**
 * Class that models the response from the API
 *
 * The `result` field can only be equal to `"success"` or `"error"`
 *
 * If `result == "error"`, all fields are omitted besides `error_type`
 *
 * If `result == "success"`, only `error_type` field is omitted
 */
@JsonClass(generateAdapter = true)
data class ApiResponse(
    val result: String,
    val documentation: String?,
    val terms_of_use: String?,
    val time_last_update_unix: Long?,
    val time_last_update_utc: String?,
    val time_next_update_unix: Long?,
    val time_next_update_utc: String?,
    val base_code: String?,
    val conversion_rates: Map<String, Double>?,
    val error_type: String?
)