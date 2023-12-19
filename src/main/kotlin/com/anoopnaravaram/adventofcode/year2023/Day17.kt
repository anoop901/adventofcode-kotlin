package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.Direction
import com.anoopnaravaram.adventofcode.Grid
import com.anoopnaravaram.adventofcode.PuzzleSolution
import java.util.*

private fun <T> shortestPath(startNode: T,
                     isTargetNode: (T) -> Boolean,
                     getAdjacentNodesAndWeights: (T) -> Map<T, Int>): Int {
    // This function was generated by ChatGPT lol
    val distance = mutableMapOf<T, Int>().withDefault { Int.MAX_VALUE }
    val visited = mutableSetOf<T>()
    val priorityQueue = PriorityQueue<Pair<T, Int>>(compareBy { it.second })

    distance[startNode] = 0
    priorityQueue.add(Pair(startNode, 0))

    return sequence {
        while (priorityQueue.isNotEmpty()) {
            val (currentNode, currentDistance) = priorityQueue.poll()

            if (visited.contains(currentNode)) continue
            visited.add(currentNode)

            if (isTargetNode(currentNode)) {
                yield(currentDistance)
            }

            for ((adjacentNode, weight) in getAdjacentNodesAndWeights(currentNode)) {
                if (!visited.contains(adjacentNode)) {
                    val newDistance = currentDistance + weight
                    if (newDistance < distance.getValue(adjacentNode)) {
                        distance[adjacentNode] = newDistance
                        priorityQueue.add(Pair(adjacentNode, newDistance))
                    }
                }
            }
        }
    }.min()
}

class Day17 : PuzzleSolution(
    inputFilePath = "input/2023/day17/input.txt",
    exampleInput = """
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """.trimIndent(),
//    useInputFile = false
) {
    private val grid = Grid.parse(inputLines) { it.digitToInt() }

    private data class State(val coordinates: Coordinates, val direction: Direction, val straightLength: Int)

    private fun nextStates(state: State, minStraightLengthBeforeTurn: Int = 1, maxStraightLengthBeforeTurn: Int = 3): List<State> = sequence {
        if (state.straightLength == 0) {
            Direction.entries.forEach {
                yield(State(state.coordinates.offset(it.offset), it, 1))
            }
        } else {
            if (state.straightLength >= minStraightLengthBeforeTurn) {
                listOf(state.direction.turnLeft, state.direction.turnRight).forEach {
                    yield(State(state.coordinates.offset(it.offset), it, 1))
                }
            }
            if (state.straightLength < maxStraightLengthBeforeTurn) {
                yield(State(state.coordinates.offset(state.direction.offset), state.direction, state.straightLength + 1))
            }
        }
    }.filter { grid.inBounds(it.coordinates) }.toList()

    override fun part1(): Number {
        val shortestDistance = shortestPath(
            State(Coordinates(0, 0), Direction.EAST, 0),
            { state: State -> state.coordinates == Coordinates(grid.width - 1, grid.height - 1) },
            { state: State -> nextStates(state).associateWith { grid[it.coordinates] }}
        )
        return shortestDistance
    }

    override fun part2(): Number {
        val shortestDistance = shortestPath(
            State(Coordinates(0, 0), Direction.EAST, 0),
            { state: State -> state.coordinates == Coordinates(grid.width - 1, grid.height - 1) },
            { state: State -> nextStates(state, 4, 10).associateWith { grid[it.coordinates] }}
        )
        return shortestDistance
    }
}

fun main() {
    val solution = Day17()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}