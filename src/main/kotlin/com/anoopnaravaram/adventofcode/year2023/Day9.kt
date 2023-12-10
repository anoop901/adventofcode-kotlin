package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution

private fun differences(list: List<Int>): List<Int> {
    return list.zipWithNext { a, b -> b - a }
}

private fun allLevelsDifferences(list: List<Int>): Sequence<List<Int>> {
    return generateSequence(list, ::differences).takeWhile { diffs -> !diffs.all { it == 0 } }
}

class Day9 : PuzzleSolution(
    inputFilePath = "input/2023/day9/input.txt",
    exampleInput = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent(),
) {
    private val valueHistories: List<List<Int>> = input.trimEnd().lines()
        .map { line -> line.split(" ").map { it.toInt() } }

    override fun part1(): Number {
        return valueHistories.sumOf { valueHistory ->
            val ends = allLevelsDifferences(valueHistory).map { it.last() }
            ends.sum()
        }
    }

    override fun part2(): Number {
        return valueHistories.sumOf { valueHistory ->
            val starts = allLevelsDifferences(valueHistory).map { it.first() }
            starts.toList().reversed().fold(0.toInt()) { acc, x -> x - acc }
        }
    }
}