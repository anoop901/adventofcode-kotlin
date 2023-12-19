package com.anoopnaravaram.adventofcode

enum class Direction(val offset: Coordinates) {
    NORTH(Coordinates(0, -1)),
    SOUTH(Coordinates(0, 1)),
    EAST(Coordinates(1, 0)),
    WEST(Coordinates(-1, 0));

    val opposite: Direction
        get() = when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }

    val turnLeft: Direction
        get() = when (this) {
            NORTH -> WEST
            WEST -> SOUTH
            SOUTH -> EAST
            EAST -> NORTH
        }

    val turnRight: Direction
        get() = when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
}
