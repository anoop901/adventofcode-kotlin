package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min

private data class Coordinates(val x: Int, val y: Int) {
    companion object {
        fun fromString(s: String): Coordinates {
            val (x, y) = s.split(",")
            return Coordinates(x.toInt(), y.toInt())
        }
    }
}

private val startingSandCoordinates = Coordinates(500, 0)

private fun coordsOnLine(endpoint1: Coordinates, endpoint2: Coordinates): Sequence<Coordinates> = sequence {
    when {
        endpoint1.x == endpoint2.x -> {
            val x = endpoint1.x
            val yMin = min(endpoint1.y, endpoint2.y)
            val yMax = max(endpoint1.y, endpoint2.y)
            for (y in yMin..yMax) {
                yield(Coordinates(x, y))
            }
        }

        endpoint1.y == endpoint2.y -> {
            val y = endpoint1.y
            val xMin = min(endpoint1.x, endpoint2.x)
            val xMax = max(endpoint1.x, endpoint2.x)
            for (x in xMin..xMax) {
                yield(Coordinates(x, y))
            }
        }

        else -> {
            throw IllegalArgumentException("points $endpoint1 and $endpoint2 are not on the same vertical or horizontal line")
        }
    }
}

class Day14 : PuzzleSolution(
    inputFilePath = "input/2022/day14/input.txt",
    exampleInput = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent(),
) {
    private val rockCoordinates =
        input
            .trimEnd()
            .lines()
            .asSequence()
            .flatMap {
                it
                    .split(" -> ")
                    .map(Coordinates::fromString)
                    .asSequence()
                    .zipWithNext()
                    .flatMap { (endpoint1, endpoint2) -> coordsOnLine(endpoint1, endpoint2) }
            }
            .toSet()

    private val yMax = rockCoordinates.maxOf { it.y }
    private val floorY = yMax + 2

    private fun coordinatesOccupied(coordinates: Coordinates, restedSand: Set<Coordinates>): Boolean {
        return rockCoordinates.contains(coordinates) || restedSand.contains(coordinates) || coordinates.y >= floorY
    }

    private fun moveSand(currentSandCoordinates: Coordinates, restedSand: Set<Coordinates>): Coordinates? {
        val (x, y) = currentSandCoordinates
        return sequenceOf(
            Coordinates(x, y + 1),
            Coordinates(x - 1, y + 1),
            Coordinates(x + 1, y + 1)
        ).firstOrNull { !coordinatesOccupied(it, restedSand) }
    }

    override fun part1(): Int {
        var currentSandCoordinates = startingSandCoordinates
        val restedSand = mutableSetOf<Coordinates>()
        while (currentSandCoordinates.y < yMax) {
            currentSandCoordinates = moveSand(currentSandCoordinates, restedSand) ?: run {
                restedSand.add(currentSandCoordinates)
                startingSandCoordinates
            }
        }
        return restedSand.size
    }

    override fun part2(): Int {
        var currentSandCoordinates = startingSandCoordinates
        val restedSand = mutableSetOf<Coordinates>()
        while (!restedSand.contains(startingSandCoordinates)) {
            currentSandCoordinates = moveSand(currentSandCoordinates, restedSand) ?: run {
                restedSand.add(currentSandCoordinates)
                startingSandCoordinates
            }
        }
        return restedSand.size
    }
}
