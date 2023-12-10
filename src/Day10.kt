fun main() {
    val testInput = readInput("Day10_test").parse()
    val testInput2 = readInput("Day10_test2").parse()
    val input = readInput("Day10").parse()

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 8)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 6842)
    }

    part2(testInput2).also {
        println("Part 2, test input: $it")
        check(it == 10)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}


private fun List<String>.parse(): Field = Field(this.map { it.toList() })

fun Field(
    tiles: List<List<Char>>
): Field {
    val startPoint = run {
        for (row in tiles.indices) {
            for (col in tiles[row].indices) {
                if (tiles[row][col] == 'S') return@run Pos(row, col)
            }
        }
        error("No starting point")
    }
    return Field(tiles, startPoint)
}

class Field(
    val tiles: List<List<Char>>,
    val startPoint: Pos,
) {
    operator fun get(pos: Pos): Char = tiles[pos.row][pos.col]

    fun withTileAtStartPoint(tile: Char): Field {
        val newTiles = MutableList(tiles.size) { row -> tiles[row].toMutableList() }
        newTiles[startPoint.row][startPoint.col] = tile
        return Field(newTiles, startPoint)
    }
}

private val tilesHasOpenSouth = setOf('|', 'F', '7')
private val tilesHasOpenNorth = setOf('|', 'L', 'J')
private val tilesHasOpenWest = setOf('-', 'J', '7')
private val tilesHasOpenEast = setOf('-', 'F', 'L')

// South - North
private fun Field.northIfCanTraverse(pos: Pos): Pos? {
    val topPos = Pos(row = pos.row - 1, col = pos.col)
    if (topPos.row < 0) return null
    val canTraverse = this[pos] in tilesHasOpenNorth && this[topPos] in tilesHasOpenSouth
    return if (canTraverse) topPos else null
}

private fun Field.southIfCanTraverse(pos: Pos): Pos? {
    val southPos = Pos(row = pos.row + 1, col = pos.col)
    if (southPos.row > this.tiles.lastIndex) return null
    val canTraverse = this[pos] in tilesHasOpenSouth && this[southPos] in tilesHasOpenNorth
    return if (canTraverse) southPos else null
}

private fun Field.westIfCanTraverse(pos: Pos): Pos? {
    val westPos = Pos(row = pos.row, col = pos.col - 1)
    if (westPos.col < 0) return null
    val canTraverse = this[pos] in tilesHasOpenWest && this[westPos] in tilesHasOpenEast
    return if (canTraverse) westPos else null
}

private fun Field.eastIfCanTraverse(pos: Pos): Pos? {
    val eastPos = Pos(row = pos.row, col = pos.col + 1)
    if (eastPos.col > this.tiles[0].lastIndex) return null
    val canTraverse = this[pos] in tilesHasOpenEast && this[eastPos] in tilesHasOpenWest
    return if (canTraverse) eastPos else null
}

private fun part1(input: Field): Int {
    return listOf('|', '-', 'L', 'J', '7', 'F').maxOf {
        val newField = input.withTileAtStartPoint(it)
        newField.getLongestPath()
    }
}

private class DeqItem(
    val left: Pos,
    val right: Pos,
    val leftFrom: Pos,
    val rightFrom: Pos,
    val distance: Int
)

private fun Field.getLongestPath(): Int {
    val startPos = this.startPoint
    val firstDirs = listOfNotNull(
        northIfCanTraverse(startPos),
        southIfCanTraverse(startPos),
        westIfCanTraverse(startPos),
        eastIfCanTraverse(startPos),
    )
    if (firstDirs.size < 2) return -1

    val deq: ArrayDeque<DeqItem> = ArrayDeque()

    (0..<firstDirs.lastIndex).forEach { leftIndex ->
        (leftIndex + 1..firstDirs.lastIndex).forEach { rightIndex ->
            val leftPos = firstDirs[leftIndex]
            val rightPos = firstDirs[rightIndex]
            deq.add(
                DeqItem(leftPos, rightPos, startPos, startPos, 1)
            )
        }
    }

    var max = -1
    while (deq.isNotEmpty()) {
        val path: DeqItem = deq.removeLast()
        if (path.left == path.right) {
            max = maxOf(path.distance, max)
            continue
        }

        val newLeft = listOfNotNull(
            northIfCanTraverse(path.left),
            southIfCanTraverse(path.left),
            westIfCanTraverse(path.left),
            eastIfCanTraverse(path.left),
        ). filter {
            it != path.leftFrom && it != path.rightFrom
        }
        val newRight = listOfNotNull(
            northIfCanTraverse(path.right),
            southIfCanTraverse(path.right),
            westIfCanTraverse(path.right),
            eastIfCanTraverse(path.right),
        ).filter {
            it != path.leftFrom && it != path.rightFrom
        }
        if (newLeft.isEmpty() || newRight.isEmpty()) {
            continue
        }
        newLeft.forEach { l ->
            newRight.forEach { r ->
                val item = DeqItem(l, r, path.left, path.right, path.distance + 1)
                deq.add(item)
            }
        }
    }

    return max

}

private fun part2(input: Field): Int {
    return 0
}
