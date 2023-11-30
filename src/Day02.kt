fun main() {
    val testInput = readInput("Day02_test").parse()
    val input = readInput("Day02").parse()

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 150)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 1427868)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 900)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 1568138742)
    }
}

private fun List<String>.parse(): List<Pair<String, Int>> = this.map {
    val (command, step) = it.split(" ", limit = 2);
    command to step.toInt()
}

private fun part1(input: List<Pair<String, Int>>): Int {
    var hp = 0
    var depth = 0
    input.forEach { (command, step) ->
            when (command) {
                "forward" -> hp += step
                "down" -> depth += step
                "up" -> depth -= step
            }
        }
    return hp * depth
}

private fun part2(input: List<Pair<String, Int>>): Int {
    var hp = 0
    var depth = 0
    var aim = 0
    input
        .forEach { (command, step) ->
            when (command) {
                "forward" -> {
                    hp += step
                    depth += aim * step
                }
                "down" -> aim += step
                "up" -> aim -= step
            }
        }
    return hp * depth
}

