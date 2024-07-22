package com.zhukovskii.currencyconverter.model.repository

/**
 * Exception thrown upon encountering an API response with the
 * `result` field equal to `"error"`
 */
class ApiException(private val errorType: String) : Exception() {

    companion object {
        val errorTypeMap: Map<String, String> = mapOf(
            "unsupported-code" to "Unsupported currency",
            "malformed-request" to "Malformed request",
            "invalid-key" to "Invalid API key",
            "inactive-account" to "Inactive API account",
            "quota-reached" to "Requests quota reached"
        )
    }

    override val message: String?
        get() = errorTypeMap[errorType]
}