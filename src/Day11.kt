
fun main() {
    val testInput1 = readInput("Day11_test").parse()
    val testInput2 = readInput("Day11_test").parse(10)
    val input1 = readInput("Day11").parse()
    val input2 = readInput("Day11").parse(1000000)

    getShortedDistancesSum(testInput1).also {
        println("Part 1, test input: $it")
        check(it == 374L)
    }

    getShortedDistancesSum(input1).also {
        println("Part 1, real input: $it")
        check(it == 10077850L)
    }

    getShortedDistancesSum(testInput2).also {
        println("Part 2, test input: $it")
        check(it == 1030L)
    }

    getShortedDistancesSum(input2).also {
        println("Part 2, real input: $it")
        check(it == 504715068438L)
    }
}

private fun List<String>.parse(cost: Long = 2): Space {
    val space = this.map { it.toList() }
    val width = space[0].size
    val galaxies = space.flatMapIndexed { row, cols ->
        cols.mapIndexedNotNull { col, char ->
            if (char == '#') Pos(row, col) else null
        }
    }
    val emptyRows = space.mapIndexedNotNull { row, chars ->
        if (chars.all { it == '.' }) row else null
    }
    val emptyCols = (0..<width).mapNotNull { col ->
        if ((space.indices).all { row -> space[row][col] == '.' }) col else null

    }
    return Space(
        emptyRows = emptyRows,
        emptyCols = emptyCols,
        galaxies = galaxies,
        emtyCost = cost
    )
}

private class Space(
    val emptyRows: List<Int>,
    val emptyCols: List<Int>,
    val galaxies: List<Pos>,
    val emtyCost: Long = 2,
) {
    fun cost(row: Int, col: Int): Long = if (row in emptyRows || col in emptyCols) emtyCost else 1
}

private fun getShortedDistancesSum(space: Space): Long {
    var length: Long = 0
    for (bottomGalaxy in (space.galaxies.lastIndex downTo 1)) {
        for (topGalaxy in (bottomGalaxy - 1 downTo 0)) {
            val bottomPos = space.galaxies[bottomGalaxy]
            val topPos = space.galaxies[topGalaxy]
            val l = space.getShortestPath(bottomPos, topPos)
            length += l
        }
    }
    return length
}

private fun Space.getShortestPath(
    bottom: Pos,
    top: Pos,
): Long {
    val rowsCost = (top.row + 1 .. bottom.row).sumOf { cost(it, 0) }
    val cols = if (bottom.col < top.col) {
        bottom.col + 1 .. top.col
    } else {
        top.col + 1 .. bottom.col
    }
    val colsCost = cols.sumOf { cost(0, it) }
    return rowsCost + colsCost
}
