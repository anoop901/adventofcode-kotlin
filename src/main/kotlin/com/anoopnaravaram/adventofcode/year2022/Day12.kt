package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution


private fun elevationFromChar(char: Char): Int = char - 'a'

class Day12 : PuzzleSolution(
    inputFilePath = "input/2022/day12/input.txt",
    exampleInput = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent(),
) {
    val elevationMap: List<List<Int>>
    var startLocation = Pair(0, 0)
    var endLocation = Pair(0, 0)

    init {
        elevationMap = input.trimEnd().lines()
            .mapIndexed { row, line ->
                line.mapIndexed { col, char ->
                    when (char) {
                        'S' -> {
                            startLocation = Pair(row, col)
                            elevationFromChar('a')
                        }

                        'E' -> {
                            endLocation = Pair(row, col)
                            elevationFromChar('z')
                        }

                        else -> {
                            elevationFromChar(char)
                        }
                    }
                }
            }
    }

    val width = elevationMap[0].size
    val height = elevationMap.size

    fun stepsFromStartingPoints(startingPoints: List<Pair<Int, Int>>): Int {
        val bfsQueue = ArrayDeque<Pair<Int, Int>?>(startingPoints)
        bfsQueue.addLast(null)
        val visited = mutableSetOf<Pair<Int, Int>>()
        var steps = 0
        while (bfsQueue.size > 1) {
            val location = bfsQueue.removeFirst()
            if (location == null) {
                steps++
                bfsQueue.addLast(null)
                continue
            }
            if (location in visited) {
                continue
            }
            visited.add(location)

            if (location == endLocation) {
                break
            }

            val (row, col) = location
            val elevation = elevationMap[row][col]

            val neighbors = sequenceOf(
                Pair(row - 1, col),
                Pair(row + 1, col),
                Pair(row, col - 1),
                Pair(row, col + 1)
            ).filter { (row, col) ->
                row in 0..<height && col in 0..<width
            }.filter { (row, col) ->
                val nextElevation = elevationMap[row][col]
                nextElevation <= elevation + 1
            }.filter {
                it !in visited
            }.toList()
            bfsQueue.addAll(neighbors)
        }
        return steps
    }

    override fun part1(): Int {
        return stepsFromStartingPoints(listOf(startLocation))
    }

    override fun part2(): Int {
        val allLocations = (0..<height).flatMap { row ->
            (0..<width).map { col ->
                Pair(row, col)
            }
        }
        return stepsFromStartingPoints(allLocations.filter { (row, col) ->
            elevationMap[row][col] == elevationFromChar('a')
        })
    }
}