fun main() {
    val testInput = readInput("Day09_test").map { it.split(" ").map { it.toLong() } }
    val input = readInput("Day09").map { it.split(" ").map { it.toLong() } }

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 114L)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        // check(it == 1)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 2L)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}

private fun part1(input: List<List<Long>>): Long {
    return input.sumOf { it.predictNextValue() }
}

private fun List<Long>.predictNextValue(): Long {
    val q: ArrayDeque<List<Long>> = ArrayDeque()
    q.add(this)

    while (q.last().any { it != 0L}) {
        val n = q.last().windowed(2).map { it[1] - it[0] }
        q.add(n)
    }

    var bottom = 0L
    var predicted = 0L
    while (q.isNotEmpty()) {
        predicted = q.removeLast().last() + bottom
        bottom = predicted
    }
    return predicted
}

private fun part2(input: List<List<Long>>): Long {
    return input.sumOf { it.predictPredValue() }
}

private fun List<Long>.predictPredValue(): Long {
    val q: ArrayDeque<List<Long>> = ArrayDeque()
    q.add(this)

    while (q.last().any { it != 0L}) {
        val n = q.last().windowed(2).map { it[1] - it[0] }
        q.add(n)
    }

    var bottom = 0L
    var predicted = 0L
    while (q.isNotEmpty()) {
        predicted = q.removeLast().first() - bottom
        bottom = predicted
    }
    return predicted
}
