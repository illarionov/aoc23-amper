import kotlin.math.abs

fun main() {
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 62L)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 40714L)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 952408144115L)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 129849166997110L)
    }
}

private data class PosXY(
    val x: Long,
    val y: Long,
)

private fun part1(input: List<String>): Long {
    val cmds = input.map { val (cmd, cnt, _) = it.split(" "); cmd[0] to cnt.toLong() }
    return getSquare(cmds)
}

private fun part2(input: List<String>): Long {
    val cmds = input.map {
        val (_, _, hexCnt) = it.split(" ");
        val color = hexCnt.substring(2, hexCnt.lastIndex)
        val cnt = color.dropLast(1).toInt(16)
        val direction = when (color.last()) {
            '0' -> 'R'
            '1' -> 'D'
            '2' -> 'L'
            '3' -> 'U'
            else -> error("Unknown direction ${color.last()}")
        }
        direction to cnt.toLong()
    }
    return getSquare(cmds)
}

private fun getSquare(
    cmds: List<Pair<Char, Long>>,
): Long {
    val trench = mutableListOf<PosXY>()
    var pos = PosXY(0, 0)
    trench.add(pos)
    var perimeter = 0L
    cmds.forEach { (cmd, cnt) ->
        pos = when (cmd) {
            'R' -> PosXY(y = pos.y, x = pos.x + cnt)
            'L' -> PosXY(y = pos.y, x = pos.x - cnt)
            'D' -> PosXY(y = pos.y + cnt, x = pos.x)
            'U' -> PosXY(y = pos.y - cnt, x = pos.x)
            else -> error("Wrong cmd")
        }
        trench.add(pos)
        perimeter += cnt
    }
    val s = trench.zipWithNext().sumOf { (x1y1, x2y2) ->
        x1y1.x * x2y2.y - x2y2.x * x1y1.y
    } / 2L

    return abs(s) + perimeter / 2L + 1L
}
