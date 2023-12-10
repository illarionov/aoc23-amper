import Border.BOTTOM
import Border.BOTTOM_RIGHT
import Border.NO
import Border.RIGHT

fun main() {
    val testInput = readInput("Day10_test").parse()
    val testInput2 = readInput("Day10_test2").parse()
    val input = readInput("Day10").parse()

//    part1(testInput).also {
//        println("Part 1, test input: $it")
//        check(it == 8)
//    }
//
//    part1(input).also {
//        println("Part 1, real input: $it")
//        check(it == 6842)
//    }

    part2(testInput2).also {
        println("Part 2, test input: $it")
        check(it == 8)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}


private fun List<String>.parse(): Field = Field(this.map { it.toList() })

fun Field(
    tiles: List<List<Char>>,
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
    return getLongestPathWithPath(input).first
}

private fun getLongestPathWithPath(input: Field): Triple<Int, Set<Pos>, Char> {
    return listOf('|', '-', 'L', 'J', '7', 'F').map {
        val newField = input.withTileAtStartPoint(it)
        val path = newField.getLongestPath()
        Triple(path.first, path.second, it)
    }.maxBy { it.first }
}

private class DeqItem(
    val left: Pos,
    val right: Pos,
    val path: Set<Pos>,
    val distance: Int,
)

private fun Field.getLongestPath(): Pair<Int, Set<Pos>> {
    val startPos = this.startPoint
    val firstDirs = listOfNotNull(
        northIfCanTraverse(startPos),
        southIfCanTraverse(startPos),
        westIfCanTraverse(startPos),
        eastIfCanTraverse(startPos),
    )
    if (firstDirs.size < 2) return -1 to emptySet()

    val deq: ArrayDeque<DeqItem> = ArrayDeque()

    (0..<firstDirs.lastIndex).forEach { leftIndex ->
        (leftIndex + 1..firstDirs.lastIndex).forEach { rightIndex ->
            val leftPos = firstDirs[leftIndex]
            val rightPos = firstDirs[rightIndex]
            deq.add(
                DeqItem(leftPos, rightPos, setOf(startPos, leftPos, rightPos), 1),
            )
        }
    }

    var max = -1
    var maxPath: Set<Pos> = emptySet()
    while (deq.isNotEmpty()) {
        val path: DeqItem = deq.removeLast()
        if (path.left == path.right) {
            max = maxOf(path.distance, max)
            maxPath = path.path + path.left
            continue
        }

        val newLeft = listOfNotNull(
            northIfCanTraverse(path.left),
            southIfCanTraverse(path.left),
            westIfCanTraverse(path.left),
            eastIfCanTraverse(path.left),
        ).filter {
            it !in path.path
        }
        val newRight = listOfNotNull(
            northIfCanTraverse(path.right),
            southIfCanTraverse(path.right),
            westIfCanTraverse(path.right),
            eastIfCanTraverse(path.right),
        ).filter {
            it !in path.path
        }
        if (newLeft.isEmpty() || newRight.isEmpty()) {
            continue
        }
        newLeft.forEach { l ->
            newRight.forEach { r ->
                val item = DeqItem(
                    l,
                    r,
                    path.path + l + r,
                    path.distance + 1,
                )
                deq.add(item)
            }
        }
    }

    return max to maxPath
}

private enum class Border { NO, BOTTOM, RIGHT, BOTTOM_RIGHT }

private data class PosWithBorder(
    val row: Int,
    val column: Int,
    val border: Border,
)

private fun part2(input: Field): Int {
    val longestPathPart1 = getLongestPathWithPath(input)
    val path: Set<Pos> = longestPathPart1.second
    val field = input.withTileAtStartPoint(longestPathPart1.third)

    val pathExtended = path.flatMap {
        when (field[it]) {
            '|' -> listOf(
                PosWithBorder(it.row, it.col, NO),
                PosWithBorder(it.row, it.col, BOTTOM),
            )
            '-' -> listOf(
                PosWithBorder(it.row, it.col, NO),
                PosWithBorder(it.row, it.col, RIGHT),
            )
            'L' -> listOf(
                PosWithBorder(it.row, it.col, NO),
                PosWithBorder(it.row, it.col, RIGHT),
            )
            'J' -> listOf(
                PosWithBorder(it.row, it.col, NO),
            )
            '7' -> listOf(
                PosWithBorder(it.row, it.col, NO),
                PosWithBorder(it.row, it.col, BOTTOM),
            )
            'F' -> listOf(
                PosWithBorder(it.row, it.col, NO),
                PosWithBorder(it.row, it.col, RIGHT),
                PosWithBorder(it.row, it.col, BOTTOM),
            )
            else -> listOf(
                PosWithBorder(it.row, it.col, NO),
            )
        }
    }.toSet()

    val start = PosWithBorder(-1, -1, Border.NO)
    val outside: MutableSet<PosWithBorder> = mutableSetOf(start)
    val deq: ArrayDeque<PosWithBorder> = ArrayDeque()

    deq.add(start)
    outside.add(start)

    while (deq.isNotEmpty()) {
        val item = deq.removeLast()
        if (item in pathExtended) {
            continue
        }

        outside.add(item)
        field.adjacentOf(item).forEach {
            if (it !in outside) {
                deq.add(it)
            }
        }
    }
    field.printFieldWithPath(path, outside)

    return field.tiles.flatMapIndexed { row, columns ->
        columns.filterIndexed { column, value ->
            Pos(row, column) !in path && PosWithBorder(row, column, NO) !in outside
        }
    }.count()
}

private fun Field.adjacentOf(pos: PosWithBorder): List<PosWithBorder> = buildList {
    val fieldRows = this@adjacentOf.tiles.size
    val fieldCols = this@adjacentOf.tiles[0].size
    when (pos.border) {
        NO -> {
            // left
            if (pos.column > -1) add(
                PosWithBorder(row = pos.row, column = pos.column - 1, border = RIGHT),
            )
            //right
            add(PosWithBorder(row = pos.row, column = pos.column, border = RIGHT))

            // top
            if (pos.row > -1) add(
                PosWithBorder(row = pos.row - 1, column = pos.column, border = BOTTOM),
            )

            // bottom
            add(PosWithBorder(row = pos.row, column = pos.column, border = BOTTOM))
        }

        BOTTOM -> {
            // left
            if (pos.column > -1) add(
                PosWithBorder(row = pos.row, column = pos.column - 1, border = BOTTOM_RIGHT),
            )
            // Right
            add(PosWithBorder(row = pos.row, column = pos.column, border = BOTTOM_RIGHT))
            // Top
            add(PosWithBorder(row = pos.row, column = pos.column, border = NO))
            // Bottom
            if (pos.row <= fieldRows) {
                add(PosWithBorder(row = pos.row + 1, column = pos.column, border = NO))
            }
        }
        RIGHT -> {
            // Top
            if (pos.row > -1) add(
                PosWithBorder(row = pos.row - 1, column = pos.column, border = BOTTOM_RIGHT),
            )
            // Bottom
            add(PosWithBorder(row = pos.row, column = pos.column, border = BOTTOM_RIGHT))
            // left
            add(PosWithBorder(row = pos.row, column = pos.column, border = NO))
            // right
            if (pos.column <= fieldCols) {
                add(PosWithBorder(row = pos.row, column = pos.column + 1, border = NO))
            }
        }

        BOTTOM_RIGHT -> {
            // Top
            add(PosWithBorder(row = pos.row, column = pos.column, border = RIGHT))
            // Bottom
            if (pos.row <= fieldRows) {
                add(PosWithBorder(row = pos.row + 1, column = pos.column, border = RIGHT))
            }
            // Left
            add(PosWithBorder(row = pos.row, column = pos.column, border = BOTTOM))
            // Right
            if (pos.column <= fieldCols) {
                add(PosWithBorder(row = pos.row, column = pos.column + 1, border = BOTTOM))
            }
        }
    }
}

private fun Field.printFieldWithPath(path: Set<Pos>, outside: Set<PosWithBorder>) {
    this.tiles.forEachIndexed { row, rowChars ->
        val rowString = rowChars.mapIndexed { column, char ->
            if (Pos(row, column) in path) {
                when (char) {
                    'L' -> '└'
                    'J' -> '┘'
                    '7' -> '┐'
                    'F' -> '┌'
                    else -> char
                }
            } else if (PosWithBorder(row, column, NO) in outside){
                'O'
            } else {
                'I'
            }
        }.joinToString("")
        println(rowString)
    }
}
