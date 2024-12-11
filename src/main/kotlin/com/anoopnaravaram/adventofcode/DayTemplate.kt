package com.anoopnaravaram.adventofcode

import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.time.measureTimedValue

class DayXX : PuzzleSolution(
    inputFilePath = "input/2024/dayXX/input.txt",
    exampleInput = """
    """.trimIndent(),
) {
    override fun part1(): Number {
        return 0
    }

    override fun part2(): Number {
        return 0
    }
}

fun main() {
    val solution = DayXX()
    measureTimedValue { solution.part1() }.run { println("part 1 (${duration.inWholeMilliseconds}ms): $value") }
    measureTimedValue { solution.part2() }.run { println("part 2 (${duration.inWholeMilliseconds}ms): $value") }
}
