package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.math.sign

private fun <T> cartesianProduct(vararg sets: Set<T>): Set<List<T>> {
    return sets.fold(listOf(listOf<T>())) { acc, set ->
        acc.flatMap { list -> set.map { element -> list + element } }
    }.toSet()
}

private fun <T> cartesianPower(set: Set<T>, n: Int): Set<List<T>> = cartesianProduct(*Array(n) { set })

class Day7 : PuzzleSolution(
    inputFilePath = "input/2024/day7/input.txt",
    exampleInput = """
        190: 10 19
        3267: 81 40 27
        83: 17 5
        156: 15 6
        7290: 6 8 6 15
        161011: 16 10 13
        192: 17 8 14
        21037: 9 7 18 13
        292: 11 6 16 20
    """.trimIndent()
) {

    data class Equation(val target: Long, val operands: List<Long>) {

        private fun evaluate(operatorCombo: List<String>): Long {
            return (operatorCombo zip operands.drop(1)).fold(operands[0]) { acc, (operator, operand) ->
                when (operator) {
                    "+" -> acc + operand
                    "*" -> acc * operand
                    "||" -> (acc.toString() + operand.toString()).toLong()
                    else -> throw IllegalArgumentException("unknown operator $operator")
                }
            }
        }

        fun canPossiblyBeTrue(enableConcatenation: Boolean = false): Boolean {
            if (operands.size == 1) {
                return target == operands.first()
            }
            val subEquations = sequence {
                if (target - operands.last() > 0) {
                    yield(Equation(target - operands.last(), operands.dropLast(1)))
                }
                if (target % operands.last() == 0L) {
                    yield(Equation(target / operands.last(), operands.dropLast(1)))
                }
                if (enableConcatenation && target.toString().endsWith(operands.last().toString())) {
                    val newTargetStr = target.toString().dropLast(operands.last().toString().length)
                    val newTarget = if (newTargetStr.isEmpty()) 0 else newTargetStr.toLong()
                    yield(Equation(newTarget, operands.dropLast(1)))
                }
            }
            return subEquations.any { it.canPossiblyBeTrue(enableConcatenation) }
        }
    }

    private val equations = inputLines.map { line ->
        val (targetStr, operandsStr) = line.split(": ")
        val target = targetStr.toLong()
        val operands = operandsStr.split(" ").map { it.toLong() }
        Equation(target, operands)
    }

    override fun part1(): Number {
        return equations.filter { it.canPossiblyBeTrue() }.sumOf { it.target }
    }

    override fun part2(): Number {
        return equations.filter { it.canPossiblyBeTrue(true) }.sumOf { it.target }
    }
}

fun main() {
    val solution = Day7()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
