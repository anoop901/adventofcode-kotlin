package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.*
import kotlin.math.abs

class Day21 : PuzzleSolution(
    inputFilePath = "input/2023/day21/input.txt",
    exampleInput = """
    """.trimIndent(),
) {
    private val grid = Grid.parse(inputLines) { it != '#' }
    private val startingCoordinates = findStartingCoordinates()

    private fun findStartingCoordinates(): Coordinates {
        val charGrid = Grid.parse(inputLines)
        for (rowIndex in 0..<charGrid.height) {
            for ((coordinates, char) in charGrid.rowWithCoordinates(rowIndex)) {
                if (char == 'S') {
                    return coordinates
                }
            }
        }
        throw IllegalArgumentException()
    }

    private fun neighbors(coordinates: Coordinates): List<Coordinates> {
        return coordinates.neighbors().filter { grid.inBounds(it) }.filter { grid[it] }.toList()
    }

    override fun part1(): Number {
        return generateSequence(setOf(startingCoordinates)) { possibleLocations ->
            val nextPossibleLocations =
                possibleLocations.flatMap { possibleLocation -> neighbors(possibleLocation) }.toSet()
            nextPossibleLocations
        }.elementAt(64).size
    }

    override fun part2(): Number {
        println("${grid.width}x${grid.height}")
        println(shortestPathLength(Coordinates(65, 65), Coordinates(130, 65), this::neighbors))
        println(shortestPathLength(Coordinates(65, 65), Coordinates(65, 130), this::neighbors))
        println(shortestPathLength(Coordinates(65, 65), Coordinates(0, 65), this::neighbors))
        println(shortestPathLength(Coordinates(65, 65), Coordinates(65, 0), this::neighbors))

        println(shortestPathLength(Coordinates(66, 65), Coordinates(66, 0), this::neighbors))
        println(shortestPathLength(Coordinates(66, 65), Coordinates(66, 130), this::neighbors))

        val lengthTo = shortestPathLengths(startingCoordinates, this::neighbors)
        for (x in 0..<grid.width) {
            for (y in 0..<grid.height) {
                val lengthToCurrent = lengthTo[Coordinates(x, y)]
                print((lengthToCurrent?.toString() ?: "#").padStart(4))
            }
            println()
        }

        val mismatch = (sequence {
            for (x in 0..<grid.width) {
                for (y in 0..<grid.width) {
                    yield(Coordinates(x, y))
                }
            }
        }.first { grid[it] && lengthTo[it] != abs(startingCoordinates.x - it.x) + abs(startingCoordinates.y - it.y) })
        println(mismatch)
        println(grid[mismatch])
        println(mismatch.let {  })
        return 0
    }
}

fun main() {
    val solution = Day21()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
