package com.zero.amaraty.domain.money

/** Exact monetary amount stored as integer piasters. */
@JvmInline
value class Money private constructor(val piasters: Long) : Comparable<Money> {
    operator fun plus(other: Money): Money = fromPiasters(Math.addExact(piasters, other.piasters))

    operator fun minus(other: Money): Money = fromPiasters(Math.subtractExact(piasters, other.piasters))

    override fun compareTo(other: Money): Int = piasters.compareTo(other.piasters)

    companion object {
        val ZERO: Money = Money(0L)

        fun fromPiasters(piasters: Long): Money = Money(piasters)
    }
}
