import Dir.BOTTOM
import Dir.LEFT
import Dir.RIGHT
import Dir.TOP

fun main() {
    val testInput = readInput("Day16_test").map { it.toList() }
    val input = readInput("Day16").map { it.toList() }

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 46)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 7236)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 51)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 7521)
    }
}

private enum class Dir { LEFT, TOP, RIGHT, BOTTOM }

private data class Beam(val row: Int, val col: Int, val dir: Dir)

private fun Beam.next(): Beam = when (dir) {
    LEFT -> Beam(row, col - 1, dir)
    TOP -> Beam(row - 1, col, dir)
    RIGHT -> Beam(row, col + 1, dir)
    BOTTOM -> Beam(row + 1, col, dir)
}

private fun part1(input: List<List<Char>>): Int {
    val startPos = Beam(0, -1, RIGHT)
    return getEnergizedTiles(input, startPos)
}

private fun part2(input: List<List<Char>>): Int {
    val startPositions = (input.indices).map { Beam(it, -1, RIGHT) } +
            (input[0].indices).map { Beam(-1, it, BOTTOM) } +
            (input.indices).map { Beam(it, input[0].size, TOP) } +
            (input[0].indices).map { Beam(input.size, it, BOTTOM) }

    return startPositions.maxOf {
        getEnergizedTiles(input, it)
    }
}

private fun getEnergizedTiles(
    input: List<List<Char>>,
    startPos: Beam,
): Int {
    val energized = mutableMapOf<Pos, MutableSet<Dir>>()
    val beams = ArrayDeque<Beam>()
    beams.add(startPos)
    while (beams.isNotEmpty()) {
        var b = beams.removeFirst()
        b = b.next()
        if (b.col !in input.indices || b.row !in input[0].indices) continue

        val set = energized.getOrPut(Pos(b.row, b.col)) { mutableSetOf<Dir>() }
        if (set.contains(b.dir)) {
            continue
        } else {
            set.add(b.dir)
        }

        when (input[b.row][b.col]) {
            '.' -> beams.add(b)
            '|' -> {
                if (b.dir == LEFT || b.dir == RIGHT) {
                    beams.add(Beam(b.row, b.col, TOP))
                    beams.add(Beam(b.row, b.col, BOTTOM))
                } else {
                    beams.add(b)
                }
            }

            '-' -> {
                if (b.dir == TOP || b.dir == BOTTOM) {
                    beams.add(Beam(b.row, b.col, LEFT))
                    beams.add(Beam(b.row, b.col, RIGHT))
                } else {
                    beams.add(b)
                }
            }

            '\\' -> {
                val b2 = when (b.dir) {
                    LEFT -> Beam(b.row, b.col, TOP)
                    TOP -> Beam(b.row, b.col, LEFT)
                    RIGHT -> Beam(b.row, b.col, BOTTOM)
                    BOTTOM -> Beam(b.row, b.col, RIGHT)
                }
                beams.add(b2)
            }

            '/' -> {
                val b2 = when (b.dir) {
                    LEFT -> Beam(b.row, b.col, BOTTOM)
                    TOP -> Beam(b.row, b.col, RIGHT)
                    RIGHT -> Beam(b.row, b.col, TOP)
                    BOTTOM -> Beam(b.row, b.col, LEFT)
                }
                beams.add(b2)
            }
        }
    }

    return energized.keys.size
}
