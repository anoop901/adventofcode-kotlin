package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.Grid
import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day10 : PuzzleSolution(
    inputFilePath = "input/2024/day10/input.txt",
    exampleInput = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent(),
    useInputFile = true
) {
    private val topographicMap = Grid.parse(inputLines) { it.digitToInt() }

    private fun trailheadScore(trailhead: Coordinates): Int {
        val stack = ArrayDeque<Coordinates>()
        val trailEnds = mutableSetOf<Coordinates>()
        if (topographicMap[trailhead] == 0) stack.addLast(trailhead)
        while (stack.isNotEmpty()) {
            val coords = stack.removeLast()
            val height = topographicMap[coords]
            if (height == 9) {
                trailEnds.add(coords)
            } else {
                coords.neighbors()
                    .filter { topographicMap.inBounds(it) && topographicMap[it] == height + 1 }
                    .forEach { stack.addLast(it) }
            }
        }
        return trailEnds.size
    }

    override fun part1(): Number {
        return topographicMap.allCoordinates().sumOf { trailheadScore(it) }
    }

    private fun trailheadRating(trailhead: Coordinates): Int {
        val stack = ArrayDeque<Coordinates>()
        var rating = 0
        if (topographicMap[trailhead] == 0) stack.addLast(trailhead)
        while (stack.isNotEmpty()) {
            val coords = stack.removeLast()
            val height = topographicMap[coords]
            if (height == 9) {
                rating++
            } else {
                coords.neighbors()
                    .filter { topographicMap.inBounds(it) && topographicMap[it] == height + 1 }
                    .forEach { stack.addLast(it) }
            }
        }
        return rating
    }

    override fun part2(): Number {
        return topographicMap.allCoordinates().sumOf { trailheadRating(it) }
    }
}

fun main() {
    val solution = Day10()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
