fun main() {
    val testInput = listOf(
        TimeDistance(0, 0),
        TimeDistance(0, 0),
        TimeDistance(0, 0),
    )

    val input = listOf(
        TimeDistance(0, 0),
        TimeDistance(0, 0),
        TimeDistance(0, 0),
        TimeDistance(0, 0),
    )
    val testInput2 = TimeDistance(0L, 0)
    val input2 = TimeDistance(0L, 0L)

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 288)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        // check(it == 1)
    }

    part2(testInput2).also {
        println("Part 2, test input: $it")
        check(it == 71503)
    }

    part2(input2).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}

private data class TimeDistance(val distance: Long, val raceRime: Long)

private fun part1(input: List<TimeDistance>): Int {
    return input.map(TimeDistance::getWinWays).reduce(Int::times)
}

private fun TimeDistance.getWinWays(): Int = (0..raceRime).count { initialHold ->
    val speed = initialHold
    val distance = (raceRime - initialHold) * speed
    distance > this.distance
}

private fun part2(input: TimeDistance): Int {
    return input.getWinWays()
}
