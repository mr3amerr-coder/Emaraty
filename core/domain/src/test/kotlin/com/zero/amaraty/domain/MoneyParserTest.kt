package com.zero.amaraty.domain

import com.zero.amaraty.domain.money.Money
import com.zero.amaraty.domain.money.MoneyFormatter
import com.zero.amaraty.domain.money.MoneyInputError
import com.zero.amaraty.domain.money.MoneyParseResult
import com.zero.amaraty.domain.money.MoneyParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class MoneyParserTest {
    @Test
    fun parsesWesternAndArabicDigitsExactly() {
        assertMoney("1234.56", 123_456L)
        assertMoney("١٬٢٣٤٫٥٦", 123_456L)
        assertMoney("1,234.56", 123_456L)
        assertMoney("1.234,56", 123_456L)
        assertMoney("١٢٣٤", 123_400L)
        assertMoney("0.1", 10L)
        assertMoney("٠٫٠٥", 5L)
    }

    @Test
    fun rejectsAmbiguousSeparatorsAndMalformedGroups() {
        assertFailure("12,345", MoneyInputError.AMBIGUOUS_SEPARATOR)
        assertFailure("1,23,4", MoneyInputError.INVALID_GROUPING)
        assertFailure("1.234.56", MoneyInputError.INVALID_GROUPING)
    }

    @Test
    fun rejectsNegativeUnsupportedPrecisionAndOverflow() {
        assertFailure("-1.00", MoneyInputError.NEGATIVE_NOT_ALLOWED)
        assertFailure("12.3456", MoneyInputError.TOO_MANY_DECIMALS)
        assertFailure("999999999999999999999999.99", MoneyInputError.OVERFLOW)
        assertFailure("١٢٣ جنيه", MoneyInputError.INVALID_CHARACTER)
    }

    @Test
    fun formatsWithoutShowingZeroPiasters() {
        assertEquals("1,234 ج.م", MoneyFormatter.format((MoneyParser.parse("1234") as MoneyParseResult.Success).money))
        assertEquals("1,234.56 ج.م", MoneyFormatter.format((MoneyParser.parse("1234.56") as MoneyParseResult.Success).money))
        assertEquals("١٬٢٣٤٫٥٦ ج.م", MoneyFormatter.format((MoneyParser.parse("1234.56") as MoneyParseResult.Success).money, useArabicDigits = true))
    }

    @Test
    fun arithmeticFailsOnOverflowInsteadOfWrapping() {
        assertThrows(ArithmeticException::class.java) {
            Money.fromPiasters(Long.MAX_VALUE) + Money.fromPiasters(1L)
        }
        assertThrows(ArithmeticException::class.java) {
            Money.fromPiasters(Long.MIN_VALUE) - Money.fromPiasters(1L)
        }
    }

    private fun assertMoney(input: String, expectedPiasters: Long) {
        val result = MoneyParser.parse(input)
        assertTrue("Expected successful parse for $input but received $result", result is MoneyParseResult.Success)
        assertEquals(expectedPiasters, (result as MoneyParseResult.Success).money.piasters)
    }

    private fun assertFailure(input: String, expected: MoneyInputError) {
        val result = MoneyParser.parse(input)
        assertTrue("Expected failure for $input but received $result", result is MoneyParseResult.Failure)
        assertEquals(expected, (result as MoneyParseResult.Failure).reason)
    }

}
