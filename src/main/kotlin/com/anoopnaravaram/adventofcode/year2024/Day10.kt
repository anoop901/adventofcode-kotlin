package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.Grid
import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.findPaths

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
) {
    private val topographicMap = Grid.parse(inputLines) { it.digitToInt() }

    private fun trailsFromTrailhead(trailhead: Coordinates): Sequence<List<Coordinates>> {
        if (topographicMap[trailhead] != 0) return emptySequence()
        return findPaths(trailhead, { topographicMap[it] == 9 }) { coords ->
            coords.neighbors()
                .filter { topographicMap.inBounds(it) && topographicMap[it] == topographicMap[coords] + 1 }.toList()
        }
    }

    private fun trailheadScore(trailhead: Coordinates): Int {
        return trailsFromTrailhead(trailhead).map { it.last() }.toSet().size
    }

    override fun part1(): Number {
        return topographicMap.allCoordinates().sumOf { trailheadScore(it) }
    }

    private fun trailheadRating(trailhead: Coordinates): Int {
        return trailsFromTrailhead(trailhead).count()
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
