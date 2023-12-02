
fun main() {
    val testInput = readInput("Day03_test")
    val input = readInput("Day03")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 4361)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 521601)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 467835)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 80694070)
    }
}

private fun part1(input: List<String>): Int {
    val map: List<List<Char>> = input.map { it.toCharArray().toList() }
    var totalSum = 0
    for (row in map.indices) {
        var number = 0
        var numberStartPosition = -1
        var numberEndPosition = -1
        for (col in map[row].indices) {
            val c = map[row][col]
            when (c) {
                in '0' .. '9' -> {
                    if (numberStartPosition == -1) {
                        numberStartPosition = col
                        numberEndPosition = col
                    } else {
                        numberEndPosition = col
                    }
                    number = 10 * number + c.digitToInt()
                }
                else -> {
                    if (numberStartPosition != -1) {
                        if (map.hasAdjacentSymbol(row, numberStartPosition, numberEndPosition)) {
                            totalSum += number
                        }
                        numberStartPosition = -1
                        numberEndPosition = -1
                        number = 0
                    }
                }
            }
        }
        if (numberStartPosition != -1) {
            if (map.hasAdjacentSymbol(row, numberStartPosition, numberEndPosition)) {
                totalSum += number
            }
            numberStartPosition = -1
            numberEndPosition = -1
            number = 0
        }
    }
    return totalSum
}

private fun List<List<Char>>.hasAdjacentSymbol(row: Int, startCol: Int, endCol: Int): Boolean {
    val top = (row - 1).coerceAtLeast(0)
    val left = (startCol - 1).coerceAtLeast(0)
    val bottom = (row + 1).coerceAtMost(this.lastIndex)
    val right = (endCol + 1).coerceAtMost(this[0].lastIndex)
    return (top..bottom).any { row ->
        (left..right).any { column ->
            !(this[row][column] == '.' || this[row][column] in '0' .. '9')
        }
    }
}

private data class Pos(
    val row: Int,
    val col: Int
)

private data class NumberWithGear(
    val gear: Pos,
    val number: Int
)

private fun part2(input: List<String>): Int {
    val map: List<List<Char>> = input.map { it.toCharArray().toList() }
    return map.findAllNumbersWithGears()
        .groupBy { it.gear }
        .mapNotNull {
            if (it.value.size == 2) {
                it.value[0].number * it.value[1].number
            } else {
                null
            }
        }.sum()
}

private fun List<List<Char>>.findAllNumbersWithGears(): List<NumberWithGear> {
    var result = mutableListOf<NumberWithGear>()
    for (row in this.indices) {
        var number = 0
        var numberStartPosition = -1
        var numberEndPosition = -1
        for (col in this[row].indices) {
            val c = this[row][col]
            when (c) {
                in '0' .. '9' -> {
                    if (numberStartPosition == -1) {
                        numberStartPosition = col
                        numberEndPosition = col
                    } else {
                        numberEndPosition = col
                    }
                    number = 10 * number + c.digitToInt()
                }
                else -> {
                    if (numberStartPosition != -1) {
                        // println("Number: $number ")
                        result += this.getGears(row, numberStartPosition, numberEndPosition).map { NumberWithGear(it, number) }
                        numberStartPosition = -1
                        numberEndPosition = -1
                        number = 0
                    }
                }
            }
        }
        if (numberStartPosition != -1) {
            result += this.getGears(row, numberStartPosition, numberEndPosition).map { NumberWithGear(it, number) }
            numberStartPosition = -1
            numberEndPosition = -1
            number = 0
        }
    }
    return result
}

private fun List<List<Char>>.getGears(row: Int, startCol: Int, endCol: Int): List<Pos> {
    val top = (row - 1).coerceAtLeast(0)
    val left = (startCol - 1).coerceAtLeast(0)
    val bottom = (row + 1).coerceAtMost(this.lastIndex)
    val right = (endCol + 1).coerceAtMost(this[0].lastIndex)
    return (top..bottom).flatMap { row ->
        (left..right).mapNotNull { column ->
            if (this[row][column] == '*') {
                Pos(row, column)
            } else {
                null
            }
        }
    }
}
