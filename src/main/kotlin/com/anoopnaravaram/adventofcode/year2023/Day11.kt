package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day11 : PuzzleSolution(
    inputFilePath = "input/2023/day11/input.txt",
    exampleInput = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent(),
//    useInputFile = false
) {
    private val galaxyLocations = inputLines.flatMapIndexed { y, line ->
        line.withIndex().filter { it.value == '#' }.map { (x, _) -> Coordinates(x, y) }
    }.toList()
    private val width = inputLines[0].length
    private val height = inputLines.size

    private val emptyX = (0..<width).filter { x -> galaxyLocations.all { it.x != x } }.toSet()
    private val emptyY = (0..<height).filter { x -> galaxyLocations.all { it.y != x } }.toSet()

    private fun distanceExpanded(c1: Coordinates, c2: Coordinates, expansion: Long = 1): Long {
        val xRange = min(c1.x, c2.x)..max(c1.x, c2.x)
        val yRange = min(c1.y, c2.y)..max(c1.y, c2.y)
        return abs(c1.x - c2.x) + abs(c1.y - c2.y) + (expansion - 1) * emptyX.count { it in xRange } + (expansion - 1) * emptyY.count { it in yRange }
    }

    private fun allPairs() = sequence {
        for (i1 in galaxyLocations.indices) {
            for (i2 in i1 + 1..<galaxyLocations.size) {
                if (galaxyLocations[i1] != galaxyLocations[i2]) {
                    yield(Pair(galaxyLocations[i1], galaxyLocations[i2]))
                }
            }
        }
    }

    override fun part1(): Number {
        return allPairs().map { (galaxy1, galaxy2) -> distanceExpanded(galaxy1, galaxy2) }.sum()
    }

    override fun part2(): Number {
        return allPairs().map { (galaxy1, galaxy2) -> distanceExpanded(galaxy1, galaxy2, 1000000) }.sum()
    }
}