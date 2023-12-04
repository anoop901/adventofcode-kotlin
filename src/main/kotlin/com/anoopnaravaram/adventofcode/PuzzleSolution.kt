package com.anoopnaravaram.adventofcode

import java.io.File

abstract class PuzzleSolution(
    private val inputFilePath: String,
    private val exampleInput: String,
    private val useInputFile: Boolean = true,
) {

    val input by lazy {
        if (useInputFile) {
            val inputFile = File(inputFilePath)
            inputFile.readText()
        } else {
            exampleInput
        }
    }

    abstract fun part1(): Number
    abstract fun part2(): Number
}