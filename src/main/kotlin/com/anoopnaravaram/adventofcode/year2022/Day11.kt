package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution
import linesGrouped

private object Regexes {
    val MONKEY_TITLE = "Monkey (\\d+):".toRegex()
    val STARTING_ITEMS = "Starting items: (.*)".toRegex()
    val OPERATION = "Operation: new = old (\\S+) (\\S+)".toRegex()
    val TEST = "Test: divisible by (\\d+)".toRegex()
    val TRUE_THROW = "If true: throw to monkey (\\d+)".toRegex()
    val FALSE_THROW = "If false: throw to monkey (\\d+)".toRegex()
}

private data class Notes(val monkeys: List<Monkey>) {
    companion object {
        fun fromString(notesStr: String): Notes {
            val monkeyNotesStrList = notesStr.linesGrouped().toList()
            return Notes(monkeyNotesStrList.map { Monkey.fromLines(it) })
        }
    }
}

private data class Monkey(
    val monkeyNumber: Int,
    val heldItems: MutableList<Long>,
    val operation: Operation,
    val testDivisor: Long,
    val trueMonkey: Int,
    val falseMonkey: Int
) {
    fun deepCopy(): Monkey {
        return Monkey(monkeyNumber, heldItems.toMutableList(), operation, testDivisor, trueMonkey, falseMonkey)
    }
    companion object {
        fun fromLines(lines: List<String>): Monkey {
            require(lines.size == 6) {
                "Invalid monkey notes. Expected 6 lines, found ${lines.size}."
            }

            val monkeyTitleMatch = requireNotNull(Regexes.MONKEY_TITLE.matchEntire(lines[0].trim())) {
                "Invalid monkey title \"${lines[0]}\". Expected \"Monkey <integer>:\"."
            }
            val monkeyNumberStr = monkeyTitleMatch.groups[1]!!.value
            val monkeyNumber = requireNotNull(monkeyNumberStr.toIntOrNull()) {
                "Invalid monkey number \"${lines[0]}\". Expected integer."
            }

            val startingItemsMatch = requireNotNull(Regexes.STARTING_ITEMS.matchEntire(lines[1].trim())) {
                "Invalid starting items \"${lines[1]}\". Expected \"Starting items: <integer>, <integer>, ...\"."
            }
            val startingItemsStr = startingItemsMatch.groups[1]!!.value
            val startingItemsStrList = startingItemsStr.split(",").map { it.trim() }
            val startingItems = startingItemsStrList.map { it.toLong() }.toMutableList()

            val operation = Operation.fromString(lines[2])

            val testDivisor = requireNotNull(Regexes.TEST.matchEntire(lines[3].trim())) {
                "Invalid test \"${lines[3]}\". Expected \"Test: divisible by <integer>\"."
            }.groups[1]!!.value.toLong()

            val trueThrowMatch = requireNotNull(Regexes.TRUE_THROW.matchEntire(lines[4].trim())) {
                "Invalid true throw \"${lines[4]}\". Expected \"If true: throw to monkey <integer>\"."
            }
            val trueMonkeyStr = trueThrowMatch.groups[1]!!.value
            val trueMonkey = requireNotNull(trueMonkeyStr.toIntOrNull()) {
                "Invalid true monkey \"$trueMonkeyStr\". Expected integer."
            }

            val falseThrowMatch = requireNotNull(Regexes.FALSE_THROW.matchEntire(lines[5].trim())) {
                "Invalid false throw \"${lines[5]}\". Expected \"If false: throw to monkey <integer>\"."
            }
            val falseMonkeyStr = falseThrowMatch.groups[1]!!.value
            val falseMonkey = requireNotNull(falseMonkeyStr.toIntOrNull()) {
                "Invalid false monkey \"$falseMonkeyStr\". Expected integer."
            }

            return Monkey(monkeyNumber, startingItems, operation, testDivisor, trueMonkey, falseMonkey)
        }
    }
}

private sealed interface Operation {
    fun apply(old: Long): Long
    data class AddOperation(val operand: Long) : Operation {
        override fun apply(old: Long): Long {
            return old + operand
        }
    }
    data class MultiplyOperation(val operand: Long) : Operation {
        override fun apply(old: Long): Long {
            return old * operand
        }
    }
    data object MultiplySelfOperation : Operation {
        override fun apply(old: Long): Long {
            return old * old
        }
    }


    companion object {
        fun fromString(operationStr: String): Operation {
            val match = Regexes.OPERATION.matchEntire(operationStr.trim())
            require(match != null) {
                "Invalid operation string \"$operationStr\". " +
                        "Expected \"Operation: new = old <operator> <operand>\"."
            }
            val operatorStr = match.groups[1]!!.value
            val operand2 = match.groups[2]!!.value
            return when {
                operatorStr == "*" && operand2 == "old" -> MultiplySelfOperation
                operatorStr == "*" -> MultiplyOperation(operand2.toLong())
                operatorStr == "+" -> AddOperation(operand2.toLong())
                else -> throw IllegalArgumentException("Invalid operator \"$operatorStr\". Expected \"*\" or \"+\".")
            }
        }
    }
}

class Day11: PuzzleSolution(
    inputFilePath = "input/2022/day11/input.txt",
    exampleInput = """
        Monkey 0:
          Starting items: 79, 98
          Operation: new = old * 19
          Test: divisible by 23
            If true: throw to monkey 2
            If false: throw to monkey 3

        Monkey 1:
          Starting items: 54, 65, 75, 74
          Operation: new = old + 6
          Test: divisible by 19
            If true: throw to monkey 2
            If false: throw to monkey 0

        Monkey 2:
          Starting items: 79, 60, 97
          Operation: new = old * old
          Test: divisible by 13
            If true: throw to monkey 1
            If false: throw to monkey 3

        Monkey 3:
          Starting items: 74
          Operation: new = old + 3
          Test: divisible by 17
            If true: throw to monkey 0
            If false: throw to monkey 1
    """.trimIndent(),
) {

    private val notes = Notes.fromString(input)

    // returns the amount of monkey business
    private fun runRounds(rounds: Int, reliefAfterInspection: Boolean): Long {
        val monkeys = notes.monkeys.map { it.deepCopy() }
        val divisorProduct = monkeys.map { it.testDivisor }.reduce(Long::times)
        val monkeyActivity = Array(monkeys.size) { 0L }
        repeat(rounds) {
            for ((index, monkey) in monkeys.withIndex()) {
                for (worryLevel in monkey.heldItems) {
                    monkeyActivity[index]++
                    var newValue = monkey.operation.apply(worryLevel)
                    if (reliefAfterInspection) {
                        newValue /= 3
                    } else {
                        newValue %= divisorProduct
                    }
                    val targetMonkey = when (newValue % monkey.testDivisor == 0L) {
                        true -> {
                            monkey.trueMonkey
                        }

                        false -> {
                            monkey.falseMonkey
                        }
                    }
                    monkeys[targetMonkey].heldItems.add(newValue)
                }
                monkey.heldItems.clear()
            }
        }
        return monkeyActivity.sortedArrayDescending().take(2).reduce(Long::times)
    }

    override fun part1(): Long {
        return runRounds(20, true)
    }

    override fun part2(): Long {
        return runRounds(10000, false)
    }
}