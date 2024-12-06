package com.anoopnaravaram.adventofcode.year2024

fun <T> List<T>.subListsOfSize(sublistSize: Int): Sequence<List<T>> = sequence {
    for (fromIndex in 0..size - sublistSize) {
        yield(subList(fromIndex, fromIndex + sublistSize))
    }
}