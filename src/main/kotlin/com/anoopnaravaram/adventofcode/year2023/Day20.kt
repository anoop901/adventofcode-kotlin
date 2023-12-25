package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day20 : PuzzleSolution(
    inputFilePath = "input/2023/day20/input.txt",
    exampleInput = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
    """.trimIndent(),
//    useInputFile = false
) {

    private enum class PulseType { HIGH, LOW }

    private enum class ModuleType { BROADCASTER, FLIP_FLOP, CONJUNCTION }

    private enum class FlipFlopState { ON, OFF }

    private data class Module(val moduleType: ModuleType, val name: String, val destinationModuleNames: List<String>)

    private val modules = inputLines.map { line ->
        val (moduleNameAndPrefix, targetModulesStr) = line.split(" -> ")
        val destinationModuleNames = targetModulesStr.split(", ")
        val (moduleType, moduleName) = if (moduleNameAndPrefix == "broadcaster") {
            Pair(ModuleType.BROADCASTER, moduleNameAndPrefix)
        } else if (moduleNameAndPrefix.startsWith("%")) {
            Pair(ModuleType.FLIP_FLOP, moduleNameAndPrefix.drop(1))
        } else if (moduleNameAndPrefix.startsWith("&")) {
            Pair(ModuleType.CONJUNCTION, moduleNameAndPrefix.drop(1))
        } else {
            throw IllegalArgumentException()
        }
        Module(moduleType, moduleName, destinationModuleNames)
    }.associateBy { it.name }

    private data class Pulse(val fromModuleName: String, val toModuleName: String, val type: PulseType)

    private class ModuleStates(private val modules: Map<String, Module>) {
        val flipFlopStates: MutableMap<String, FlipFlopState> =
            modules.values.filter { it.moduleType == ModuleType.FLIP_FLOP }
                .associate { it.name to FlipFlopState.OFF }.toMutableMap()
        val conjunctionStates: MutableMap<String, MutableMap<String, PulseType>> =
            modules.values.filter { it.moduleType == ModuleType.CONJUNCTION }
                .associate { conjunctionModule ->
                    val fromModuleNames =
                        modules.values.filter { it.destinationModuleNames.contains(conjunctionModule.name) }
                    val conjunctionState = fromModuleNames.associate { it.name to PulseType.LOW }.toMutableMap()
                    conjunctionModule.name to conjunctionState
                }.toMutableMap()

        fun sendLowPulseToBroadcaster() = sequence {
            val pulsesQueue = ArrayDeque<Pulse>()
            pulsesQueue.addLast(Pulse("button", "broadcaster", PulseType.LOW))
            while (pulsesQueue.isNotEmpty()) {
                val pulse = pulsesQueue.removeFirst()
                yield(pulse)
                val (fromModuleName, toModuleName, pulseType) = pulse
                val toModule = modules[toModuleName] ?: continue

                fun sendPulse(pulseType: PulseType) {
                    toModule.destinationModuleNames.forEach {
                        pulsesQueue.addLast(Pulse(toModuleName, it, pulseType))
                    }
                }

                when (toModule.moduleType) {
                    ModuleType.BROADCASTER -> sendPulse(pulseType)
                    ModuleType.FLIP_FLOP -> {
                        if (pulseType == PulseType.LOW) {
                            when (flipFlopStates.getValue(toModuleName)) {
                                FlipFlopState.OFF -> {
                                    flipFlopStates[toModuleName] = FlipFlopState.ON
                                    sendPulse(PulseType.HIGH)
                                }

                                FlipFlopState.ON -> {
                                    flipFlopStates[toModuleName] = FlipFlopState.OFF
                                    sendPulse(PulseType.LOW)
                                }
                            }
                        }
                    }

                    ModuleType.CONJUNCTION -> {
                        val conjunctionState = conjunctionStates.getOrPut(toModuleName) {
                            modules.values
                                .filter { it.destinationModuleNames.contains(toModuleName) }
                                .associate { it.name to PulseType.LOW }
                                .toMutableMap()
                        }
                        conjunctionState[fromModuleName] = pulseType
                        if (conjunctionState.values.all { it == PulseType.HIGH }) {
                            sendPulse(PulseType.LOW)
                        } else {
                            sendPulse(PulseType.HIGH)
                        }
                    }
                }
            }
        }
    }

    override fun part1(): Number {
        val moduleStates = ModuleStates(modules)
        val pulseCounts = sequence {
            repeat(1000) {
                yieldAll(moduleStates.sendLowPulseToBroadcaster())
            }
        }.groupingBy { it.type }.eachCount().withDefault { 0 }
        return pulseCounts.getValue(PulseType.LOW) * pulseCounts.getValue(PulseType.HIGH)
    }

    override fun part2(): Number {
        // TODO: this doesn't work fast enough
        val moduleStates = ModuleStates(modules)
        return sequence {
            var timesButtonWasPressed = 0
            while (true) {
                timesButtonWasPressed++
                for (pulse in moduleStates.sendLowPulseToBroadcaster()) {
                    yield(Pair(timesButtonWasPressed, pulse))
                }
            }
        }.first { it.second.toModuleName == "rx" && it.second.type == PulseType.LOW }.first
    }
}

fun main() {
    val solution = Day20()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
