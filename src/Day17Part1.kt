import CrucibleDirection.DOWN
import CrucibleDirection.LEFT
import CrucibleDirection.RIGHT
import CrucibleDirection.UP
import kotlin.math.min

fun main() {
    val testInput = readInput("Day17_test").map { it.toList().map { it.toString().toInt() } }
    val input = readInput("Day17").map { it.toList().map { it.toString().toInt() } }

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 102)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 674)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 94)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 773)
    }
}

private enum class CrucibleDirection {
    DOWN, LEFT, RIGHT, UP
}

private data class CrucibleState(
    val dir: CrucibleDirection,
    val blockNo: Int,
)

private data class DpPos(
    val row: Int,
    val col: Int,
    val state: CrucibleState,
)

private fun part1(input: List<List<Int>>): Int {
    return Day17Part1(input).getMinPath()
}

private open class Day17Part1(
    val input: List<List<Int>>,
    val crucibleStates: List<CrucibleState> = CrucibleDirection.entries.flatMap { dir ->
        (1..3).map { CrucibleState(dir, it) }
    },
) {
    fun getMinPath(): Int {
        val endEntries = crucibleStates
            .filter { it.dir == RIGHT  || it.dir == DOWN }
            .map {
                DpPos(input.lastIndex, input[0].lastIndex, it)
            }

        var pathLimit = Int.MAX_VALUE
        endEntries.forEach {
            val minPath = finMinPath(it, pathLimit)
            println("${System.currentTimeMillis() / 1000}: end: $it minPath: $minPath")
            pathLimit = min(pathLimit, minPath)
        }
        return pathLimit
    }

    private fun finMinPath(
        dst: DpPos,
        pathLimit: Int = Int.MAX_VALUE,
    ): Int {
        val q: ArrayDeque<Pair<DpPos, Int>> = ArrayDeque()

        val minPositions: MutableMap<DpPos, Int> = mutableMapOf()

        val startEntries = listOf(
            DpPos(0, 0, CrucibleState(RIGHT, 1)),
            DpPos(0, 0, CrucibleState(DOWN, 1)),
        )
        startEntries.forEach { minPositions[it] = 0 }

        var maxPath = pathLimit
        q.addAll(startEntries.map { it to 0 })
        while (q.isNotEmpty()) {
            val (from, path) = q.removeLast()
            if (from == dst) {
                maxPath = min(maxPath, path)
                continue
            }
            val to = getTo(from)
            if (to.isEmpty()) continue
            to.forEach {
                val newPath = path + input[it.row][it.col]
                if (newPath <= maxPath) {
                    val old = minPositions[it]
                    if (old == null || old > newPath) {
                        minPositions[it] = newPath
                        q.add(it to newPath)
                    }
                }
            }
        }
        return maxPath
    }

    open fun getTo(src: DpPos): List<DpPos> = buildList {
        when (src.state.dir) {
            DOWN -> {
                if (src.state.blockNo < 3) {
                    add(src.row + 1, src.col, CrucibleState(DOWN, src.state.blockNo + 1))
                }
                add(src.row, src.col - 1, CrucibleState(LEFT, 1))
                add(src.row, src.col + 1, CrucibleState(RIGHT, 1))
            }

            LEFT -> {
                if (src.state.blockNo < 3) {
                    add(src.row, src.col - 1, CrucibleState(LEFT, src.state.blockNo + 1))
                }
                add(src.row - 1, src.col, CrucibleState(UP, 1))
                add(src.row + 1, src.col, CrucibleState(DOWN, 1))
            }

            RIGHT -> {
                if (src.state.blockNo < 3) {
                    add(src.row, src.col + 1, CrucibleState(RIGHT, src.state.blockNo + 1))
                }
                add(src.row - 1, src.col, CrucibleState(UP, 1))
                add(src.row + 1, src.col, CrucibleState(DOWN, 1))
            }

            UP -> {
                if (src.state.blockNo < 3) {
                    add(src.row - 1, src.col, CrucibleState(UP, src.state.blockNo + 1))
                }
                add(src.row, src.col - 1, CrucibleState(LEFT, 1))
                add(src.row, src.col + 1, CrucibleState(RIGHT, 1))
            }
        }
    }

    protected fun MutableList<DpPos>.add(row: Int, col: Int, dir: CrucibleState) {
        if (row !in input.indices || col !in input[0].indices) return
        add(DpPos(row, col, dir))
    }
}

private class Day17Part2(
    input: List<List<Int>>,
) : Day17Part1(
    input = input,
    crucibleStates = CrucibleDirection.entries.flatMap { dir ->
        (1..10).map { CrucibleState(dir, it) }
    },
) {
    override fun getTo(src: DpPos): List<DpPos> = buildList {
        when (src.state.dir) {
            DOWN -> {
                if (src.state.blockNo < 10) {
                    add(src.row + 1, src.col, CrucibleState(DOWN, src.state.blockNo + 1))
                }
                if (src.state.blockNo >= 4) {
                    add(src.row, src.col - 1, CrucibleState(LEFT, 1))
                    add(src.row, src.col + 1, CrucibleState(RIGHT, 1))
                }
            }

            LEFT -> {
                if (src.state.blockNo < 10) {
                    add(src.row, src.col - 1, CrucibleState(LEFT, src.state.blockNo + 1))
                }
                if (src.state.blockNo >= 4) {
                    add(src.row - 1, src.col, CrucibleState(UP, 1))
                    add(src.row + 1, src.col, CrucibleState(DOWN, 1))
                }
            }

            RIGHT -> {
                if (src.state.blockNo < 10) {
                    add(src.row, src.col + 1, CrucibleState(RIGHT, src.state.blockNo + 1))
                }
                if (src.state.blockNo >= 4) {
                    add(src.row - 1, src.col, CrucibleState(UP, 1))
                    add(src.row + 1, src.col, CrucibleState(DOWN, 1))
                }
            }

            UP -> {
                if (src.state.blockNo < 10) {
                    add(src.row - 1, src.col, CrucibleState(UP, src.state.blockNo + 1))
                }
                if (src.state.blockNo >= 4) {
                    add(src.row, src.col - 1, CrucibleState(LEFT, 1))
                    add(src.row, src.col + 1, CrucibleState(RIGHT, 1))
                }
            }
        }
    }
}

private fun part2(input: List<List<Int>>): Int {
    return Day17Part2(input).getMinPath()
}
