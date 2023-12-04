package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.github.h0tk3y.betterParse.grammar

private data class Scratchcard(val winningNumbers: List<Int>, val myNumbers: List<Int>) {
    val numWinningNumbers: Int
        get() {
            val winningNumbersSet = winningNumbers.toSet()
            return myNumbers.count { winningNumbersSet.contains(it) }
        }
}

object ScratchcardGrammar: Grammar<Scratchcard> {}

class Day4 : PuzzleSolution(
    inputFilePath = "input/2023/day4/input.txt",
    exampleInput = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent(),
) {
    private val cards: List<Scratchcard> = input.trimEnd().lines().map { line ->
        val words = line.split(" ").filter { it != "" }
        val numWinningWords = words.indexOf("|") - 2
        val winningNumbers = words.slice(2..<2 + numWinningWords).map { it.toInt() }
        val myNumbers = words.run { slice(2 + numWinningWords + 1..<size) }.map { it.toInt() }
        Scratchcard(winningNumbers, myNumbers)
    }

    override fun part1(): Number {
        return cards.sumOf { card ->
            when (card.numWinningNumbers) {
                0 -> 0
                else -> 1 shl card.numWinningNumbers - 1
            }
        }
    }

    override fun part2(): Number {
        val cardCounts = MutableList(cards.size) { 1 }
        for ((i, card) in cards.withIndex()) {
            for (j in i + 1..<i + 1 + card.numWinningNumbers) {
                cardCounts[j] += cardCounts[i]
            }
        }
        return cardCounts.sum()
    }
}
