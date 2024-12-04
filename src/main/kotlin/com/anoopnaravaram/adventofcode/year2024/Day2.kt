package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.math.abs

class Day2 : PuzzleSolution(
    inputFilePath = "input/2024/day2/input.txt",
    exampleInput = """
        7 6 4 2 1
        1 2 7 8 9
        9 7 6 2 1
        1 3 2 4 5
        8 6 4 4 1
        1 3 6 7 9
    """.trimIndent(),
) {
    private val reports = inputLines.map { line -> line.split(' ').map { it.toInt() } }

    private fun isReportSafe(report: List<Int>): Boolean {
        val diffs = report.zipWithNext { a, b -> b - a }
        val isAscending = diffs.all { it > 0 }
        val isDescending = diffs.all { it < 0 }
        val areAllDiffsInRange = diffs.all { abs(it) in 1..3 }
        return (isAscending || isDescending) && areAllDiffsInRange
    }

    override fun part1(): Number {
        return reports.count { isReportSafe(it) }
    }

    private fun isReportSafeWithProblemDampener(report: List<Int>): Boolean {
        if (isReportSafe(report)) {
            return true
        }
        return report.indices.any { i ->
            val modifiedReport = report.toMutableList()
            modifiedReport.removeAt(i)
            isReportSafe(modifiedReport)
        }
    }

    override fun part2(): Number {
        return reports.count { isReportSafeWithProblemDampener(it) }
    }

}