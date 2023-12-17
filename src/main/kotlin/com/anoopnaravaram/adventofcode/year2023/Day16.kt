package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution

private val DIRECTION_LEFT = Coordinates(-1, 0)
private val DIRECTION_RIGHT = Coordinates(1, 0)
private val DIRECTION_UP = Coordinates(0, -1)
private val DIRECTION_DOWN = Coordinates(0, 1)

private fun Coordinates.move(direction: Coordinates): Coordinates {
    val (dx, dy) = direction
    return Coordinates(x + dx, y + dy)
}

private data class Beam(val position: Coordinates, val direction: Coordinates) {
}

class Day16 : PuzzleSolution(
    inputFilePath = "input/2023/day16/input.txt",
    exampleInput = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent(),
) {
    private val height = inputLines.size
    private val width = inputLines[0].length
    private fun getCellAtCoordinates(coordinates: Coordinates) = inputLines[coordinates.y][coordinates.x]

    private fun advanceBeam(beam: Beam): List<Beam> {
        val direction = beam.direction
        val newDirections = when (getCellAtCoordinates(beam.position)) {
            '/' -> {
                val newDirection = when (direction) {
                    DIRECTION_RIGHT -> DIRECTION_UP
                    DIRECTION_UP -> DIRECTION_RIGHT
                    DIRECTION_DOWN -> DIRECTION_LEFT
                    DIRECTION_LEFT -> DIRECTION_DOWN
                    else -> direction
                }
                listOf(newDirection)
            }

            '\\' -> {
                val newDirection = when (direction) {
                    DIRECTION_RIGHT -> DIRECTION_DOWN
                    DIRECTION_DOWN -> DIRECTION_RIGHT
                    DIRECTION_UP -> DIRECTION_LEFT
                    DIRECTION_LEFT -> DIRECTION_UP
                    else -> direction
                }
                listOf(newDirection)
            }

            '|' -> when (direction) {
                DIRECTION_RIGHT, DIRECTION_LEFT -> listOf(DIRECTION_UP, DIRECTION_DOWN)
                else -> listOf(direction)
            }

            '-' -> when (direction) {
                DIRECTION_UP, DIRECTION_DOWN -> listOf(DIRECTION_LEFT, DIRECTION_RIGHT)
                else -> listOf(direction)
            }

            else -> listOf(direction)
        }
        return newDirections.map { Beam(beam.position.move(it), it) }
    }

    private fun countEnergizedTilesWithStartingBeam(beam: Beam): Int {
        var currentBeams = listOf(beam)
        val energizedTiles = mutableSetOf<Coordinates>()
        val previousBeams = mutableSetOf<Beam>()
        while (currentBeams.isNotEmpty()) {
            currentBeams.forEach {
                energizedTiles.add(it.position)
                previousBeams.add(it)
            }
            currentBeams = currentBeams
                .flatMap { advanceBeam(it) }
                .filter { it.position.x in 0..<width && it.position.y in 0..<height }
                .filter { !previousBeams.contains(it) }
        }
        return energizedTiles.size

    }

    override fun part1(): Number {
        return countEnergizedTilesWithStartingBeam(Beam(Coordinates(0, 0), DIRECTION_RIGHT))
    }

    override fun part2(): Number {
        return sequence {
            for (y in 0..<height) {
                yield(Beam(Coordinates(0, y), DIRECTION_RIGHT))
                yield(Beam(Coordinates(width - 1, y), DIRECTION_LEFT))
            }
            for (x in 0..<width) {
                yield(Beam(Coordinates(x, 0), DIRECTION_DOWN))
                yield(Beam(Coordinates(x, height - 1), DIRECTION_UP))
            }
        }.maxOf { countEnergizedTilesWithStartingBeam(it) }
    }
}