package com.anoopnaravaram.adventofcode.year2024

import com.anoopnaravaram.adventofcode.PuzzleSolution

class Day9 : PuzzleSolution(
    inputFilePath = "input/2024/day9/input.txt",
    exampleInput = "2333133121414131402",
    useInputFile = true
) {
    private val fileAndFreeSpaceSizes = input.trim().map { it.digitToInt() }

    private fun initializeDisk(): MutableList<Int?> {
        return sequence {
            for ((index, size) in fileAndFreeSpaceSizes.withIndex()) {
                val isFreeSpace = index % 2 == 1
                val fileId = index / 2
                yieldAll(List(size) { if (isFreeSpace) null else fileId})
            }
        }.toMutableList()
    }

    private fun consolidateDisk(disk: MutableList<Int?>) {
        var i1 = 0
        var i2 = disk.lastIndex
        while (true) {
            while (disk[i1] != null) {
                i1++
            }
            while (disk[i2] == null) {
                i2--
            }
            if (i1 >= i2) {
                break
            }
            // now, disk[i1] == null && disk[i2] != null && i1 < i2
            disk[i1] = disk[i2]
            disk[i2] = null
        }
    }

    private fun checksum(disk: List<Int?>): Long {
        return disk.withIndex().sumOf { (it.value ?: 0).toLong() * it.index.toLong() }
    }

    override fun part1(): Number {
        val disk = initializeDisk()
        consolidateDisk(disk)
        return checksum(disk)
    }

    private fun findFreeSpaces(): MutableList<Pair<Int, Int>> {
        return sequence {
            var i = 0
            var isFreeSpace = false
            for (digit in fileAndFreeSpaceSizes) {
                if (isFreeSpace) {
                    yield(Pair(i, digit))
                }
                i += digit
                isFreeSpace = !isFreeSpace
            }
        }.toMutableList()
    }

    private fun findFiles(): MutableList<Triple<Int, Int, Int>> {
        // start, size, id
        return sequence {
            var i = 0
            var isFreeSpace = false
            var fileId = 0
            for (digit in fileAndFreeSpaceSizes) {
                if (!isFreeSpace) {
                    yield(Triple(i, digit, fileId))
                }
                i += digit
                isFreeSpace = !isFreeSpace
                if (!isFreeSpace) {
                    fileId++
                }
            }
        }.toMutableList()
    }

    private fun checksumPartFromFile(file: Triple<Int, Int, Int>): Long {
        val (start, size, id) = file
        return (start..<start + size).sum().toLong() * id.toLong()
    }

    override fun part2(): Number {
        val freeSpaces = findFreeSpaces()
        val files = findFiles()
        files.reverse()
        for (fileIdx in files.indices) {
            val (fileStart, fileSize, fileId) = files[fileIdx]
            val freeSpaceIdx = freeSpaces.indexOfFirst { (_, freeSize) -> freeSize >= fileSize }
            val (freeStart, freeSize) = freeSpaces[freeSpaceIdx]
            if (freeStart < fileStart) {
                files[fileIdx] = Triple(freeStart, fileSize, fileId)
                freeSpaces[freeSpaceIdx] = Pair(freeStart + fileSize, freeSize - fileSize)
            }
        }
        return files.sumOf { checksumPartFromFile(it) }
    }
}

fun main() {
    val solution = Day9()
    println("part 1: ${solution.part1()}")
    println("part 2: ${solution.part2()}")
}
