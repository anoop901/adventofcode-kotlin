package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import java.io.File


private enum class CubeColor(val string: String) {
    RED("red"),
    GREEN("green"),
    BLUE("blue");

    companion object {
        private val map = entries.associateBy { it.string }
        fun fromString(string: String): CubeColor {
            return requireNotNull(map[string]) { "no color named $string" }
        }
    }
}

private data class CubeSet(val cubeCounts: Map<CubeColor, Int>) {

    val wouldBePossible: Boolean get() = totalCubeSet.contains(this)

    fun contains(that: CubeSet): Boolean {
        return that.cubeCounts.all { (color, count) -> count <= (this.cubeCounts[color] ?: 0) }
    }

    val power get() = CubeColor.entries.map { cubeCounts[it] ?: 0 }.reduce { a, b -> a * b }

    companion object {
        fun fromString(string: String): CubeSet {
            val cubeCounts = string.split(", ").associate {
                val (countStr, colorStr) = it.split(" ")
                CubeColor.fromString(colorStr) to countStr.toInt()
            }
            return CubeSet(cubeCounts)
        }
    }
}

private data class CubesGame(val gameId: Int, val cubeSets: List<CubeSet>) {

    val minimalCubeSetToMakePossible: CubeSet
        get() {
            val cubeCounts = CubeColor.entries.associateWith { color ->
                cubeSets.maxOfOrNull { it.cubeCounts[color] ?: 0 } ?: 0
            }
            return CubeSet(cubeCounts)
        }

    val wouldBePossible: Boolean get() = cubeSets.all { it.wouldBePossible }

    companion object {
        fun fromString(string: String): CubesGame {
            val (gameIdStr, cubeSetsStr) = string.split(": ")
            val gameId = gameIdStr.split(" ")[1].toInt()
            val cubeSets = cubeSetsStr.split("; ").map { CubeSet.fromString(it) }
            return CubesGame(gameId, cubeSets)
        }
    }
}

private val totalCubeSet = CubeSet(
    mapOf(
        CubeColor.RED to 12,
        CubeColor.GREEN to 13,
        CubeColor.BLUE to 14
    )
)

class Day2 : PuzzleSolution(
    inputFilePath = "input/2023/day2/input.txt",
    exampleInput = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent(),
) {
    private val games = input.trimEnd().lines().map { CubesGame.fromString(it) }

    init {
        fun cubeCountToJson(entry: Map.Entry<CubeColor, Int>): JsonElement =
            buildJsonObject {
                put("count", JsonPrimitive(entry.value))
                put("color", JsonPrimitive(entry.key.string))
            }

        fun cubeSetToJson(cubeSet: CubeSet): JsonElement =
            buildJsonArray {
                cubeSet.cubeCounts.map {
                    cubeCountToJson(it)
                }.forEach { add(it) }
            }

        fun gameToJson(cubesGame: CubesGame): JsonElement =
            buildJsonObject {
                put("gameId", JsonPrimitive(cubesGame.gameId))
                put("cubeSets", buildJsonArray {
                    cubesGame.cubeSets.map { cubeSetToJson(it) }.forEach { add(it) }
                })
            }

        val gamesJson = buildJsonArray { games.map { gameToJson(it) }.forEach { add(it) } }
        File("output/2023/day2/input.json").writeText(gamesJson.toString())
    }

    override fun part1(): Number {
        return games
            .filter { it.wouldBePossible }
            .sumOf { it.gameId }
    }

    override fun part2(): Number {
        return games.sumOf { it.minimalCubeSetToMakePossible.power }
    }
}
