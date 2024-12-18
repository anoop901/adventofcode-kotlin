package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.*
import kotlin.time.measureTimedValue

class Day12 : PuzzleSolution(
    inputFilePath = "input/2024/day12/input.txt",
    exampleInput = """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent(),
    useInputFile = true
) {
    val grid = Grid.parse(input.trim())

    data class Region(val letter: Char, val coordinates: Set<Coordinates>) {
        private val area = coordinates.size
        private val perimeter = coordinates.sumOf { it.neighbors().filterNot { it in coordinates }.count() }
        private val numberOfSides: Int get() {
            val edges = coordinates.flatMap { coord ->
                Direction.cardinals.mapNotNull { dir ->
                    if (coord + dir.offset in coordinates) return@mapNotNull null
                    else {
                        val edgeCoords = coord + when (dir) {
                            Direction.NORTH -> Coordinates(0, 0)
                            Direction.EAST -> Coordinates(1, 0)
                            Direction.SOUTH -> Coordinates(1, 1)
                            Direction.WEST -> Coordinates(0, 1)
                            else -> TODO()
                        }
                        val edgeDir = dir.turnRight
                        return@mapNotNull edgeCoords to edgeDir
                    }
                }
            }
            var sidesCount = 0
            for (dir in Direction.cardinals) {
                var edgesInDir = edges.filter { (_, d) -> d == dir }.toList()
                if (dir == Direction.NORTH || dir == Direction.SOUTH) edgesInDir = edgesInDir.groupBy { it.first.x }.flatMap { it.value.sortedBy { it.first.y } }
                if (dir == Direction.EAST || dir == Direction.WEST) edgesInDir = edgesInDir.groupBy { it.first.y }.flatMap { it.value.sortedBy { it.first.x } }
                if (dir == Direction.NORTH || dir == Direction.WEST) edgesInDir = edgesInDir.reversed()
                println(edgesInDir)
                val moreSides = edgesInDir.zipWithNext { (c1, d1), (c2, d2) -> c1 + d1.offset != c2 }.count { it } + 1
                sidesCount += moreSides
            }
            return sidesCount
        }
        val price = area * perimeter
        val priceDiscount = area * numberOfSides
    }

    private fun getRegions(): List<Region> {
        val seen = mutableSetOf<Coordinates>()
        val regions = mutableListOf<Region>()
        for (coords in grid.allCoordinates()) {
            if (coords in seen) continue

            val letter = grid[coords]
            val coordinates = mutableSetOf<Coordinates>()
            val stack = ArrayDeque<Coordinates>()
            stack.addLast(coords)
            while (stack.isNotEmpty()) {
                val c = stack.removeLast()
                if (c in seen) continue
                if (!grid.inBounds(c)) continue
                if (grid[c] != letter) continue
                coordinates.add(c)
                seen.add(c)
                stack.addAll(c.neighbors())
            }
            regions.add(Region(letter, coordinates))
        }
        return regions
    }

    override fun part1(): Number {
        return getRegions().sumOf { it.price }
    }

    override fun part2(): Number {
        return getRegions().sumOf { it.priceDiscount }
    }
}

fun main() {
    val solution = Day12()
    measureTimedValue { solution.part1() }.run { println("part 1 (${duration.inWholeMilliseconds}ms): $value") }
    measureTimedValue { solution.part2() }.run { println("part 2 (${duration.inWholeMilliseconds}ms): $value") }
}
