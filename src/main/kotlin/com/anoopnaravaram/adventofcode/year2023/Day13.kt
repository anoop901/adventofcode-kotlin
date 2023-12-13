package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.linesGrouped

fun indexPairsReflectedAbout(reflectionLine: Int, size: Int) = sequence {
    var i1 = reflectionLine - 1
    var i2 = reflectionLine
    while (i1 >= 0 && i2 < size) {
        yield(Pair(i1, i2))
        i1--
        i2++
    }
}

fun countDiscrepancies(l1: List<Boolean>, l2: List<Boolean>): Int {
    return (l1 zip l2).count { (b1, b2) -> b1 != b2 }
}

fun findReflection(smudges: Int, numLayers: Int, getLayer: (Int) -> List<Boolean>): Int? {
    for (reflectionLine in 1..numLayers - 1) {
        val indexPairs = indexPairsReflectedAbout(reflectionLine, numLayers)
        val totalDiscrepancies = indexPairs.sumOf { (c1, c2) -> countDiscrepancies(getLayer(c1), getLayer(c2)) }
        if (totalDiscrepancies == smudges) {
            return reflectionLine
        }
    }
    return null
}

data class Pattern(val rocks: List<List<Boolean>>) {
    private val height = rocks.size
    private val width = rocks[0].size

    private fun getRow(index: Int) = rocks[index]
    private fun getColumn(index: Int) = rocks.map { it[index] }

    fun findVerticalReflection(smudges: Int = 0): Int? {
        return findReflection(smudges, width, this::getColumn)
    }

    fun findHorizontalReflection(smudges: Int = 0): Int? {
        return findReflection(smudges, height, this::getRow)
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
        Pattern(lines.map { line -> line.map { it == '#' } })
    }.toList()

    override fun part1(): Number {
        return patterns.sumOf { it.findVerticalReflection() ?: (it.findHorizontalReflection()?.times(100)) ?: 0 }
    }

    override fun part2(): Number {
        return patterns.sumOf { pattern ->
            pattern.findVerticalReflection(1)
                ?: pattern.findHorizontalReflection(1)?.let { it * 100 }
                ?: throw IllegalArgumentException()
        }
    }
}