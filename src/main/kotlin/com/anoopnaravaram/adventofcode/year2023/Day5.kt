package com.anoopnaravaram.adventofcode.year2023

import com.anoopnaravaram.adventofcode.PuzzleSolution
import com.anoopnaravaram.adventofcode.linesGrouped

private data class AlmanacRange(
    val start: Long,
    val length: Long
) {
    val endExclusive = start + length

    operator fun times(that: AlmanacRange): AlmanacRange? {
        val start = maxOf(this.start, that.start)
        val endExclusive = minOf(this.endExclusive, that.endExclusive)
        return AlmanacRange(start, endExclusive - start).takeIf { start < endExclusive }
    }

    fun offset(delta: Long): AlmanacRange {
        return AlmanacRange(start + delta, length)
    }
}

private data class AlmanacRangeMapping(
    val destinationRangeStart: Long,
    val sourceRangeStart: Long,
    val rangeLength: Long
) {
    val sourceRange = AlmanacRange(sourceRangeStart, rangeLength)
    val offset = destinationRangeStart - sourceRangeStart
}

private data class AlmanacMap(
    val sourceCategory: String,
    val destinationCategory: String,
    val rangeMappings: List<AlmanacRangeMapping>
) {
    fun convertNumber(x: Long): Long {
        for (rangeMapping in rangeMappings) {
            if (x in rangeMapping.sourceRangeStart..<rangeMapping.sourceRangeStart + rangeMapping.rangeLength) {
                return x + rangeMapping.offset
            }
        }
        return x
    }

    fun convertRange(ranges: List<AlmanacRange>): List<AlmanacRange> = sequence {
        for (rangeToConvert in ranges) {
            for (rangeMapping in rangeMappings) {
                val intersection = (rangeMapping.sourceRange * rangeToConvert) ?: continue
                yield(intersection.offset(rangeMapping.offset))
            }
        }
    }.toList()
}

class Day5 : PuzzleSolution(
    inputFilePath = "input/2023/day5/input.txt",
    exampleInput = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent(),
) {
    private val seeds: List<Long>
    private val almanacMaps: List<AlmanacMap>

    init {
        val lineGroups = input.linesGrouped().toList()
        val initialSeedsLine = lineGroups[0][0]
        seeds = initialSeedsLine.split(" ").drop(1).map { it.toLong() }

        almanacMaps = lineGroups.drop(1).map { lines ->
            val (sourceCategory, _, destinationCategory) = lines[0].split(" ")[0].split("-")
            val ranges = lines.drop(1).map { line ->
                val (destinationRangeStart, sourceRangeStart, rangeLength) = line.split(" ").map { it.toLong() }
                AlmanacRangeMapping(destinationRangeStart, sourceRangeStart, rangeLength)
            }
            AlmanacMap(sourceCategory, destinationCategory, ranges)
        }
    }

    override fun part1(): Number {
        val seedLocations = seeds.map {
            almanacMaps.fold(it) { acc, almanacMap -> almanacMap.convertNumber(acc) }
        }
        return seedLocations.min()
    }

    private val seedRanges = seeds.chunked(2).map { (start, length) -> AlmanacRange(start, length) }
    override fun part2(): Number {
        val seedLocationRanges = almanacMaps.fold(seedRanges) { acc, almanacMap -> almanacMap.convertRange(acc) }
        return seedLocationRanges.minOf { it.start }
    }
}