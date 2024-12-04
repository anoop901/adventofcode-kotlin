package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day3 : PuzzleSolution(
    inputFilePath = "input/2024/day3/input.txt",
    // exampleInput = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))", // part 1
    exampleInput = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))", // part 2
    useInputFile = true
) {
    override fun part1(): Number {
        val regex = "mul\\((\\d{1,3}),(\\d{1,3})\\)".toRegex()
        return regex.findAll(input).sumOf { matchResult ->
            val x = matchResult.groupValues[1].toInt()
            val y = matchResult.groupValues[2].toInt()
            x * y
        }
    }

    override fun part2(): Number {
        val regex = "(do\\(\\))|(don't\\(\\))|mul\\((\\d{1,3}),(\\d{1,3})\\)".toRegex()
        return sequence {
            var enabled = true
            for (matchResult in regex.findAll(input)) {
                val doInstruction = matchResult.groupValues[1]
                val dontInstruction = matchResult.groupValues[2]
                if (doInstruction.isNotEmpty()) {
                    enabled = true
                } else if (dontInstruction.isNotEmpty()) {
                    enabled = false
                } else if (enabled) {
                    val x = matchResult.groupValues[3].toInt()
                    val y = matchResult.groupValues[4].toInt()
                    yield(x * y)
                }
            }
        }.sum()
    }
}