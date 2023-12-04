package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution

private data class NumberInSchematic(val value: Int, val length: Int, val leftEndLocation: Coordinates) {
    val rightEndLocation = Coordinates(leftEndLocation.x + length - 1, leftEndLocation.y)
}

class Day3 : PuzzleSolution(
    inputFilePath = "input/2023/day3/input.txt",
    exampleInput = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...$.*....
        .664.598..
    """.trimIndent(),
) {
    private val grid = input.trimEnd().lines().map { it.toList() }

    private val width = grid[0].size
    private val height = grid.size

    private val numbersInSchematic = sequence {
        for (y in 0..<height) {
            var leftEndX: Int? = null
            var value = 0
            for (x in 0..<width) {
                val c = grid[y][x]
                if (c.isDigit()) {
                    if (leftEndX == null) {
                        leftEndX = x
                    }
                    value = value * 10 + c.digitToInt()
                }
                if (leftEndX != null && !c.isDigit()) {
                    yield(NumberInSchematic(value, x - leftEndX, Coordinates(leftEndX, y)))
                    leftEndX = null
                    value = 0
                }
            }
            if (leftEndX != null) {
                yield(NumberInSchematic(value, width - leftEndX, Coordinates(leftEndX, y)))
            }
        }
    }.toList()

    private fun coordinatesAroundNumber(numberInSchematic: NumberInSchematic) = sequence {
        val (leftX, y) = numberInSchematic.leftEndLocation
        val (rightX, _) = numberInSchematic.rightEndLocation
        yield(Coordinates(leftX - 1, y))
        yield(Coordinates(rightX + 1, y))
        for (x in leftX - 1..rightX + 1) {
            yield(Coordinates(x, y - 1))
            yield(Coordinates(x, y + 1))
        }
    }.filter { (x, y) -> x in 0..<width && y in 0..<height }

    private fun charAtCoordinates(coordinates: Coordinates) = grid[coordinates.y][coordinates.x]

    override fun part1(): Number {
        return numbersInSchematic
            .filter { n -> coordinatesAroundNumber(n).any { charAtCoordinates(it).isSymbol() } }
            .sumOf { it.value }
    }

    private val starsToAdjacentNumbers = mutableMapOf<Coordinates, MutableList<NumberInSchematic>>()

    init {
        for (numberInSchematic in numbersInSchematic) {
            for (coordinates in coordinatesAroundNumber(numberInSchematic)) {
                if (charAtCoordinates(coordinates) == '*') {
                    starsToAdjacentNumbers.getOrPut(coordinates) { mutableListOf() }.add(numberInSchematic)
                }
            }
        }
    }

    override fun part2(): Number {
        return starsToAdjacentNumbers.entries.filter { it.value.size == 2 }
            .sumOf { (_, values) -> values.map { it.value }.reduce { a, b -> a * b } }
    }
}

private fun Char.isSymbol(): Boolean {
    return !isDigit() && this != '.'
}
