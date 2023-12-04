package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution

private data class Valve(val name: String, val flowRate: Int, val tunnelsLeadTo: List<String>)

class Day16 : PuzzleSolution(
    inputFilePath = "input/2022/day16/input.txt",
    exampleInput = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent(),
    useInputFile = false
) {
    private val valves =
        input
            .trimEnd()
            .lines()
            .associate { line ->
                val words = line.split(" ")
                val valveName = words[1]
                val flowRate = words[4].drop(5).dropLast(1).toInt()
                val tunnelsLeadTo = words.drop(9).joinToString("").split(",")
                valveName to Valve(valveName, flowRate, tunnelsLeadTo)
            }

    private fun maxFlow(): Int {
        val openedValves = mutableSetOf<String>()
        val traversedTunnels = valves.mapValues { mutableSetOf<String>() }
        fun maxFlowRecursive(currentValveName: String, timeLeft: Int, existingFlowRate: Int, pastFlow: Int): Int {
            println("current $currentValveName, time left: $timeLeft min")
            val currentValve = requireNotNull(valves[currentValveName])
            if (timeLeft >= 1) {
                return sequence {
                    if (!openedValves.contains(currentValveName) && currentValve.flowRate > 0) {
                        openedValves.add(currentValveName)
                        yield(
                            maxFlowRecursive(
                                currentValveName,
                                timeLeft - 1,
                                existingFlowRate + currentValve.flowRate,
                                pastFlow + existingFlowRate
                            )
                        )
                        openedValves.remove(currentValveName)
                    }
                    for (otherValveName in currentValve.tunnelsLeadTo) {
                        if (traversedTunnels[currentValveName]?.contains(otherValveName) != true) {
                            traversedTunnels[currentValveName]?.add(otherValveName)
                            yield(
                                maxFlowRecursive(
                                    otherValveName,
                                    timeLeft - 1,
                                    existingFlowRate,
                                    pastFlow + existingFlowRate
                                )
                            )
                            traversedTunnels[currentValveName]?.remove(otherValveName)
                        }
                    }
                }.maxOrNull() ?: (pastFlow + timeLeft * existingFlowRate)
            } else {
                return pastFlow
            }
        }
        return maxFlowRecursive("AA", 30, 0, 0)
    }

    override fun part1(): Number {
        return maxFlow()
    }

    override fun part2(): Number {
        TODO("Not yet implemented")
    }
}