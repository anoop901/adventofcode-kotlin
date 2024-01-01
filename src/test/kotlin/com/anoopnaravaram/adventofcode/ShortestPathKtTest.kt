package com.anoopnaravaram.adventofcode

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ShortestPathKtTest {

    private fun getAdjacentNodesFunctionFromMazeString(mazeString: String): (Coordinates) -> List<Coordinates> {
        val maze = Grid.parse(mazeString) { it == '.' }
        return { coordinates: Coordinates ->
            coordinates.neighbors().filter { maze.inBounds(it) }.filter { maze[it] }.toList()
        }
    }

    @Test
    fun shortestPathLength_direct() {
        val adjacentNodes = getAdjacentNodesFunctionFromMazeString(
            """
            .....
            .....
            ..#..
            ..#..
            ..#..
            """.trimIndent()
        )
        assertEquals(4, shortestPathLength(Coordinates(0, 0), Coordinates(4, 0), adjacentNodes))
    }

    @Test
    fun shortestPathLength_withBarrier() {
        val adjacentNodes = getAdjacentNodesFunctionFromMazeString(
            """
            ..#..
            ..#..
            ..#..
            .....
            .....
            """.trimIndent()
        )
        assertEquals(10, shortestPathLength(Coordinates(0, 0), Coordinates(4, 0), adjacentNodes))
    }

    @Test
    fun shortestPathLength_pathDoesNotExist() {
        val adjacentNodes = getAdjacentNodesFunctionFromMazeString(
            """
            ..#..
            ..#..
            ..#..
            ..#..
            ..#..
            """.trimIndent()
        )
        assertEquals(null, shortestPathLength(Coordinates(0, 0), Coordinates(4, 0), adjacentNodes))
    }

    @Test
    fun shortestPathLength_singleLong() {
        val adjacentNodes = getAdjacentNodesFunctionFromMazeString(
            """
            .###.
            ..#..
            #.#.#
            ..#..
            .###.
            .....
            """.trimIndent()
        )
        assertEquals(18, shortestPathLength(Coordinates(0, 0), Coordinates(4, 0), adjacentNodes))
    }

    @Test
    fun shortestPathLengths() {
        val adjacentNodes = getAdjacentNodesFunctionFromMazeString(
            """
            .#...
            ##.#.
            .....
            .####
            ..#..
            """.trimIndent()
        )
        assertEquals(mapOf(
            Coordinates(x = 2, y = 2) to 0,
            Coordinates(x = 2, y = 1) to 1,
            Coordinates(x = 3, y = 2) to 1,
            Coordinates(x = 1, y = 2) to 1,
            Coordinates(x = 2, y = 0) to 2,
            Coordinates(x = 4, y = 2) to 2,
            Coordinates(x = 0, y = 2) to 2,
            Coordinates(x = 3, y = 0) to 3,
            Coordinates(x = 4, y = 1) to 3,
            Coordinates(x = 0, y = 3) to 3,
            Coordinates(x = 4, y = 0) to 4,
            Coordinates(x = 0, y = 4) to 4,
            Coordinates(x = 1, y = 4) to 5,
        ), shortestPathLengths(Coordinates(2, 2), adjacentNodes))

    }
}