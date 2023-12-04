package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution

private val stringToDigit = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)

private fun getFirstRealDigit(line: String): Int? {
    val indexOfFirstRealDigit = line.indexOfFirst { it.isDigit() }
    if (indexOfFirstRealDigit == -1) {
        return null
    }
    return line[indexOfFirstRealDigit].digitToInt()
}

private fun getLastRealDigit(line: String): Int? {
    val indexOfLastRealDigit = line.indexOfLast { it.isDigit() }
    if (indexOfLastRealDigit == -1) {
        return null
    }
    return line[indexOfLastRealDigit].digitToInt()
}

private fun getFirstDigit(line: String): Int {
    return sequence {

        val indexOfFirstRealDigit = line.indexOfFirst { it.isDigit() }
        if (indexOfFirstRealDigit != -1) {
            val firstRealDigit = line[indexOfFirstRealDigit].digitToInt()
            yield(Pair(indexOfFirstRealDigit, firstRealDigit))
        }

        for ((word, digit) in stringToDigit) {
            val index = line.indexOf(word)
            if (index != -1) {
                yield(Pair(index, digit))
            }
        }
    }.minBy { it.first }.second
}

private fun getLastDigit(line: String): Int {
    return sequence {

        val indexOfLastRealDigit = line.indexOfLast { it.isDigit() }
        if (indexOfLastRealDigit != -1) {
            val lastRealDigit = line[indexOfLastRealDigit].digitToInt()
            yield(Pair(indexOfLastRealDigit, lastRealDigit))
        }

        for ((word, digit) in stringToDigit) {
            val index = line.lastIndexOf(word)
            if (index != -1) {
                yield(Pair(index, digit))
            }
        }
    }.maxBy { it.first }.second
}

private fun getCalibrationValue(line: String): Int {
    val firstDigit = requireNotNull(getFirstRealDigit(line))
    val lastDigit = requireNotNull(getLastRealDigit(line))
    return firstDigit * 10 + lastDigit
}

private fun getCalibrationValueUpdated(line: String): Int {
    val firstDigit = getFirstDigit(line)
    val lastDigit = getLastDigit(line)
    return firstDigit * 10 + lastDigit
}

class Day1 : PuzzleSolution(
    inputFilePath = "input/2023/day1/input.txt",
    exampleInput = """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent(),
) {
    private val lines = input.trimEnd().lines()

    override fun part1(): Number {
        return lines.sumOf { getCalibrationValue(it) }
    }


    override fun part2(): Number {
        return lines.sumOf { getCalibrationValueUpdated(it) }
    }
}