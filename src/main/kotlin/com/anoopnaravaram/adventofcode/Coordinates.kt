package com.anoopnaravaram.adventofcode

data class Coordinates(val x: Int, val y: Int) {
    fun offset(offset: Coordinates): Coordinates {
        return Coordinates(x + offset.x, y + offset.y)
    }
}