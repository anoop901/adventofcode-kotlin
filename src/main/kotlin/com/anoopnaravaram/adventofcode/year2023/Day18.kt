package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.Direction
import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day18 : PuzzleSolution(
    inputFilePath = "input/2023/day18/input.txt",
    exampleInput = """
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """.trimIndent(),
) {
    private data class DigPlan(val steps: List<Step>) {
        data class Step(val direction: Direction, val distance: Int)

        fun corners() = steps.scan(Coordinates(0, 0)) { acc, step -> acc + step.direction.offset * step.distance }

        fun areaInsideBorderCenterX2(): Long {
            return corners().zipWithNext().sumOf { (corner1, corner2) ->
                corner1.x.toLong() * corner2.y - corner1.y.toLong() * corner2.x
            }
        }

        fun areaOfOuterHalfOfBorderX2(): Int {
            return 2 + steps.sumOf { it.distance }
        }

        fun area() = (areaOfOuterHalfOfBorderX2() + areaInsideBorderCenterX2()) / 2
    }

    private val digPlan = DigPlan(inputLines.map { line ->
        val words = line.split(" ")
        val direction = when (words[0]) {
            "U" -> Direction.NORTH
            "D" -> Direction.SOUTH
            "L" -> Direction.WEST
            "R" -> Direction.EAST
            else -> throw IllegalArgumentException()
        }
        val distance = words[1].toInt()
        DigPlan.Step(direction, distance)
    })

    override fun part1(): Number {
        return digPlan.area()
    }

    private val digPlanCorrected = DigPlan(inputLines.map { line ->
        val words = line.split(" ")
        val hexCode = words[2].drop(2).dropLast(1)
        val distance = hexCode.dropLast(1).toInt(16)
        val direction = when (hexCode.last()) {
            '0' -> Direction.EAST
            '1' -> Direction.SOUTH
            '2' -> Direction.WEST
            '3' -> Direction.NORTH
            else -> throw IllegalArgumentException()
        }
        DigPlan.Step(direction, distance)
    })

    override fun part2(): Number {
        return digPlanCorrected.area()
    }
}

fun main() {
    val solution = Day18()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
