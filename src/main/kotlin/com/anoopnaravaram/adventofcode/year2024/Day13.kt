package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.linesGrouped
import java.math.BigInteger
import kotlin.time.measureTimedValue

data class BigCoordinates(val x: BigInteger, val y: BigInteger)

class Day13 : PuzzleSolution(
    inputFilePath = "input/2024/day13/input.txt",
    exampleInput = """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
    """.trimIndent(),
    useInputFile = true
) {
    data class ClawMachine(val a: BigCoordinates, val b: BigCoordinates, val prize: BigCoordinates) {
        private val aPrice = 3.toBigInteger()
        private val bPrice = 1.toBigInteger()
        fun tokensNeeded(useActualPrizeLocation: Boolean = false): BigInteger? {
            // (ax bx | ay by) * (ta | tb) = (px | py)
            // find (3 1) * (ta | tb) = (3 1) * (ax bx | ay by)^-1 * (px | py)
            // = (3 1) * 1/(ax*by-bx*ay) * (by -bx | -ay ax) * (px | py)
            // = (3 1) * 1/(ax*by-bx*ay) * (by*px-bx*py | ax*py-ay*px)
            // ta = (by*px-bx*py)/(ax*by-bx*ay), tb=(ax*py-ay*px)/(ax*by-bx*ay)
            var prize = prize
            if (useActualPrizeLocation) {
                prize = BigCoordinates(prize.x + 10000000000000.toBigInteger(), prize.y + 10000000000000.toBigInteger())
            }
            val det = a.x * b.y - b.x * a.y
            val numerA = b.y * prize.x - b.x * prize.y
            val numerB = a.x * prize.y - a.y * prize.x
            if (numerA % det != 0.toBigInteger() || numerB % det != 0.toBigInteger()) return null
            val aPresses = numerA / det
            val bPresses = numerB / det
            return aPresses * aPrice + bPresses * bPrice
        }
    }

    val clawMachines = input.linesGrouped().map { lineGroup ->
        val (lineA, lineB, linePrize) = lineGroup
        val (ax, ay) = lineA.split(": ")[1].split(", ").map { it.split("+")[1].toBigInteger() }
        val (bx, by) = lineB.split(": ")[1].split(", ").map { it.split("+")[1].toBigInteger() }
        val (px, py) = linePrize.split(": ")[1].split(", ").map { it.split("=")[1].toBigInteger() }
        ClawMachine(BigCoordinates(ax, ay), BigCoordinates(bx, by), BigCoordinates(px, py))
    }.toList()

    override fun part1(): Number {
        return clawMachines.sumOf { it.tokensNeeded() ?: 0.toBigInteger() }
    }

    override fun part2(): Number {
        return clawMachines.sumOf { it.tokensNeeded(true) ?: 0.toBigInteger() }
    }
}

fun main() {
    val solution = Day13()
    measureTimedValue { solution.part1() }.run { println("part 1 (${duration.inWholeMilliseconds}ms): $value") }
    measureTimedValue { solution.part2() }.run { println("part 2 (${duration.inWholeMilliseconds}ms): $value") }
}
