package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

private data class ToyBoatRace(val time: Long, val distance: Long)

class Day6 : PuzzleSolution(
    inputFilePath = "input/2023/day6/input.txt",
    exampleInput = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent(),
) {

    private fun lineToLongList(line: String): List<Long> {
        return line.split("\\s+".toRegex()).drop(1).map { it.toLong() }
    }

    private val lines = input.trimEnd().lines()
    private val races = (lineToLongList(lines[0]) zip lineToLongList(lines[1])).map { (a, b) -> ToyBoatRace(a, b) }

    override fun part1(): Number {
        return races.map { (time, distance) ->
            (0..time).map { it * (time - it) }.count { it > distance }
        }.reduce { a, b -> a * b }
    }

    private val actualTime = lines[0].split(" ").drop(1).joinToString("").toLong()
    private val actualDistance = lines[1].split(" ").drop(1).joinToString("").toLong()

    override fun part2(): Number {
        val t = actualTime.toDouble()
        val d = actualDistance.toDouble()

        val r1 = ceil((t - sqrt(t * t - 4 * d)) / 2).toLong()
        val r2 = floor((t + sqrt(t * t - 4 * d)) / 2).toLong()

        return r2 - r1 + 1
    }
}