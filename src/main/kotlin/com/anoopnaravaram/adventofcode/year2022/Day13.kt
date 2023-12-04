package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution

private sealed class PacketData {
    data class PacketInteger(val value: Int) : PacketData() {
        override fun toString(): String {
            return value.toString()
        }
    }

    data class PacketList(val values: MutableList<PacketData>) : PacketData() {
        override fun toString(): String {
            return values.joinToString(prefix = "[", postfix = "]", separator = ",")
        }
    }

    companion object {
        fun parse(input: String): PacketData {
            val tokenRegex = Regex("""(\d+|\[|])""")
            val tokens = tokenRegex.findAll(input).map { it.value }.toList()
            val stack = mutableListOf<PacketList>()
            var result: PacketData? = null
            for (token in tokens) {
                when (token) {
                    "[" -> {
                        val newList = PacketList(mutableListOf())
                        if (stack.isNotEmpty()) {
                            val top = stack.last()
                            top.values.add(newList)
                        }
                        stack.add(newList)
                    }

                    "]" -> {
                        result = stack.removeLast()
                    }

                    else -> {
                        val value = token.toInt()
                        val top = stack.last()
                        top.values.add(PacketInteger(value))
                    }
                }
            }
            return result!!
        }
    }
}

private object packetComparator : Comparator<PacketData> {
    override fun compare(o1: PacketData, o2: PacketData): Int {
        return compare(o1, o2, 0)
    }

    private fun compare(o1: PacketData, o2: PacketData, numIndents: Int = 0): Int {
        return when {
            o1 is PacketData.PacketInteger && o2 is PacketData.PacketInteger -> o1.value.compareTo(o2.value)
            o1 is PacketData.PacketInteger && o2 is PacketData.PacketList -> compare(
                PacketData.PacketList(mutableListOf(o1)),
                o2,
                numIndents + 1
            )

            o1 is PacketData.PacketList && o2 is PacketData.PacketInteger -> compare(
                o1,
                PacketData.PacketList(mutableListOf(o2)),
                numIndents + 1
            )

            o1 is PacketData.PacketList && o2 is PacketData.PacketList -> {
                val o1Size = o1.values.size
                val o2Size = o2.values.size
                for (i in 0..<minOf(o1Size, o2Size)) {
                    val o1Value = o1.values[i]
                    val o2Value = o2.values[i]
                    val compare = compare(o1Value, o2Value, numIndents + 1)
                    if (compare != 0) {
                        return compare
                    }
                }
                return o1Size.compareTo(o2Size)
            }

            else -> throw IllegalStateException("unreachable")
        }
    }
}

class Day13 : PuzzleSolution(
    inputFilePath = "input/2022/day13/input.txt",
    exampleInput = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent(),
) {

    private val packetDataPairs: List<Pair<PacketData, PacketData>> =
        input.lines().chunked(3).map { chunk ->
            val packetData1 = PacketData.parse(chunk[0])
            val packetData2 = PacketData.parse(chunk[1])
            Pair(packetData1, packetData2)
        }

    override fun part1(): Int {
        return packetDataPairs.withIndex()
            .map { IndexedValue(it.index + 1, it.value) }
            .filter {
                packetComparator.compare(it.value.first, it.value.second) < 0
            }.sumOf { (index, _) -> index }
    }

    override fun part2(): Int {
        val dividerPackets = listOf(
            PacketData.PacketList(mutableListOf(PacketData.PacketList(mutableListOf(PacketData.PacketInteger(2))))),
            PacketData.PacketList(mutableListOf(PacketData.PacketList(mutableListOf(PacketData.PacketInteger(6)))))
        )
        val allPackets = sequence {
            for (pair in packetDataPairs) {
                yield(pair.first)
                yield(pair.second)
            }
            yieldAll(dividerPackets)
        }
        val sortedPackets = allPackets.sortedWith(packetComparator)
        return dividerPackets.map { divider -> sortedPackets.indexOfFirst { it == divider } + 1 }.reduce(Int::times)
    }
}
