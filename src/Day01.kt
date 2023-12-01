fun main() {
    val testInput = readInput("Day01_test")
    val input = readInput("Day01")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 142)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 1)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 54885)
    }
}

private fun part1(input: List<String>): Int {
    return input.sumOf { string ->
        val a = string.find { it in '0'..'9' }!!.digitToInt()
        val b = string.findLast { it in '0'..'9' }!!.digitToInt()
        a * 10 + b
    }
}

private fun part2(input: List<String>): Int {
    return input.sumOf { string ->
        val a = string.indexOfAny(digits).let { string.substringToDigit(it) }
        val b = string.lastIndexOfAny(digits).let { string.substringToDigit(it) }
        a * 10 + b
    }
}

private val digits = ('0'..'9').map { it.toString() } + setOf(
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
)

private fun String.substringToDigit(position: Int): Int {
    if (this[position] in ('0'..'9')) {
        return this[position].digitToInt()
    }
    val ss = this.substring(position)
    return when {
        ss.startsWith("one") -> 1
        ss.startsWith("two") -> 2
        ss.startsWith("three") -> 3
        ss.startsWith("four") -> 4
        ss.startsWith("five") -> 5
        ss.startsWith("six") -> 6
        ss.startsWith("seven") -> 7
        ss.startsWith("eight") -> 8
        ss.startsWith("nine") -> 9
        else -> error("Unknown value $this")
    }
}