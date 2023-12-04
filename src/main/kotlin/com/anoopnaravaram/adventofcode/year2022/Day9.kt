package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.math.absoluteValue
import kotlin.math.sign

private enum class Direction(val stringValue: String, val offset: Pair<Int, Int>) {
    U("U", Pair(1, 0)),
    D("D", Pair(-1, 0)),
    L("L", Pair(0, -1)),
    R("R", Pair(0, 1));

    companion object {
        fun fromString(value: String): Direction? {
            return entries.find { it.stringValue == value }
        }
    }
}

private data class Motion(val direction: Direction, val amount: Int)

class Day9 : PuzzleSolution(
    inputFilePath = "input/2022/day9/input.txt",
    exampleInput = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent()
) {
    private val motions: List<Motion> = input
        .trimEnd()
        .split("\n")
        .map { line ->
            val tokens = line.split(" ")
            require(tokens.size == 2) { "could not parse motion \"$line\"" }
            val (directionStr, amountStr) = line.split(" ")
            Motion(
                Direction.fromString(directionStr)
                    ?: throw IllegalArgumentException("invalid direction \"$directionStr\""),
                amountStr.toInt()
            )
        }

    private fun headTailTouching(head: Pair<Int, Int>, tail: Pair<Int, Int>): Boolean {
        val (headRow, headCol) = head
        val (tailRow, tailCol) = tail
        return (headRow - tailRow).absoluteValue <= 1 && (headCol - tailCol).absoluteValue <= 1
    }

    private fun moveTailTowardHead(tail: Pair<Int, Int>, head: Pair<Int, Int>): Pair<Int, Int> {
        var newTail = tail
        while (!headTailTouching(head, newTail)) {
            val (tailRow, tailCol) = newTail
            val (headRow, headCol) = head
            val rowOff = (headRow - tailRow).sign
            val colOff = (headCol - tailCol).sign
            newTail = Pair(tailRow + rowOff, tailCol + colOff)
        }
        return newTail
    }

    private fun moveKnotListTowardHead(knotLocations: MutableList<Pair<Int, Int>>) {
        for (i in 0..<knotLocations.size - 1) {
            val headLocation = knotLocations[i]
            var tailLocation = knotLocations[i + 1]
            tailLocation = moveTailTowardHead(tailLocation, headLocation)
            knotLocations[i + 1] = tailLocation
        }
    }

    override fun part1(): Int {
        var headLocation = Pair(0, 0)
        var tailLocation = Pair(0, 0)
        val tailLocationHistory = mutableSetOf<Pair<Int, Int>>()
        for (motion in motions) {
            val (direction, amount) = motion
            repeat(amount) {
                headLocation = Pair(
                    headLocation.first + direction.offset.first,
                    headLocation.second + direction.offset.second
                )
                tailLocation = moveTailTowardHead(tailLocation, headLocation)
                tailLocationHistory.add(tailLocation)
            }
        }
        return tailLocationHistory.size
    }

    override fun part2(): Int {
        val numKnots = 10
        val knotLocations = MutableList(10) { Pair(0, 0) }
        val tailLocationHistory = mutableSetOf<Pair<Int, Int>>()

        for (motion in motions) {
            val (direction, amount) = motion
            repeat(amount) {
                knotLocations[0] = Pair(
                    knotLocations[0].first + direction.offset.first,
                    knotLocations[0].second + direction.offset.second
                )
                moveKnotListTowardHead(knotLocations)
                tailLocationHistory.add(knotLocations[numKnots - 1])
            }
        }

        return tailLocationHistory.size
    }
}