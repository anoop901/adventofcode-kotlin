package com.anoopnaravaram.adventofcode

enum class Direction(val offset: Coordinates) {
    NORTH(Coordinates(0, -1)),
    SOUTH(Coordinates(0, 1)),
    EAST(Coordinates(1, 0)),
    WEST(Coordinates(-1, 0)),
    NORTHWEST(Coordinates(-1, -1)),
    NORTHEAST(Coordinates(1, -1)),
    SOUTHWEST(Coordinates(-1, 1)),
    SOUTHEAST(Coordinates(1, 1));

    val opposite: Direction
        get() = when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
            NORTHWEST -> SOUTHEAST
            NORTHEAST -> SOUTHWEST
            SOUTHWEST -> NORTHEAST
            SOUTHEAST -> NORTHWEST
        }

    val turnLeft: Direction
        get() = when (this) {
            NORTH -> WEST
            WEST -> SOUTH
            SOUTH -> EAST
            EAST -> NORTH
            NORTHWEST -> SOUTHWEST
            NORTHEAST -> NORTHWEST
            SOUTHWEST -> SOUTHEAST
            SOUTHEAST -> NORTHEAST
        }

    val turnRight: Direction
        get() = when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
            NORTHWEST -> NORTHEAST
            NORTHEAST -> SOUTHEAST
            SOUTHWEST -> NORTHWEST
            SOUTHEAST -> SOUTHWEST
        }
}
