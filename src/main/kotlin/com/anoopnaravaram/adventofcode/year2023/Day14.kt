package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution

private enum class DishCell(val char: Char) {
    EMPTY('.'),
    CUBE_ROCK('#'),
    ROUND_ROCK('O');

    companion object {
        val map = entries.associateBy { it.char }
        fun fromChar(char: Char) = requireNotNull(map[char])
    }
}

private data class Dish(val grid: List<List<DishCell>>) {
    val height = grid.size

    init {
        require(height > 0)
    }

    val width = grid[0].size

    init {
        require(grid.all { it.size == width })
    }

    fun tiltNorth(): Dish {
        val newGrid = List(height) { MutableList(width) { DishCell.EMPTY } }
        for (columnIndex in 0..<width) {
            var newRowIndex = 0
            for (rowIndex in 0..<height) {
                val cell = grid[rowIndex][columnIndex]
                if (cell == DishCell.CUBE_ROCK) {
                    newGrid[rowIndex][columnIndex] = DishCell.CUBE_ROCK
                    newRowIndex = rowIndex + 1
                }
                if (cell == DishCell.ROUND_ROCK) {
                    newGrid[newRowIndex][columnIndex] = DishCell.ROUND_ROCK
                    newRowIndex++
                }
            }
        }
        return Dish(newGrid)
    }

    fun totalLoadOnNorthSupportBeams() = sequence {
        for (i in 0..<height) {
            for (j in 0..<width) {
                if (grid[i][j] == DishCell.ROUND_ROCK) {
                    yield(height - i)
                }
            }
        }
    }.sum()

    private fun getColumn(index: Int) = grid.map { it[index] }

    fun rotateRight(): Dish {
        return Dish((0..<width).map { getColumn(it).reversed() })
    }
}

class Day14 : PuzzleSolution(
    inputFilePath = "input/2023/day14/input.txt",
    exampleInput = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent(),
) {
    private val dish = Dish(inputLines.map { line -> line.map { DishCell.fromChar(it) } })
    override fun part1(): Number {
        return dish.tiltNorth().totalLoadOnNorthSupportBeams()
    }

    override fun part2(): Number {
        var currentDish = dish
        var i = 0
        val previousStates = mutableMapOf<Dish, Int>()
        val totalLoadsHistory = mutableListOf(currentDish.totalLoadOnNorthSupportBeams())
        while (true) {
            repeat(4) {
                currentDish = currentDish.tiltNorth().rotateRight()
            }
            i++

            previousStates[currentDish]?.let { j ->
                val cycleLength = i - j
                val target = 1000000000
                val indexEquivalentToTarget = ((target - j) % cycleLength) + j
                return totalLoadsHistory[indexEquivalentToTarget]
            }
            previousStates[currentDish] = i
            totalLoadsHistory.add(currentDish.totalLoadOnNorthSupportBeams())
        }
    }
}