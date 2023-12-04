package com.anoopnaravaram.adventofcode.year2022

import com.anoopnaravaram.adventofcode.Coordinates
import com.anoopnaravaram.adventofcode.PuzzleSolution
import kotlin.math.absoluteValue

private data class SensorReport(val sensor: Coordinates, val closestBeacon: Coordinates)

private fun distance(p1: Coordinates, p2: Coordinates) = (p1.x - p2.x).absoluteValue + (p1.y - p2.y).absoluteValue

private const val specialRowY = 2000000
private const val searchSpaceSize = 4000000

private fun rangeOfRowWhereBeaconCannotExistDueToSensorReport(sensorReport: SensorReport, y: Int): IntRange? {
    val (sensor, closestBeacon) = sensorReport
    val dist = distance(sensor, closestBeacon)
    val yDist = (sensor.y - y).absoluteValue
    if (yDist > dist) {
        return null
    }
    val xDist = dist - yDist
    return sensor.x - xDist..sensor.x + xDist
}

private fun consolidateRanges(ranges: Iterable<IntRange>): List<IntRange> {
    val sortedRanges = ranges.sortedBy { it.first }

    return sequence {
        var consolidatedRange: IntRange? = null
        for (range in sortedRanges) {
            if (consolidatedRange == null) {
                consolidatedRange = range
            } else if (range.last > consolidatedRange.last) {
                if (range.first <= consolidatedRange.last + 1) {
                    consolidatedRange = consolidatedRange.first..range.last
                } else {
                    yield(consolidatedRange)
                    consolidatedRange = range
                }
            }
        }
        if (consolidatedRange != null) {
            yield(consolidatedRange)
        }
    }.toList()
}

class Day15 : PuzzleSolution(
    inputFilePath = "input/2022/day15/input.txt",
    exampleInput = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent(),
    useInputFile = true
) {
    private val sensorReports =
        input
            .trimEnd()
            .lines()
            .map { line ->
                val words = line.split(" ")
                val sensorCoordinates = Coordinates(
                    words[2].let { it.substring(2, it.length - 1) }.toInt(),
                    words[3].let { it.substring(2, it.length - 1) }.toInt()
                )
                val closestBeaconCoordinates = Coordinates(
                    words[8].let { it.substring(2, it.length - 1) }.toInt(),
                    words[9].substring(2).toInt()
                )
                SensorReport(sensorCoordinates, closestBeaconCoordinates)
            }

    override fun part1(): Number {
        val numBeaconsOnSpecialRow =
            sensorReports
                .asSequence()
                .map { it.closestBeacon }
                .filter { it.y == specialRowY }
                .toSet()
                .size
        sensorReports
            .mapNotNull { rangeOfRowWhereBeaconCannotExistDueToSensorReport(it, specialRowY) }
            .let { consolidateRanges(it) }
            .sortedBy { it.first }
            .sumOf { it.last - it.first + 1 }
            .let { it - numBeaconsOnSpecialRow }
            .also { return it }
    }

    override fun part2(): Number {
        (0..searchSpaceSize).flatMap { y ->
            sensorReports
                .mapNotNull { rangeOfRowWhereBeaconCannotExistDueToSensorReport(it, y) }
                .let { consolidateRanges(it) }
                .zipWithNext()
                .map { (range1, _) ->
                    val x = range1.last + 1
                    Coordinates(x, y)
                }
        }.first().also {
            val (x, y) = it
            return x.toLong() * searchSpaceSize + y
        }
    }
}