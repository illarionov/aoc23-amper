fun main() {
    val testInput = readInput("Day02_test")
        .map(String::parseGame)
    val input = readInput("Day02")
        .map(String::parseGame)

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 8)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 2006)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 2286)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 84911)
    }
}

private data class Game(
    val id: Int,
    val sets: List<Map<String, Int>>,
)

private fun String.parseGame(): Game {
    val id = this.substringAfter("Game ")
        .substringBefore(": ")
        .toInt()
    val sets = this.substringAfter(": ")
        .split("; ")
        .map { set ->
            val cubes = set.split(", ").associate {
                val (count, color) = it.split(" ")
                color to count.toInt()
            }
            cubes
        }
    return Game(id, sets)
}

private fun Game.isPossible(): Boolean {
    return this.sets.all {
        it.getOrDefault("red", 0) <= 12
                && it.getOrDefault("green", 0) <= 13
                && it.getOrDefault("blue", 0) <= 14
    }
}

private fun part1(input: List<Game>): Int {
    return input.filter(Game::isPossible).sumOf { it.id }
}

private fun part2(input: List<Game>): Int {
    return input.map { it.fewestCubes() }.sumOf {
        it.getOrDefault("red", 0) *
                it.getOrDefault("green", 0) *
                it.getOrDefault("blue", 0)
    }
}

private fun Game.fewestCubes(): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    sets.forEach { cubes ->
        cubes.forEach { (color, count) ->
            map[color] = maxOf(map.getOrDefault(color, 0), count)
        }
    }
    return map
}
