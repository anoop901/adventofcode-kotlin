package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.Grid

private fun <T> allDistinctOrderedPairs(list: List<T>) = sequence {
    for (a in list) {
        for (b in list) {
            if (a != b) {
                yield(Pair(a, b))
            }
        }
    }
}

class Day8 : PuzzleSolution(
    inputFilePath = "input/2024/day8/input.txt",
    exampleInput = """
        ............
        ........0...
        .....0......
        .......0....
        ....0.......
        ......A.....
        ............
        ............
        ........A...
        .........A..
        ............
        ............
    """.trimIndent(),
    useInputFile = true
) {
    val grid = Grid.parse(inputLines)
    private val antennaLocationsByFrequency = grid.allCoordinates().filter { grid[it] != '.' }.groupBy { grid[it] }

    private fun allAntennaPairs() =
        antennaLocationsByFrequency.flatMap { (_, antennas) -> allDistinctOrderedPairs(antennas) }

    private fun antinodeFromAntennas(antenna1: Coordinates, antenna2: Coordinates): Coordinates {
        return antenna2 + antenna1.vectorTo(antenna2)
    }

    override fun part1(): Number {
        val antinodes =
            allAntennaPairs().map { (antenna1, antenna2) -> antinodeFromAntennas(antenna1, antenna2) }.toSet()
        return antinodes.count { grid.inBounds(it) }
    }

    private fun antinodesResonantFromAntennas(antenna1: Coordinates, antenna2: Coordinates): Sequence<Coordinates> {
        val diff = antenna1.vectorTo(antenna2)
        return generateSequence(antenna2) { it + diff }.takeWhile { grid.inBounds(it) }
    }

    override fun part2(): Number {
        return allAntennaPairs().flatMap { (antenna1, antenna2) -> antinodesResonantFromAntennas(antenna1, antenna2) }
            .toSet().count()
    }
}

fun main() {
    val solution = Day8()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
