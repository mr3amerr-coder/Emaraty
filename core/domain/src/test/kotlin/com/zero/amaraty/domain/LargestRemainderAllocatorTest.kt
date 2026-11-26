package com.zero.amaraty.domain

import com.zero.amaraty.domain.allocation.AllocationTarget
import com.zero.amaraty.domain.allocation.LargestRemainderAllocator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LargestRemainderAllocatorTest {
    @Test
    fun matchesReadableGoldenVectors() {
        assertEquals(
            listOf(33_334L, 33_333L, 33_333L),
            allocate(100_000L, listOf(1L, 1L, 1L)),
        )
        assertEquals(
            listOf(2_500L, 2_500L, 5_000L),
            allocate(10_000L, listOf(1L, 1L, 2L)),
        )
        assertEquals(
            listOf(334L, 334L, 333L),
            allocate(1_001L, listOf(1L, 1L, 1L)),
        )
    }

    @Test
    fun tiesGoToLowestUnitSortOrderRegardlessOfInputOrder() {
        val result = LargestRemainderAllocator.allocate(
            totalPiasters = 2L,
            targets = listOf(
                AllocationTarget(unitId = "B", sortOrder = 20, weight = 1L),
                AllocationTarget(unitId = "A", sortOrder = 10, weight = 1L),
            ),
        )
        assertEquals(listOf("A", "B"), result.map { it.unitId })
        assertEquals(listOf(1L, 1L), result.map { it.amountPiasters })
    }

    @Test
    fun generatedCasesAlwaysSumExactlyAndAreDeterministic() {
        for (total in 0L..500L) {
            for (unitCount in 1..8) {
                val targets = (0 until unitCount).map { index ->
                    AllocationTarget(
                        unitId = "U$index",
                        sortOrder = index,
                        weight = (index % 5 + 1).toLong(),
                    )
                }.reversed()
                val first = LargestRemainderAllocator.allocate(total, targets)
                val second = LargestRemainderAllocator.allocate(total, targets)
                assertEquals(total, first.sumOf { it.amountPiasters })
                assertEquals(first, second)
            }
        }
    }

    @Test
    fun rejectsInvalidAllocationInputs() {
        assertThrows(IllegalArgumentException::class.java) {
            LargestRemainderAllocator.allocate(-1L, listOf(AllocationTarget("U1", 1, 1L)))
        }
        assertThrows(IllegalArgumentException::class.java) {
            LargestRemainderAllocator.allocate(10L, emptyList())
        }
        assertThrows(IllegalArgumentException::class.java) {
            LargestRemainderAllocator.allocate(10L, listOf(AllocationTarget("U1", 1, 0L)))
        }
        assertThrows(IllegalArgumentException::class.java) {
            LargestRemainderAllocator.allocate(
                10L,
                listOf(
                    AllocationTarget("U1", 1, 1L),
                    AllocationTarget("U1", 2, 1L),
                ),
            )
        }
    }

    private fun allocate(total: Long, weights: List<Long>): List<Long> =
        LargestRemainderAllocator.allocate(
            totalPiasters = total,
            targets = weights.mapIndexed { index, weight ->
                AllocationTarget("U$index", index, weight)
            },
        ).map { it.amountPiasters }
}
