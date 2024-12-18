package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.time.measureTimedValue

const val width = 101
const val height = 103

class Day14 : PuzzleSolution(
    inputFilePath = "input/2024/day14/input.txt",
    exampleInput = """
    """.trimIndent(),
) {

    data class Robot(val initialPosition: Coordinates, val velocity: Coordinates) {
        fun positionAfter(time: Int): Coordinates {
            val result = initialPosition + velocity * time
            return Coordinates(result.x.mod(width), result.y.mod(height))
        }
    }

    private val robots = inputLines.map { line ->
        val (p, v) = line.split(" ").map { it.split("=")[1].split(",").map { it.toInt() } }
        Robot(Coordinates(p[0], p[1]), Coordinates(v[0], v[1]))
    }

    private fun safetyScore(positions: List<Coordinates>): Int {
        return positions.count { it.x < 50 && it.y < 51 } *
                positions.count { it.x < 50 && it.y > 51 } *
                positions.count { it.x > 50 && it.y < 51 } *
                positions.count { it.x > 50 && it.y > 51 }
    }

    override fun part1(): Number {
        return safetyScore(robots.map { it.positionAfter(100) })
    }

    private fun saveImageForPositions(positions: List<Coordinates>) {
        val im = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (position in positions) {
            im.setRGB(position.x, position.y, Color.GREEN.rgb)
        }
        val outputFile = File("output/2024/day14/tree.png")
        outputFile.mkdirs()
        ImageIO.write(im, "png", outputFile)
    }

    override fun part2(): Number {
        val positionsAfterTime = List(width * height) { time -> robots.map { it.positionAfter(time) } }
        val (time, positions) = positionsAfterTime.withIndex().minBy { (_, positions) -> safetyScore(positions) }
        saveImageForPositions(positions)
        return time
    }
}

fun main() {
    val solution = Day14()
    measureTimedValue { solution.part1() }.run { println("part 1 (${duration.inWholeMilliseconds}ms): $value") }
    measureTimedValue { solution.part2() }.run { println("part 2 (${duration.inWholeMilliseconds}ms): $value") }
}
