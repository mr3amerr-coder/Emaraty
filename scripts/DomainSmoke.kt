import com.zero.amaraty.domain.allocation.AllocationTarget
import com.zero.amaraty.domain.allocation.LargestRemainderAllocator
import com.zero.amaraty.domain.money.MoneyFormatter
import com.zero.amaraty.domain.money.MoneyInputError
import com.zero.amaraty.domain.money.MoneyParseResult
import com.zero.amaraty.domain.money.MoneyParser

fun main() {
    val samples = mapOf(
        "1234.56" to 123_456L,
        "١٬٢٣٤٫٥٦" to 123_456L,
        "1,234.56" to 123_456L,
        "1.234,56" to 123_456L,
        "١٢٣٤" to 123_400L,
        "0.1" to 10L,
        "٠٫٠٥" to 5L,
    )
    samples.forEach { (input, expected) ->
        val parsed = MoneyParser.parse(input)
        check(parsed is MoneyParseResult.Success) { "$input => $parsed" }
        check(parsed.money.piasters == expected) { "$input => ${parsed.money.piasters}; expected $expected" }
    }

    val invalidCases = mapOf(
        "12,345" to MoneyInputError.AMBIGUOUS_SEPARATOR,
        "1,23,4" to MoneyInputError.INVALID_GROUPING,
        "-1.00" to MoneyInputError.NEGATIVE_NOT_ALLOWED,
        "12.3456" to MoneyInputError.TOO_MANY_DECIMALS,
        "999999999999999999999999.99" to MoneyInputError.OVERFLOW,
        "١٢٣ جنيه" to MoneyInputError.INVALID_CHARACTER,
    )
    invalidCases.forEach { (input, expected) ->
        val parsed = MoneyParser.parse(input)
        check(parsed == MoneyParseResult.Failure(expected)) { "$input => $parsed; expected $expected" }
    }

    val amount = (MoneyParser.parse("1234.56") as MoneyParseResult.Success).money
    check(MoneyFormatter.format(amount) == "1,234.56 ج.م")
    check(MoneyFormatter.format(amount, useArabicDigits = true) == "١٬٢٣٤٫٥٦ ج.م")

    val goldenVectors = listOf(
        Triple(100_000L, listOf(1L, 1L, 1L), listOf(33_334L, 33_333L, 33_333L)),
        Triple(10_000L, listOf(1L, 1L, 2L), listOf(2_500L, 2_500L, 5_000L)),
        Triple(1_001L, listOf(1L, 1L, 1L), listOf(334L, 334L, 333L)),
    )
    goldenVectors.forEach { (total, weights, expectedShares) ->
        val shares = LargestRemainderAllocator.allocate(total, weights.mapIndexed { index, weight ->
            AllocationTarget("U$index", index, weight)
        }).map { it.amountPiasters }
        check(shares == expectedShares) { "Allocation $total => $shares; expected $expectedShares" }
    }

    var generatedCases = 0
    for (total in 0L..500L) {
        for (count in 1..8) {
            val targets = (0 until count).map { index ->
                AllocationTarget("U$index", index, (index % 5 + 1).toLong())
            }.reversed()
            val first = LargestRemainderAllocator.allocate(total, targets)
            val second = LargestRemainderAllocator.allocate(total, targets)
            check(first.sumOf { it.amountPiasters } == total)
            check(first == second)
            generatedCases += 1
        }
    }

    println("DOMAIN_SMOKE_OK: ${samples.size} valid inputs, ${invalidCases.size} invalid inputs, 2 formats, 3 golden vectors, $generatedCases generated allocation cases")
}
