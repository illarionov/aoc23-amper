fun main() {
    val testInput = readInput("Day03_test").parse()
    val input = readInput("Day03").parse()

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 198)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 3429254)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 230)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 5410338)
    }
}

private fun List<String>.parse(): List<List<Int>> = this.map { string ->
    string.toCharArray().map { it.digitToInt() }
}

private fun part1(input: List<List<Int>>): Int {
    var gammaRate = 0
    var epsilonRate = 0
    for (col in 0 .. input[0].lastIndex) {
        val ones = (0..input.lastIndex).count { input[it][col] == 1 }
        val zeroes = (0..input.lastIndex).count { input[it][col] == 0 }
        val gammaBit = if (ones > zeroes) 1 else 0
        val epsilonBit = if (zeroes > ones) 1 else 0
        gammaRate = 2 * gammaRate + gammaBit
        epsilonRate = 2 * epsilonRate + epsilonBit
    }
    return gammaRate * epsilonRate
}

private fun part2(input: List<List<Int>>): Int {
    var col = 0
    var numbers: List<List<Int>> = input
    while (numbers.size != 1) {
        check(col < numbers[0].size)
        numbers = numbers.filterByMostCommonBitAtPosition(col)
        col += 1
    }
    val oxyGenRating = numbers[0].joinToString("").toInt(2)
    
    numbers = input
    col = 0
    while (numbers.size != 1) {
        check(col < numbers[0].size)
        numbers = numbers.filterByLeatCommonBitAtPosition(col)
        col += 1
    }
    val scrubberRating = numbers[0].joinToString("").toInt(2)
    
    return oxyGenRating * scrubberRating
}

private fun List<List<Int>>.filterByMostCommonBitAtPosition(column: Int): List<List<Int>> {
    val ones = (0..lastIndex).count { this[it][column] == 1 }
    val zeroes = (0..lastIndex).count { this[it][column] == 0 }
    
    val mostCommonBit = if (ones >= zeroes) 1 else 0
    return this.filter{ it[column] == mostCommonBit }
}

private fun List<List<Int>>.filterByLeatCommonBitAtPosition(column: Int): List<List<Int>> {
    val ones = (0..lastIndex).count { this[it][column] == 1 }
    val zeroes = (0..lastIndex).count { this[it][column] == 0 }

    val mostCommonBit = if (ones < zeroes ) 1 else 0
    return this.filter{ it[column] == mostCommonBit }
}