fun main() {
    val testInput = readInput("Day00_test")
    val input = readInput("Day00")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 1)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        // check(it == 1)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        // check(it == 1)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}

private fun part1(input: List<String>): Int {
    return input.size
}

private fun part2(input: List<String>): Int {
    return input.size
}