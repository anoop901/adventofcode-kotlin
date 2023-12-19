package com.anoopnaravaram.adventofcode

data class Grid<T>(val cells: List<List<T>>) {

    val height = cells.size
    val width = cells.getOrNull(0)?.size ?: 0

    init {
        require(cells.all { it.size == width }) { "failed to construct grid because not all rows are the same width" }
    }

    operator fun get(coordinates: Coordinates): T {
        return requireNotNull(getOrNull(coordinates))
    }

    fun getOrNull(coordinates: Coordinates): T? {
        return cells.getOrNull(coordinates.y)?.getOrNull(coordinates.x)
    }

    fun rowCoordinates(y: Int): Sequence<Coordinates> = sequence {
        for (x in 0..<width) {
            val coordinates = Coordinates(x, y)
            yield(coordinates)
        }
    }

    fun rowWithCoordinates(y: Int): Sequence<Pair<Coordinates, T>> = sequence {
        for (coordinates in rowCoordinates(y)) {
            yield(Pair(coordinates, get(coordinates)))
        }
    }

    fun row(y: Int): Sequence<T> = sequence {
        for (coordinates in rowCoordinates(y)) {
            yield(get(coordinates))
        }
    }

    fun columnCoordinates(x: Int): Sequence<Coordinates> = sequence {
        for (y in 0..<height) {
            val coordinates = Coordinates(x, y)
            yield(coordinates)
        }
    }

    fun columnWithCoordinates(x: Int): Sequence<Pair<Coordinates, T>> = sequence {
        for (coordinates in columnCoordinates(x)) {
            yield(Pair(coordinates, get(coordinates)))
        }
    }

    fun column(x: Int): Sequence<T> = sequence {
        for (coordinates in columnCoordinates(x)) {
            yield(get(coordinates))
        }
    }

    fun inBounds(coordinates: Coordinates): Boolean {
        return coordinates.x in 0..<width && coordinates.y in 0..<height
    }

    companion object {
        fun parse(string: String): Grid<Char> {
            return parse(string.lines())
        }

        fun parse(lines: List<String>): Grid<Char> {
            return parse(lines) { it }
        }

        fun <T> parse(string: String, parseChar: (Char) -> T): Grid<T> {
            return parse(string.lines(), parseChar)
        }

        fun <T> parse(lines: List<String>, parseChar: (Char) -> T): Grid<T> {
            val cells = lines.map { it.map(parseChar) }
            return Grid(cells)
        }
    }
}