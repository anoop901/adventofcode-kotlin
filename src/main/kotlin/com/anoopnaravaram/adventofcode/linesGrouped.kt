package com.anoopnaravaram.adventofcode

fun String.linesGrouped(): Sequence<List<String>> {
    val lines = this.lines()
    return lines.groupedBySeparator { it.isBlank() }
}