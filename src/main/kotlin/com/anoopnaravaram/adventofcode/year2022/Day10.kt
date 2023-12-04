package com.anoopnaravaram.adventofcode.year2022


import com.anoopnaravaram.adventofcode.PuzzleSolution
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private enum class InstructionType(val string: String) {
    ADDX("addx"),
    NOOP("noop");

    companion object {
        private val map = entries.associateBy { it.string }
        fun fromString(string: String): InstructionType {
            return map[string] ?: throw IllegalArgumentException("no such instruction: $string")
        }
    }
}

private data class Instruction(val type: InstructionType, val value: Int = 0)

class Day10 : PuzzleSolution(
    inputFilePath = "input/2022/day10/input.txt",
    exampleInput = """
        addx 15
        addx -11
        addx 6
        addx -3
        addx 5
        addx -1
        addx -8
        addx 13
        addx 4
        noop
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx -35
        addx 1
        addx 24
        addx -19
        addx 1
        addx 16
        addx -11
        noop
        noop
        addx 21
        addx -15
        noop
        noop
        addx -3
        addx 9
        addx 1
        addx -3
        addx 8
        addx 1
        addx 5
        noop
        noop
        noop
        noop
        noop
        addx -36
        noop
        addx 1
        addx 7
        noop
        noop
        noop
        addx 2
        addx 6
        noop
        noop
        noop
        noop
        noop
        addx 1
        noop
        noop
        addx 7
        addx 1
        noop
        addx -13
        addx 13
        addx 7
        noop
        addx 1
        addx -33
        noop
        noop
        noop
        addx 2
        noop
        noop
        noop
        addx 8
        noop
        addx -1
        addx 2
        addx 1
        noop
        addx 17
        addx -9
        addx 1
        addx 1
        addx -3
        addx 11
        noop
        noop
        addx 1
        noop
        addx 1
        noop
        noop
        addx -13
        addx -19
        addx 1
        addx 3
        addx 26
        addx -30
        addx 12
        addx -1
        addx 3
        addx 1
        noop
        noop
        noop
        addx -9
        addx 18
        addx 1
        addx 2
        noop
        noop
        addx 9
        noop
        noop
        noop
        addx -1
        addx 2
        addx -37
        addx 1
        addx 3
        noop
        addx 15
        addx -21
        addx 22
        addx -6
        addx 1
        noop
        addx 2
        addx 1
        noop
        addx -10
        noop
        noop
        addx 20
        addx 1
        addx 2
        addx 2
        addx -6
        addx -11
        noop
        noop
        noop
    """.trimIndent(),
) {

    private val instructions =
        input
            .trimEnd()
            .split("\n")
            .map { line ->
                val tokens = line.split(" ")
                require(tokens.isNotEmpty())
                when (val instructionType = InstructionType.fromString(tokens[0])) {
                    InstructionType.ADDX -> {
                        require(tokens.size == 2) { "ADDX expected 1 argument, found ${tokens.size - 1}" }
                        val value = tokens[1].toIntOrNull()
                            ?: throw IllegalArgumentException("ADDX expected an integer, found \"${tokens[1]}\"")
                        Instruction(instructionType, value)
                    }

                    InstructionType.NOOP -> {
                        require(tokens.size == 1) { "NOOP expected 0 arguments, found ${tokens.size - 1}" }
                        Instruction(instructionType)
                    }
                }
            }

    private fun emulate() = sequence {
        var cycleCount = 0
        var xRegisterValue = 1
        for (instruction in instructions) {
            when (instruction.type) {
                InstructionType.ADDX -> {
                    repeat(2) {
                        cycleCount++
                        yield(Pair(cycleCount, xRegisterValue))
                    }
                    xRegisterValue += instruction.value
                }

                InstructionType.NOOP -> {
                    cycleCount++
                    yield(Pair(cycleCount, xRegisterValue))
                }
            }
        }
    }

    override fun part1(): Int {
        val firstCycleToSample = 20
        val samplingPeriod = 40

        fun shouldSample(cycleCount: Int): Boolean {
            return (cycleCount - firstCycleToSample) % samplingPeriod == 0
        }

        return emulate()
            .filter { (cycleCount, _) -> shouldSample(cycleCount) }
            .map { (cycleCount, xRegisterValue) -> cycleCount * xRegisterValue }
            .sum()
    }

    override fun part2(): Int {
        val screenWidth = 40
        val screenHeight = 6
        val spriteWidth = 3
        val spriteRadius = (spriteWidth - 1) / 2
        val image = BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB)

        emulate()
            .take(screenWidth * screenHeight)
            .forEach { (cycleCount, xRegisterValue) ->
                val columnNumber = (cycleCount - 1) % screenWidth
                val rowNumber = (cycleCount - 1) / screenWidth
                val pixelLit =
                    columnNumber in (xRegisterValue - spriteRadius)..(xRegisterValue + spriteRadius)
                image.setRGB(columnNumber, rowNumber, if (pixelLit) 0xFFFFFFFF.toInt() else 0xFF000000.toInt())
            }

        File("output/2022/day10/part2.png").also {
            it.mkdirs()
            ImageIO.write(image, "png", it)
        }

        return 0
    }
}