package com.zhukovskii.currencyconverter.view.util

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Class used to format large currencies
 */
object LargeCurrencyFormatter {

    private val QUADRILLION = BigDecimal(1_000_000_000_000_000)
    private val TRILLION = BigDecimal(1_000_000_000_000)
    private val BILLION = BigDecimal(1_000_000_000)
    private val MILLION = BigDecimal(1_000_000)

    private fun BigDecimal.divideAndScale(other: BigDecimal): BigDecimal {
        return setScale(6, RoundingMode.HALF_UP) / other
    }

    /**
     * Formats the amount leaving two decimal places if it is less than a million
     *
     * Otherwise, converts it to one of the commonly used representations with a
     * corresponding letter suffix (i.e. M for million and so on) leaving six
     * decimal places
     */
    fun format(amount: BigDecimal): String {

        if (amount >= QUADRILLION)
            return "${amount.divideAndScale(QUADRILLION)}Q"
        if (amount >= TRILLION)
            return "${amount.divideAndScale(TRILLION)}T"
        if (amount >= BILLION)
            return "${amount.divideAndScale(BILLION)}B"
        if (amount >= MILLION)
            return "${amount.divideAndScale(MILLION)}M"

        return "${amount.setScale(2, RoundingMode.HALF_UP)}"
    }
}