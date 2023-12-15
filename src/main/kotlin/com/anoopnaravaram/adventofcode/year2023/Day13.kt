package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.linesGrouped

private fun indexPairsReflectedAbout(reflectionLine: Int, size: Int) = sequence {
    var i1 = reflectionLine - 1
    var i2 = reflectionLine
    while (i1 >= 0 && i2 < size) {
        yield(Pair(i1, i2))
        i1--
        i2++
    }
}

private fun <T> countDiscrepancies(l1: List<T>, l2: List<T>): Int {
    return (l1 zip l2).count { (b1, b2) -> b1 != b2 }
}

private fun <T> findReflection(smudges: Int, numLayers: Int, getLayer: (Int) -> List<T>): Int? {
    for (reflectionLine in 1..numLayers - 1) {
        val indexPairs = indexPairsReflectedAbout(reflectionLine, numLayers)
        val totalDiscrepancies = indexPairs.sumOf { (c1, c2) -> countDiscrepancies(getLayer(c1), getLayer(c2)) }
        if (totalDiscrepancies == smudges) {
            return reflectionLine
        }
    }
    return null
}

private data class Pattern(val grid: List<List<Char>>) {
    private val height = grid.size
    private val width = grid[0].size

    private fun getRow(index: Int) = grid[index]
    private fun getColumn(index: Int) = grid.map { it[index] }

    private fun findVerticalReflection(smudges: Int = 0): Int? {
        return findReflection(smudges, width, this::getColumn)
    }

    private fun findHorizontalReflection(smudges: Int = 0): Int? {
        return findReflection(smudges, height, this::getRow)
    }

    fun summarizeReflectionLine(smudges: Int = 0): Int {
        return requireNotNull(
            findVerticalReflection(smudges)
                ?: findHorizontalReflection(smudges)?.let { it * 100 })
    }
}

class Day13 : PuzzleSolution(
    inputFilePath = "input/2023/day13/input.txt",
    exampleInput = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent(),
) {
    private val patterns = input.linesGrouped().map { lines ->
        Pattern(lines.map { line -> line.toList() })
    }.toList()

    override fun part1(): Number {
        return patterns.sumOf { it.summarizeReflectionLine() }
    }

    override fun part2(): Number {
        return patterns.sumOf { it.summarizeReflectionLine(1) }
    }
}