package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.Direction
import com.anoopnaravaram.adventofcode.Grid
import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day4 : PuzzleSolution(
    inputFilePath = "input/2024/day4/input.txt",
    exampleInput = """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
    """.trimIndent(),
) {
    private var grid = Grid.parse(input.trim())

    private fun findAllSlices(): Sequence<List<Char>> = sequence {
        for (y in 0..<grid.height) {
            yield(grid.row(y).toList())
        }
        for (x in 0..<grid.width) {
            yield(grid.column(x).toList())
        }
        for (a in grid.allUpwardDiagonalPositions()) {
            yield(grid.upwardDiagonal(a).toList())
        }
        for (a in grid.allDownwardDiagonalPositions()) {
            yield(grid.downwardDiagonal(a).toList())
        }
    }

    private val xmas = "XMAS".toList()

    override fun part1(): Number {
        return findAllSlices()
            .flatMap { it.subListsOfSize(xmas.size) }
            .count { it == xmas || it.reversed() == xmas }
    }

    private val mas = "MAS".toList()

    private fun possibleXMasCenters() = sequence {
        for (x in 1..<grid.width - 1) {
            for (y in 1..<grid.height - 1) {
                yield(Coordinates(x, y))
            }
        }
    }

    private fun isXMasAtCoordinates(center: Coordinates): Boolean {
        val cornerNW = center + Direction.NORTHWEST.offset
        val cornerNE = center + Direction.NORTHEAST.offset
        val cornerSW = center + Direction.SOUTHWEST.offset
        val cornerSE = center + Direction.SOUTHEAST.offset
        val diagonalLine1Coordinates = listOf(cornerNW, center, cornerSE)
        val diagonalLine2Coordinates = listOf(cornerSW, center, cornerNE)
        return sequenceOf(diagonalLine1Coordinates, diagonalLine2Coordinates).all { lineCoordinates ->
            val line = lineCoordinates.map { grid[it] }
            line == mas || line.reversed() == mas
        }
    }

    override fun part2(): Number {
        return possibleXMasCenters().count { isXMasAtCoordinates(it) }
    }
}

fun main() {
    val solution = Day4()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}