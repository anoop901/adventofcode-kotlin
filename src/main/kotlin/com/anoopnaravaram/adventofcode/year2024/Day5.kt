package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.linesGrouped

class Day5 : PuzzleSolution(
    inputFilePath = "input/2024/day5/input.txt",
    exampleInput = """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13

        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        75,97,47,61,53
        61,13,29
        97,13,75,29,47
    """.trimIndent(),
    useInputFile = true
) {
    private val inputSections = input.linesGrouped().toList()

    private val pageOrderingRules: List<Pair<Int, Int>> = inputSections[0].map { line ->
        val splitLine = line.split("|")
        Pair(splitLine[0].toInt(), splitLine[1].toInt())
    }
    private val pageOrderingRulesByFirst =
        pageOrderingRules.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }
    private val updates: List<List<Int>> = inputSections[1].map { line -> line.split(",").map { it.toInt() } }


    private fun isUpdateCorrectlyOrdered(update: List<Int>): Boolean {
        val indexPairs = sequence {
            for (i1 in update.indices) {
                for (i2 in (i1 + 1)..<update.size) {
                    yield(Pair(i1, i2))
                }
            }
        }
        return indexPairs.none { (i1, i2) -> pageOrderingRulesByFirst[update[i2]]?.contains(update[i1]) ?: false }
    }

    private fun middleNumber(update: List<Int>): Int {
        return update[update.size / 2]
    }

    override fun part1(): Number {
        return updates.filter { isUpdateCorrectlyOrdered(it) }.sumOf { middleNumber(it) }
    }

    private fun reorderUpdate(update: List<Int>): List<Int> {
        val remainingPages = update.toMutableSet()
        val remainingRules =
            pageOrderingRulesByFirst
                .filterKeys { remainingPages.contains(it) }
                .mapValues { entry -> entry.value.filter { remainingPages.contains(it) } }
                .toMutableMap()
        val reorderedUpdate = mutableListOf<Int>()
        while (remainingPages.isNotEmpty()) {
            val possibleNextPages = remainingPages.filter { page -> remainingRules.none { it.value.contains(page) } }
            val nextPage = possibleNextPages.first()
            reorderedUpdate.add(nextPage)
            remainingPages.remove(nextPage)
            remainingRules.remove(nextPage)
        }
        return reorderedUpdate
    }

    override fun part2(): Number {
        return updates
            .filterNot { isUpdateCorrectlyOrdered(it) }
            .map { reorderUpdate(it) }
            .sumOf { update -> middleNumber(update) }
    }
}