package com.zhukovskii.currencyconverter.viewmodel

import java.math.BigDecimal

/**
 * Class which represents the result of the conversion
 *
 * `ConversionResult.Success` is used when the conversion
 * was successful
 *
 * `ConversionResult.Empty` is used when no conversions
 * were yet made or the latest conversion result is no
 * longer needed
 */
sealed class ConversionResult {

    data class Success(
        val baseCode: String,
        val targetCode: String,
        val baseName: String,
        val targetName: String,
        val amount: BigDecimal,
        val exchangeRate: Double,
        val resultAmount: BigDecimal
    ): ConversionResult()

    data object Empty: ConversionResult()
}