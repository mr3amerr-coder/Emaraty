package com.zero.amaraty.domain.allocation

import java.math.BigInteger

/** A target's weight is an integer. Equal remainders are resolved by sortOrder, then id. */
data class AllocationTarget(
    val unitId: String,
    val sortOrder: Int,
    val weight: Long,
)

data class AllocationShare(
    val unitId: String,
    val amountPiasters: Long,
)

object LargestRemainderAllocator {
    fun allocate(totalPiasters: Long, targets: List<AllocationTarget>): List<AllocationShare> {
        require(totalPiasters >= 0L) { "Total must not be negative" }
        require(targets.isNotEmpty()) { "At least one target is required" }
        require(targets.map(AllocationTarget::unitId).distinct().size == targets.size) {
            "Unit identifiers must be unique"
        }
        require(targets.all { it.weight > 0L }) { "Every allocation weight must be positive" }

        val ordered = targets.sortedWith(compareBy<AllocationTarget> { it.sortOrder }.thenBy { it.unitId })
        val total = BigInteger.valueOf(totalPiasters)
        val totalWeight = ordered.fold(BigInteger.ZERO) { sum, target ->
            sum.add(BigInteger.valueOf(target.weight))
        }

        val baseByUnit = HashMap<String, Long>(ordered.size)
        val remainderByUnit = HashMap<String, BigInteger>(ordered.size)
        var allocated = BigInteger.ZERO

        ordered.forEach { target ->
            val numerator = total.multiply(BigInteger.valueOf(target.weight))
            val division = numerator.divideAndRemainder(totalWeight)
            val base = division[0].longValueExact()
            baseByUnit[target.unitId] = base
            remainderByUnit[target.unitId] = division[1]
            allocated = allocated.add(division[0])
        }

        val leftover = total.subtract(allocated).intValueExact()
        val remainderOrder = ordered.sortedWith(
            compareByDescending<AllocationTarget> { remainderByUnit.getValue(it.unitId) }
                .thenBy { it.sortOrder }
                .thenBy { it.unitId },
        )
        check(leftover in 0..remainderOrder.size) { "Largest-remainder invariant violated" }

        val extraUnitIds = remainderOrder.take(leftover).mapTo(HashSet()) { it.unitId }
        return ordered.map { target ->
            val extra = if (target.unitId in extraUnitIds) 1L else 0L
            AllocationShare(target.unitId, Math.addExact(baseByUnit.getValue(target.unitId), extra))
        }
    }
}
