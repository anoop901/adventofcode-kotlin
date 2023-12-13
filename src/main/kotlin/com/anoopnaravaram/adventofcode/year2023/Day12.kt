package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.groupedBySeparator

private enum class SpringState(val char: Char) {
    OPERATIONAL('.'),
    DAMAGED('#'),
    UNKNOWN('?');

    val mightBeDamaged get() = this == DAMAGED || this == UNKNOWN
    val mightBeOperational get() = this == OPERATIONAL || this == UNKNOWN

    companion object {
        val map = entries.associateBy { it.char }
        fun fromChar(char: Char) = requireNotNull(map[char])
    }
}


private data class SpringRow(val springStates: List<SpringState>, val contiguousDamagedGroups: List<Int>) {

    private fun damagedGroupMightExistAtPosition(position: Int, groupSize: Int): Boolean {
        return (position..<position + groupSize).map { springStates[it] }.all { it.mightBeDamaged }
                && springStates.getOrNull(position - 1)?.mightBeOperational ?: true
                && springStates.getOrNull(position + groupSize)?.mightBeOperational ?: true
    }

    private fun allAfterPositionMightBeOperational(position: Int) =
        (position..<springStates.size).all { springStates[it].mightBeOperational }

    private fun allWithinRangeMightBeOperational(from: Int, to: Int) =
        (from..<to).all { springStates[it].mightBeOperational }

    fun countPossibilities(): Long {
        val memo = mutableMapOf<Pair<Int, Int>, Long>()
        fun countPossibilitiesHelper(currentPosition: Int = 0, groupIndex: Int = 0): Long =
            memo.getOrPut(Pair(currentPosition, groupIndex)) {
                if (groupIndex >= contiguousDamagedGroups.size) {
                    return@getOrPut if (allAfterPositionMightBeOperational(currentPosition)) 1 else 0
                }
                val groupSize = contiguousDamagedGroups[groupIndex]

                (currentPosition..(springStates.size - groupSize))
                    .takeWhile { allWithinRangeMightBeOperational(currentPosition, it) }
                    .filter { damagedGroupMightExistAtPosition(it, groupSize) }
                    .sumOf { countPossibilitiesHelper(it + groupSize + 1, groupIndex + 1) }
            }
        return countPossibilitiesHelper()
    }

    fun unfold(factor: Int = 5): SpringRow {
        val unfoldedSpringStates = (0..<factor).flatMap { index ->
            sequence {
                if (index != 0) yield(SpringState.UNKNOWN)
                yieldAll(springStates)
            }
        }.toList()
        val unfoldedContiguousDamagedGroups = (0..<factor).flatMap { contiguousDamagedGroups }
        return SpringRow(unfoldedSpringStates, unfoldedContiguousDamagedGroups)
    }

    companion object {
        fun fromString(string: String): SpringRow {
            val (statesString, continuousDamagedGroupsString) = string.split(" ")
            val states = statesString.map { SpringState.fromChar(it) }
            val contiguousDamagedGroups = continuousDamagedGroupsString.split(",").map { it.toInt() }
            return SpringRow(states, contiguousDamagedGroups)
        }
    }
}

private fun allBooleanLists(size: Int) = sequence {
    for (x in 0..<(1 shl size)) {
        yield((0..<size).map { (1 shl it) and x != 0 }.toList())
    }
}

private fun getPossibilities(springStates: List<SpringState>) = sequence<List<SpringState>> {
    val unknownIndices = springStates.withIndex().filter { it.value == SpringState.UNKNOWN }.map { it.index }
    val numberOfUnknowns = unknownIndices.size
    for (booleanList in allBooleanLists(numberOfUnknowns)) {
        val result = springStates.toMutableList()
        unknownIndices.forEachIndexed { booleanIndex, index ->
            result[index] = if (booleanList[booleanIndex]) {
                SpringState.OPERATIONAL
            } else {
                SpringState.DAMAGED
            }
        }
        yield(result)
    }
}

private fun findContiguousDamagedGroups(springStates: List<SpringState>): List<Int> {
    return springStates.groupedBySeparator { it != SpringState.DAMAGED }.map { it.size }.toList()
}

class Day12 : PuzzleSolution(
    inputFilePath = "input/2023/day12/input.txt",
    exampleInput = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent(),
//    useInputFile = false
) {
    private val springRows = inputLines.map { SpringRow.fromString(it) }

    override fun part1(): Number {
        return springRows.sumOf { it.countPossibilities() }
    }

    override fun part2(): Number {
        return springRows.sumOf { it.unfold().countPossibilities() }
    }
}