package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.Direction
import com.anoopnaravaram.adventofcode.Grid
import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day6 : PuzzleSolution(
    inputFilePath = "input/2024/day6/input.txt",
    exampleInput = """
        ....#.....
        .........#
        ..........
        ..#.......
        .......#..
        ..........
        .#..^.....
        ........#.
        #.........
        ......#...
    """.trimIndent(),
    useInputFile = true
) {
    private val grid = Grid.parse(input.trim())
    private val startingCoordinates = requireNotNull(grid.allCoordinates().find { grid[it] == '^' })

    private fun generateGuardPath(grid: Grid<Char>): Sequence<Pair<Coordinates, Direction>> =
        generateSequence(Pair(startingCoordinates, Direction.NORTH)) { (position, direction) ->
            val nextPosition = position + direction.offset
            if (!grid.inBounds(nextPosition)) {
                null
            } else if (grid[nextPosition] == '#') {
                Pair(position, direction.turnRight)
            } else {
                Pair(nextPosition, direction)
            }
        }

    override fun part1(): Number {
        return generateGuardPath(grid).map { it.first }.toSet().count()
    }

    private fun addObstructionToGrid(grid: Grid<Char>, obstructionCoordinates: Coordinates): Grid<Char> {
        val mutableCells = grid.cells.toMutableList()
        val mutableRow = mutableCells[obstructionCoordinates.y].toMutableList()
        mutableRow[obstructionCoordinates.x] = '#'
        mutableCells[obstructionCoordinates.y] = mutableRow
        return Grid(mutableCells)
    }

    private fun guardPathGetsStuckInLoop(guardPath: Sequence<Pair<Coordinates, Direction>>): Boolean {
        val visitedPathSegments = mutableSetOf<Pair<Coordinates, Direction>>()
        for (pathSegment in guardPath) {
            if (!visitedPathSegments.add(pathSegment)) {
                return true
            }
        }
        return false
    }

    override fun part2(): Number {
        val possibleObstructionCoordinates = generateGuardPath(grid).map { it.first }.toSet() - startingCoordinates
        return possibleObstructionCoordinates.count {
            val modifiedGrid = addObstructionToGrid(grid, it)
            val guardPath = generateGuardPath(modifiedGrid)
            guardPathGetsStuckInLoop(guardPath)
        }
    }
}

fun main() {
    val solution = Day6()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}