package com.zero.amaraty.domain.money

import java.math.BigInteger

object MoneyFormatter {
    fun format(
        money: Money,
        useArabicDigits: Boolean = false,
        currencyLabel: String = "ج.م",
    ): String {
        val raw = BigInteger.valueOf(money.piasters)
        val negative = raw.signum() < 0
        val absolute = raw.abs()
        val parts = absolute.divideAndRemainder(BigInteger.valueOf(100L))
        val whole = groupThousands(parts[0].toString())
        val fraction = parts[1].toInt().toString().padStart(2, '0')
        val amount = buildString {
            if (negative) append('-')
            append(whole)
            if (parts[1].signum() != 0) {
                append('.')
                append(fraction)
            }
        }
        val renderedAmount = if (useArabicDigits) toArabicDigits(amount) else amount
        return "$renderedAmount $currencyLabel"
    }

    private fun groupThousands(value: String): String = buildString {
        value.forEachIndexed { index, char ->
            if (index > 0 && (value.length - index) % 3 == 0) append(',')
            append(char)
        }
    }

    private fun toArabicDigits(value: String): String = buildString(value.length) {
        value.forEach { char ->
            append(when (char) {
                in '0'..'9' -> ('٠'.code + (char.code - '0'.code)).toChar()
                ',' -> '٬'
                '.' -> '٫'
                else -> char
            })
        }
    }
}
