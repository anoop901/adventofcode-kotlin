package com.anoopnaravaram.adventofcode

import java.util.ArrayDeque

fun <T> shortestPathLength(fromNode: T, toNode: T, adjacentNodes: (T) -> Iterable<T>): Int? {
    val queue = ArrayDeque<Pair<T,Int>>()
    val visited = mutableSetOf<T>()

    queue.addLast(Pair(fromNode, 0))
    while (queue.isNotEmpty()) {
        val (current, lastPathLength) = requireNotNull(queue.removeFirst())
        if (current == toNode) return lastPathLength
        if (current in visited) continue
        visited.add(current)
        adjacentNodes(current).forEach { queue.addLast(Pair(it, lastPathLength + 1)) }
    }
    return null
}

fun <T> shortestPathLengths(fromNode: T, adjacentNodes: (T) -> Iterable<T>): Map<T, Int> {
    val queue = ArrayDeque<Pair<T,Int>>()
    val result = mutableMapOf<T, Int>()

    queue.addLast(Pair(fromNode, 0))
    while (queue.isNotEmpty()) {
        val (current, lastPathLength) = requireNotNull(queue.removeFirst())
        if (current in result) continue
        result[current] = lastPathLength
        adjacentNodes(current).forEach { queue.addLast(Pair(it, lastPathLength + 1)) }
    }
    return result
}
