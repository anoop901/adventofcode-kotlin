package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken

private data class Valve(val name: String, val flowRate: Int, val tunnelsLeadTo: List<String>)

private object ValveGrammar : Grammar<Valve>() {
    private val valve by literalToken("Valve")
    private val valveNameToken by regexToken("[A-Z]+")
    private val hasFlowRate by regexToken("has flow rate")
    private val equal by literalToken("=")
    private val numberToken by regexToken("\\d+")
    private val semicolon by literalToken(";")
    private val tunnelsLeadToValves by regexToken("tunnels? leads? to valves?")
    private val comma by literalToken(",")
    private val space by regexToken("\\s+", ignore = true)

    override val tokenizer = DefaultTokenizer(listOf(valve, hasFlowRate, equal, semicolon, tunnelsLeadToValves, comma, numberToken, valveNameToken, space))

    private val valveName by valveNameToken use { text }
    private val number by numberToken use { text.toInt() }
    private val valveNamesList by separatedTerms(valveName, comma)

    override val rootParser =
        -valve * valveName * -hasFlowRate * -equal * number * -semicolon * -tunnelsLeadToValves * valveNamesList map { (name, flowRate, tunnelsLeadTo) ->
            Valve(
                name,
                flowRate,
                tunnelsLeadTo
            )
        }
}

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
    private val valves = input.trimEnd().lines().map { ValveGrammar.parseToEnd(it) }.associateBy { it.name }

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