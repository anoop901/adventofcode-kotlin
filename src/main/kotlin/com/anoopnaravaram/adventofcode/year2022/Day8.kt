package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day8 : PuzzleSolution(
    inputFilePath = "input/2022/day8/input.txt",
    exampleInput = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()
) {
    private val treeHeights: List<List<Int>> = input.trimEnd().split("\n").map { it.map(Char::digitToInt) }

    init {
        require(treeHeights.isNotEmpty()) { "no trees in input" }
    }

    private val height = treeHeights.size
    private val width = treeHeights[0].size

    init {
        require(treeHeights.all { it.size == width }) { "tree field is not rectangular" }
    }

    override fun part1(): Int {
        return 0
    }

    private fun inBounds(location: Pair<Int, Int>): Boolean {
        val (r, c) = location
        return r in 0..<height && c in 0..<width
    }

    private fun numTreesVisible(startingLocation: Pair<Int, Int>, direction: Pair<Int, Int>): Int {
        val startingHeight = treeHeights[startingLocation.first][startingLocation.second]

        val locationSequence = generateSequence(startingLocation) {
            Pair(it.first + direction.first, it.second + direction.second)
        }.drop(1).takeWhile { inBounds(it) }

        var result = 0
        for (location in locationSequence) {
            result++
            if (treeHeights[location.first][location.second] >= startingHeight) {
                break
            }
        }
        return result
    }

    private fun scenicScore(location: Pair<Int, Int>): Int {
        val directions = mapOf(
            "up" to Pair(-1, 0), "left" to Pair(0, -1), "right" to Pair(0, 1), "down" to Pair(1, 0)
        )
        return directions.values.map { numTreesVisible(location, it) }.reduce { a, b -> a * b }
    }

    override fun part2(): Int {
        fun generateAllLocations(): Sequence<Pair<Int, Int>> = sequence {
            for (r in 0..<height) {
                for (c in 0..<width) {
                    yield(Pair(r, c))
                }
            }
        }

        return generateAllLocations().map { scenicScore(it) }.maxOf { it }
    }
}
