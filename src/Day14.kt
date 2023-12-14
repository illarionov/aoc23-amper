fun main() {
    val testInput = readInput("Day14_test").map { it.toList() }
    val input = readInput("Day14").map { it.toList() }

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 136)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        // check(it == 1)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 64)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}

private fun part1(input: List<List<Char>>): Int {
    val platform = rollNorth(input)

    var sum = 0
    for (row in platform.indices) {
        for (column in platform[row].indices) {
            if (platform[row][column] == 'O') {
                sum += platform.size - row
            }
        }
    }

    return sum
}

private fun part2(input: List<List<Char>>): Int {
    val history: MutableMap<List<List<Char>>, Int> = mutableMapOf()
    var platform = input

    var cycle = 0
    history[input] = 0
    while (cycle < 1000000000) {
        cycle += 1
        platform = roll(platform)
        val old = history[platform]
        if (old != null) {
            val size = old - cycle
            val full = (1000000000 - cycle) / size
            cycle += full * size
        } else {
            history[platform] = cycle
        }
    }
    val load = platform.totalLoad()
    return load
}

private fun List<List<Char>>.totalLoad(): Int {
    var sum = 0
    for (row in indices) {
        for (column in this[row].indices) {
            if (this[row][column] == 'O') {
                sum += this.size - row
            }
        }
    }
    return sum
}

private fun roll(input: List<List<Char>>): List<List<Char>> {
    var platform = rollNorth(input)
    platform = rollWest(platform)
    platform = rollSouth(platform)
    platform = rollEast(platform)
    return platform
}

private fun rollNorth(input: List<List<Char>>): List<List<Char>> {
    val platform = input.toMutableList().map { it.toMutableList() }
    for (row in input.indices) {
        for (column in input[row].indices) {
            if (platform[row][column] == 'O') {
                var newRow = row
                while (newRow > 0 && platform[newRow - 1][column] == '.') {
                    newRow -= 1
                }
                if (newRow < row) {
                    platform[newRow][column] = 'O'
                    platform[row][column] = '.'
                }
            }
        }
    }
    return platform
}

private fun rollWest(input: List<List<Char>>): List<List<Char>> {
    val platform = input.toMutableList().map { it.toMutableList() }
    for (column in input[0].indices) {
        for (row in input.indices) {
            if (platform[row][column] == 'O') {
                var newColumn = column
                while (newColumn > 0 && platform[row][newColumn - 1] == '.') {
                    newColumn -= 1
                }
                if (newColumn < column) {
                    platform[row][newColumn] = 'O'
                    platform[row][column] = '.'
                }
            }
        }
    }
    return platform
}

private fun rollSouth(input: List<List<Char>>): List<List<Char>> {
    val platform = input.toMutableList().map { it.toMutableList() }
    for (row in input.indices.reversed()) {
        for (column in input[row].indices) {
            if (platform[row][column] == 'O') {
                var newRow = row
                while (newRow < input.lastIndex && platform[newRow + 1][column] == '.') {
                    newRow += 1
                }
                if (newRow > row) {
                    platform[newRow][column] = 'O'
                    platform[row][column] = '.'
                }
            }
        }
    }
    return platform
}

private fun rollEast(input: List<List<Char>>): List<List<Char>> {
    val platform = input.toMutableList().map { it.toMutableList() }
    for (column in input[0].indices.reversed()) {
        for (row in input.indices) {
            if (platform[row][column] == 'O') {
                var newColumn = column
                while (newColumn < input[0].lastIndex && platform[row][newColumn + 1] == '.') {
                    newColumn += 1
                }
                if (newColumn > column) {
                    platform[row][newColumn] = 'O'
                    platform[row][column] = '.'
                }
            }
        }
    }
    return platform
}


private fun List<List<Char>>.print() {
    for (row in indices) {
        println(this[row].joinToString(""))
    }
}
