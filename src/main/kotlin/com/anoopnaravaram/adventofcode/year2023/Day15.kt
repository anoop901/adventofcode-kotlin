package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution

private fun hash(string: String): Int {
    var current = 0
    for (char in string) {
        current += char.code
        current *= 17
        current %= 256
    }
    return current
}

class Day15 : PuzzleSolution(
    inputFilePath = "input/2023/day15/input.txt",
    exampleInput = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7",
//    useInputFile = false
) {
    val steps = input.trimEnd().split(",")
    override fun part1(): Number {
        return steps.sumOf { hash(it) }
    }

    override fun part2(): Number {
        val boxes = List(256) { mutableListOf<Pair<String, Int>>() }
        for (step in steps) {
            if (step.contains('=')) {
                val label = step.dropLast(2)
                val focalLength = step.last().digitToInt()
                val boxNumber = hash(label)

                val existingLensIndex = boxes[boxNumber].indexOfFirst { it.first == label }
                if (existingLensIndex == -1) {
                    boxes[boxNumber].add(Pair(label, focalLength))
                } else {
                    boxes[boxNumber][existingLensIndex] = Pair(boxes[boxNumber][existingLensIndex].first, focalLength)
                }
            } else if (step.contains('-')) {
                val label = step.dropLast(1)
                val boxNumber = hash(label)
                boxes[boxNumber].indexOfFirst { it.first == label }.takeIf { it != -1 }?.let {
                    boxes[boxNumber].removeAt(it)
                }
            }
        }
        return boxes.mapIndexed { boxIndex, box ->
            box.mapIndexed { slotIndex, lens -> (boxIndex + 1) * (slotIndex + 1) * lens.second }.sum()
        }.sum()
    }
}