package com.zero.amaraty.domain.money

import java.math.BigInteger

sealed interface MoneyParseResult {
    data class Success(val money: Money) : MoneyParseResult
    data class Failure(val reason: MoneyInputError) : MoneyParseResult
}

enum class MoneyInputError {
    EMPTY,
    INVALID_CHARACTER,
    NEGATIVE_NOT_ALLOWED,
    INVALID_GROUPING,
    AMBIGUOUS_SEPARATOR,
    TOO_MANY_DECIMALS,
    OVERFLOW,
}

/**
 * Parses EGP input without binary floating-point arithmetic.
 * Arabic separators are explicit; a lone comma/dot followed by three digits is rejected
 * because it can mean either a decimal fraction or a thousands group.
 */
object MoneyParser {
    private const val ARABIC_DECIMAL = '٫'
    private const val ARABIC_GROUP = '٬'

    fun parse(input: String): MoneyParseResult {
        val text = normalizeDigits(input.trim())
        if (text.isEmpty()) return MoneyParseResult.Failure(MoneyInputError.EMPTY)
        if (text.startsWith('-')) return MoneyParseResult.Failure(MoneyInputError.NEGATIVE_NOT_ALLOWED)
        if (text.any(Char::isWhitespace)) return MoneyParseResult.Failure(MoneyInputError.INVALID_CHARACTER)
        if (text.any { !it.isDigit() && it !in setOf(',', '.', ARABIC_DECIMAL, ARABIC_GROUP) }) {
            return MoneyParseResult.Failure(MoneyInputError.INVALID_CHARACTER)
        }

        val parsedParts = splitParts(text)
        if (parsedParts is PartsResult.Error) return MoneyParseResult.Failure(parsedParts.reason)
        val parts = (parsedParts as PartsResult.Valid).parts

        val integerDigits = if (parts.groupSeparator == null) {
            if (!parts.integerPart.all(Char::isDigit)) {
                return MoneyParseResult.Failure(MoneyInputError.INVALID_GROUPING)
            }
            parts.integerPart
        } else {
            val groups = parts.integerPart.split(parts.groupSeparator)
            if (!isValidGrouping(groups)) {
                return MoneyParseResult.Failure(MoneyInputError.INVALID_GROUPING)
            }
            groups.joinToString(separator = "")
        }

        if (integerDigits.isEmpty()) return MoneyParseResult.Failure(MoneyInputError.EMPTY)
        if (parts.fractionPart != null && parts.fractionPart.length > 2) {
            return MoneyParseResult.Failure(MoneyInputError.TOO_MANY_DECIMALS)
        }
        if (parts.fractionPart != null && !parts.fractionPart.all(Char::isDigit)) {
            return MoneyParseResult.Failure(MoneyInputError.INVALID_CHARACTER)
        }

        return try {
            val whole = BigInteger(integerDigits)
            val fraction = (parts.fractionPart ?: "").padEnd(2, '0').ifEmpty { "00" }
            val piasters = whole.multiply(BigInteger.valueOf(100L))
                .add(BigInteger(fraction))
            val longValue = piasters.longValueExact()
            MoneyParseResult.Success(Money.fromPiasters(longValue))
        } catch (_: NumberFormatException) {
            MoneyParseResult.Failure(MoneyInputError.INVALID_CHARACTER)
        } catch (_: ArithmeticException) {
            MoneyParseResult.Failure(MoneyInputError.OVERFLOW)
        }
    }

