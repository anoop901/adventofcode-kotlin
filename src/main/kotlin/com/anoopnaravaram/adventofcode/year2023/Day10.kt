package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution

private enum class PipeDirection(val dx: Int, val dy: Int) {
    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(1, 0),
    WEST(-1, 0);

    val opposite: PipeDirection
        get() = when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }
}

private enum class PipeType(val char: Char, val connectedDirections: List<PipeDirection>) {
    NORTH_SOUTH('|', listOf(PipeDirection.NORTH, PipeDirection.SOUTH)),
    EAST_WEST('-', listOf(PipeDirection.EAST, PipeDirection.WEST)),
    NORTH_EAST('L', listOf(PipeDirection.NORTH, PipeDirection.EAST)),
    NORTH_WEST('J', listOf(PipeDirection.NORTH, PipeDirection.WEST)),
    SOUTH_WEST('7', listOf(PipeDirection.SOUTH, PipeDirection.WEST)),
    SOUTH_EAST('F', listOf(PipeDirection.SOUTH, PipeDirection.EAST)),
    GROUND('.', listOf()),
    START('S', listOf());

    companion object {
        private val map = entries.associateBy { it.char }
        fun fromChar(char: Char) = requireNotNull(map[char])
    }
}

private fun Coordinates.adjacentInDirection(direction: PipeDirection) =
    Coordinates(this.x + direction.dx, this.y + direction.dy)

class Day10 : PuzzleSolution(
    inputFilePath = "input/2023/day10/input.txt",
    exampleInput = """
    """.trimIndent(),
) {
    private val pipes = inputLines.map { line -> line.map { PipeType.fromChar(it) } }
    private fun pipeTypeAtCoordinates(coordinates: Coordinates) = pipes[coordinates.y][coordinates.x]

    private val width = pipes[0].size
    private val height = pipes.size

    private fun allCoordinates() = sequence {
        for (y in 0..<height) {
            for (x in 0..<width) {
                yield(Coordinates(x, y))
            }
        }
    }

    private val startCoordinates = requireNotNull(allCoordinates().find { pipeTypeAtCoordinates(it) == PipeType.START })
    private val startCoordinates2 = Coordinates(startCoordinates.x + 1, startCoordinates.y)
    private val path = generateSequence<Pair<Coordinates, PipeDirection?>>(
        Pair(
            startCoordinates2,
            PipeDirection.EAST
        )
    ) { (coordinates, direction) ->
        if (direction == null) {
            return@generateSequence null
        }
        val nextCoordinates = coordinates.adjacentInDirection(direction)
        val nextPipeType = pipeTypeAtCoordinates(nextCoordinates)
        val nextDirection = if (nextPipeType == PipeType.START) {
            null
        } else {
            pipeTypeAtCoordinates(nextCoordinates).connectedDirections.filter { it != direction.opposite }.first()
        }
        Pair(nextCoordinates, nextDirection)
    }.toList()

    override fun part1(): Number {
        return path.count() / 2
    }

    override fun part2(): Number {
        val pathCoordinates = path.map { it.first }.toSet()
        var count = 0
        for (y in 0..<height) {
            var inside = false
            for (x in 0..<width) {
                val coordinates = Coordinates(x, y)
                if (pathCoordinates.contains(coordinates)) {
                    if (setOf(PipeType.NORTH_SOUTH, PipeType.NORTH_WEST, PipeType.NORTH_EAST).contains(pipeTypeAtCoordinates(coordinates))) {
                        inside = !inside
                    }
                } else {
                    if (inside) {
                        count++
                    }
                }
            }
        }
        return count
    }
}