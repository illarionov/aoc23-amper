import java.util.regex.Pattern

fun main() {
    val testInput = readInput("Day04_test")
        .parse()
    val input = readInput("Day04")
        .parse()

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 13)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 19135)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 30)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 5704953)
    }
}

private fun List<String>.parse(): List<Pair<Set<Int>, Set<Int>>> = map { str ->
    val numbers = str.substringAfter(": ")
    val winningNumbers = numbers.substringBefore(" | ")
        .trim()
        .split("""\s+""".toRegex(), 0)
        .map(String::toInt)
        .toSet()
    val havingNumber = numbers.substringAfter(" | ")
        .trim()
        .split("""\s+""".toRegex(), 0)
        .map(String::toInt)
        .toSet()
    winningNumbers to havingNumber
}

private fun part1(input: List<Pair<Set<Int>, Set<Int>>>): Int {
    return input.sumOf { (winningNumbers, havingNumbers) ->
        winningNumbers.fold<Int, Int>(0) { acc, value ->
            if (value in havingNumbers) {
                if (acc == 0) 1 else acc * 2
            } else {
                acc
            }
        }
    }
}

private fun part2(input: List<Pair<Set<Int>, Set<Int>>>): Int {
    val cards: MutableMap<Int, Int> = mutableMapOf()
    input.forEachIndexed { index, _ ->  cards[index + 1] = 1 }

    for (cardNo in 1..input.size) {
        val winningNumbers = input[cardNo - 1].getMatchingNumbersCount()
        val cardsCount = cards[cardNo]!!
        for (winCardNo in cardNo + 1 .. cardNo + winningNumbers) {
            cards[winCardNo]= cards[winCardNo]!! + cardsCount
        }
    }

    return cards.values.sum()
}

private fun Pair<Set<Int>, Set<Int>>.getMatchingNumbersCount(): Int {
    val (winninNumbers, havingNumbers) = this
    return winninNumbers.count { it in havingNumbers }
}