    private fun splitParts(text: String): PartsResult {
        val arabicDecimalCount = text.count { it == ARABIC_DECIMAL }
        val arabicGroupCount = text.count { it == ARABIC_GROUP }
        val asciiCommaCount = text.count { it == ',' }
        val asciiDotCount = text.count { it == '.' }

        if (arabicDecimalCount > 1) return PartsResult.Error(MoneyInputError.AMBIGUOUS_SEPARATOR)

        if (arabicDecimalCount == 1) {
            if (asciiCommaCount > 0 || asciiDotCount > 0) {
                return PartsResult.Error(MoneyInputError.AMBIGUOUS_SEPARATOR)
            }
            if (arabicGroupCount > 0 && text.substringBefore(ARABIC_DECIMAL).count { it == ARABIC_GROUP } == 0) {
                return PartsResult.Error(MoneyInputError.INVALID_GROUPING)
            }
            val halves = text.split(ARABIC_DECIMAL)
            if (halves.size != 2) return PartsResult.Error(MoneyInputError.AMBIGUOUS_SEPARATOR)
            val integer = halves[0]
            val fraction = halves[1]
            if (fraction.isEmpty()) return PartsResult.Error(MoneyInputError.TOO_MANY_DECIMALS)
            if (integer.contains(ARABIC_DECIMAL) || fraction.contains(ARABIC_GROUP)) {
                return PartsResult.Error(MoneyInputError.INVALID_GROUPING)
            }
            return PartsResult.Valid(Parts(integer, ARABIC_GROUP.takeIf { arabicGroupCount > 0 }, fraction))
        }

        if (arabicGroupCount > 0) {
            if (asciiCommaCount > 0 || asciiDotCount > 0) {
                return PartsResult.Error(MoneyInputError.AMBIGUOUS_SEPARATOR)
            }
            return PartsResult.Valid(Parts(text, ARABIC_GROUP, null))
        }

        if (asciiCommaCount > 0 && asciiDotCount > 0) {
            val decimalSeparator = if (text.lastIndexOf(',') > text.lastIndexOf('.')) ',' else '.'
            val groupSeparator = if (decimalSeparator == ',') '.' else ','
            if (text.count { it == decimalSeparator } != 1) {
                return PartsResult.Error(MoneyInputError.AMBIGUOUS_SEPARATOR)
            }
            val integer = text.substringBeforeLast(decimalSeparator)
            val fraction = text.substringAfterLast(decimalSeparator)
            if (fraction.isEmpty()) return PartsResult.Error(MoneyInputError.TOO_MANY_DECIMALS)
            return PartsResult.Valid(Parts(integer, groupSeparator, fraction))
        }

        val separator = when {
            asciiCommaCount > 0 -> ','
            asciiDotCount > 0 -> '.'
            else -> null
        } ?: return PartsResult.Valid(Parts(text, null, null))

        val count = if (separator == ',') asciiCommaCount else asciiDotCount
        if (count > 1) {
            return PartsResult.Valid(Parts(text, separator, null))
        }

        val integer = text.substringBefore(separator)
        val fraction = text.substringAfter(separator)
        if (integer.isEmpty() || fraction.isEmpty()) {
            return PartsResult.Error(MoneyInputError.INVALID_CHARACTER)
        }
        if (fraction.length == 3 && integer.length in 1..3) {
            return PartsResult.Error(MoneyInputError.AMBIGUOUS_SEPARATOR)
        }
        if (fraction.length > 2) return PartsResult.Error(MoneyInputError.TOO_MANY_DECIMALS)
        return PartsResult.Valid(Parts(integer, null, fraction))
    }

    private fun isValidGrouping(groups: List<String>): Boolean {
        if (groups.size < 2) return false
        if (groups.first().length !in 1..3 || !groups.first().all(Char::isDigit)) return false
        return groups.drop(1).all { it.length == 3 && it.all(Char::isDigit) }
    }

    private fun normalizeDigits(value: String): String = buildString(value.length) {
        value.forEach { char ->
            val normalized = when (char) {
                in '٠'..'٩' -> ('0'.code + (char.code - '٠'.code)).toChar()
                in '۰'..'۹' -> ('0'.code + (char.code - '۰'.code)).toChar()
                else -> char
            }
            append(normalized)
        }
    }

    private data class Parts(
        val integerPart: String,
        val groupSeparator: Char?,
        val fractionPart: String?,
    )

    private sealed interface PartsResult {
        data class Valid(val parts: Parts) : PartsResult
        data class Error(val reason: MoneyInputError) : PartsResult
    }
}
