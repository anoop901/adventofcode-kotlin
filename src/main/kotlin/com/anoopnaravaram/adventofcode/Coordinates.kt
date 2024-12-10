package com.anoopnaravaram.adventofcode

data class Coordinates(val x: Int, val y: Int) {
    fun offset(offset: Coordinates): Coordinates {
        return Coordinates(x + offset.x, y + offset.y)
    }

    operator fun plus(that: Coordinates): Coordinates {
        return Coordinates(x + that.x, y + that.y)
    }

    operator fun minus(that: Coordinates): Coordinates {
        return Coordinates(x - that.x, y - that.y)
    }

    fun vectorTo(that: Coordinates): Coordinates {
        return that - this
    }

    operator fun times(factor: Int): Coordinates {
        return Coordinates(x * factor, y * factor)
    }

    fun neighbors(): Sequence<Coordinates> {
        return Direction.cardinals.asSequence().map { this + it.offset }
    }

    fun neighborsWithDiagonals(): Sequence<Coordinates> {
        return Direction.entries.asSequence().map { this + it.offset }
    }
}