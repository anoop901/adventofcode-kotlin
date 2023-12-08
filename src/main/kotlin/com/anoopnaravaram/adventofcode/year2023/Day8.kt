package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution

private enum class HauntedInstruction(val char: Char) {
    LEFT('L'),
    RIGHT('R');

    companion object {
        private val map = entries.associateBy { it.char }
        fun fromChar(char: Char) = requireNotNull(map[char])
    }
}

private data class HauntedNetworkNode(val name: String, val nextLeft: String, val nextRight: String) {
    fun next(instruction: HauntedInstruction) = when (instruction) {
        HauntedInstruction.LEFT -> nextLeft
        HauntedInstruction.RIGHT -> nextRight
    }
}

class Day8 : PuzzleSolution(
    inputFilePath = "input/2023/day8/input.txt",
    exampleInput = """
        LR

        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
    """.trimIndent(),
) {
    private val lines = input.trimEnd().lines()
    private val instructions = lines[0].map { HauntedInstruction.fromChar(it) }

    private val network = lines.drop(2).map {
        val (from, _, toLeftS, toRightS) = it.split(" ")
        val toLeft = toLeftS.drop(1).dropLast(1)
        val toRight = toRightS.dropLast(1)
        HauntedNetworkNode(from, toLeft, toRight)
    }.associateBy { it.name }

    private fun infiniteInstructions() = sequence { while (true) yieldAll(instructions) }

    private fun pathFrom(start: String) = sequence {
        var current = start
        for (instruction in infiniteInstructions()) {
            yield(current)
            current = network[current]!!.next(instruction)
        }
    }

    override fun part1(): Number {
        return pathFrom("AAA").indexOfFirst { it == "ZZZ" }
    }

    override fun part2(): Number {
        fun cycleLengthFrom(start: String) = pathFrom(start).indexOfFirst { it.endsWith("Z") }
        val starts = network.keys.filter { it.endsWith("A") }
        val cycleLengths = starts.map { cycleLengthFrom(it) }
        return cycleLengths.map { it.toLong() }.reduce(::lcm)
    }
}

private fun lcm(a: Long, b: Long): Long {
    return a * b / gcd(a, b)
}

private fun gcd(a: Long, b: Long): Long {
    if (a == 0L) return b
    return gcd(b % a, a)
}
