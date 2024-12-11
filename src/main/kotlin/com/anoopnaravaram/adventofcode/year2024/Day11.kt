package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.PuzzleSolution
import java.math.BigInteger

class Day11 : PuzzleSolution(
    inputFilePath = "input/2024/day11/input.txt",
    exampleInput = "125 17",
) {
    private val initialStonesArrangement = input.trim().split(" ").map { it.toBigInteger() }

    private fun blink(stonesArrangement: Map<BigInteger, BigInteger>): Map<BigInteger, BigInteger> {
        return stonesArrangement.flatMap { (number, count) ->
            if (number == BigInteger.ZERO) {
                listOf(BigInteger.ONE to count)
            } else {
                val s = number.toString()
                if (s.length % 2 == 0) {
                    listOf(
                        s.substring(0..<s.length / 2).toBigInteger() to count,
                        s.substring(s.length / 2..<s.length).toBigInteger() to count
                    )
                } else {
                    listOf(number * 2024.toBigInteger() to count)
                }
            }
        }.groupBy({ it.first }, { it.second }).mapValues { (_, value) -> value.sumOf { it } }
    }

    override fun part1(): Number {
        var stonesArrangement =
            initialStonesArrangement.groupingBy { it }.eachCount().mapValues { (_, value) -> value.toBigInteger() }
        repeat(25) { stonesArrangement = blink(stonesArrangement) }
        return stonesArrangement.values.sumOf { it }
    }

    override fun part2(): Number {
        var stonesArrangement =
            initialStonesArrangement.groupingBy { it }.eachCount().mapValues { (_, value) -> value.toBigInteger() }
        repeat(75) { stonesArrangement = blink(stonesArrangement) }
        return stonesArrangement.values.sumOf { it }
    }
}

fun main() {
    val solution = Day11()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
