package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.math.abs

class Day1 : PuzzleSolution(
    inputFilePath = "input/2024/day1/input.txt",
    exampleInput = """
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3
    """.trimIndent(),
    useInputFile = true
) {

    private val leftList: List<Int>
    private val rightList: List<Int>

    init {
        val inputNumbersByRow = inputLines.map { line -> line.split("\\s+".toRegex()).map { it.toInt() } }
        leftList = inputNumbersByRow.map { it[0] }
        rightList = inputNumbersByRow.map { it[1] }
    }

    override fun part1(): Number {
        return (leftList.sorted() zip rightList.sorted()).sumOf { (a, b) -> abs(a - b) }
    }

    override fun part2(): Number {
        return leftList.sumOf { leftNumber -> leftNumber * rightList.count { rightNumber -> leftNumber == rightNumber } }
    }
}