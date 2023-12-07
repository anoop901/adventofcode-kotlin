package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import java.lang.IllegalArgumentException

private enum class CamelCardsLabel(val char: Char) {
    LABEL_2('2'),
    LABEL_3('3'),
    LABEL_4('4'),
    LABEL_5('5'),
    LABEL_6('6'),
    LABEL_7('7'),
    LABEL_8('8'),
    LABEL_9('9'),
    LABEL_T('T'),
    LABEL_J('J'),
    LABEL_Q('Q'),
    LABEL_K('K'),
    LABEL_A('A');

    companion object {
        private val map = entries.associateBy { it.char }
        fun fromChar(char: Char) = requireNotNull(map[char])
    }
}

private enum class HandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND;

    companion object {
        fun fromLabelFrequencies(labelFrequencies: Collection<Int>): HandType {
            return when (labelFrequencies.sortedDescending()) {
                listOf(5) -> FIVE_OF_A_KIND
                listOf(4, 1) -> FOUR_OF_A_KIND
                listOf(3, 2) -> FULL_HOUSE
                listOf(3, 1, 1) -> THREE_OF_A_KIND
                listOf(2, 2, 1) -> TWO_PAIR
                listOf(2, 1, 1, 1) -> ONE_PAIR
                listOf(1, 1, 1, 1, 1) -> HIGH_CARD
                else -> throw IllegalArgumentException()
            }
        }
    }
}

private data class Hand(val cards: List<CamelCardsLabel>) {
    val type: HandType
        get() {
            val labelFrequencies = cards.groupingBy { it }.eachCount().values
            return HandType.fromLabelFrequencies(labelFrequencies)
        }

    val typeConsideringJokers: HandType
        get() {
            val numberOfJokers = cards.count { it == CamelCardsLabel.LABEL_J }
            val labelFrequencies =
                cards.filter { it != CamelCardsLabel.LABEL_J }.groupingBy { it }.eachCount().values.toMutableList()
            labelFrequencies.sortDescending()
            if (labelFrequencies.size == 0) {
                labelFrequencies.add(0)
            }
            labelFrequencies[0] += numberOfJokers
            return HandType.fromLabelFrequencies(labelFrequencies)
        }

    override fun toString() = cards.map { it.char }.joinToString("")
}

private data class HandAndBid(val hand: Hand, val bid: Int)

private fun <T : Comparable<T>> lexicographicComparator(): Comparator<Collection<T>> {
    return lexicographicComparator(naturalOrder())
}

private fun <T> lexicographicComparator(elementComparator: Comparator<T>): Comparator<Collection<T>> {
    return Comparator { collection1: Collection<T>, collection2: Collection<T> ->
        (collection1 zip collection2)
            .map { (e1, e2) -> elementComparator.compare(e1, e2) }
            .firstOrNull { it != 0 }
            ?: (collection1.size compareTo collection2.size)
    }
}

class Day7 : PuzzleSolution(
    inputFilePath = "input/2023/day7/input.txt",
    exampleInput = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent(),
) {
    private val handsAndBids = input.trimEnd().lines().map { line ->
        val (handStr, bid) = line.split(" ")
        HandAndBid(Hand(handStr.toCharArray().asList().map { CamelCardsLabel.fromChar(it) }), bid.toInt())
    }

    private fun totalWinnings(handComparator: Comparator<Hand>): Int {
        val sortedHandsAndBids = handsAndBids.sortedWith(compareBy(handComparator) { it.hand })
        return sortedHandsAndBids.withIndex().sumOf { (index, handAndBid) -> (index + 1) * handAndBid.bid }
    }

    override fun part1(): Number {
        return totalWinnings(
            compareBy<Hand> { it.type }
                    then compareBy(lexicographicComparator()) { it.cards }
        )
    }

    override fun part2(): Number {
        val jokersFirstLabelComparator =
            compareBy<CamelCardsLabel> { it != CamelCardsLabel.LABEL_J } then naturalOrder()
        return totalWinnings(
            compareBy<Hand> { it.typeConsideringJokers }
                    then compareBy(lexicographicComparator(jokersFirstLabelComparator)) { it.cards }
        )
    }
}