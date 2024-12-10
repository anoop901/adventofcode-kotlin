package com.anoopnaravaram.adventofcode

fun <T> findPaths(
    start: T,
    isTarget: (T) -> Boolean,
    getAdjacent: (T) -> Iterable<T>
): Sequence<List<T>> = sequence {
    val stack = ArrayDeque<List<T>>()

    stack.add(listOf(start))

    while (stack.isNotEmpty()) {
        val path = stack.removeLast()
        val node = path.last()

        if (isTarget(node)) {
            yield(path)
        }

        for (neighbor in getAdjacent(node)) {
            if (neighbor !in path) {
                stack.addLast(path + neighbor)
            }
        }
    }
}

fun <T> findPathsToTarget(
    start: T,
    target: T,
    getAdjacent: (T) -> List<T>
): Sequence<List<T>> = findPaths(start, { it == target }, getAdjacent)

fun <T> findPathsToAnywhere(
    start: T,
    getAdjacent: (T) -> List<T>
): Sequence<List<T>> = findPaths(start, { true }, getAdjacent)


fun main() {
    val maze = Grid.parse(
        """
             ###
        ## # ###
        #    ###
    """.trimIndent()
    ) { it == ' ' }

    val paths = findPaths(
        Coordinates(0, 0),
        { it.x >= 3 }
    ) { it.neighbors().filter { maze.inBounds(it) && maze[it] }.toList() }

    for (path in paths) {
        val pathSet = path.toSet()
        for (y in 0..<maze.height) {
            println(
                maze.rowWithCoordinates(y)
                    .map { (coords, mazeCell) -> if (pathSet.contains(coords)) '.' else if (mazeCell) ' ' else '#' }
                    .joinToString("")
            )
        }
        println()
    }
}
