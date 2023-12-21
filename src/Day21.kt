import kotlin.math.round

fun main() {
    val testInput = readInput("Day21_test").parse()
    val input = readInput("Day21").parse()

    part1(testInput, 6).also {
        println("Part 1, test input: $it")
        check(it == 16)
    }

    part1(input, 64).also {
        println("Part 1, real input: $it")
        check(it == 3733)
    }

    part2(testInput, 50).also {
        println("Part 2, test input: $it")
        check(it.reachable == 1594)
    }

    var reachableOld = 0
    for (i in 1..26501365) {
        val reachable = part2(input, i)
        println("reachable in $i points: $reachable")
    }

    part2(input, 26501365).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}

private data class PosStep(
    val row: Int,
    val column: Int,
    val step: Int,
)

private fun List<String>.parse(): Pair<Pos, List<List<Boolean>>> {
    val m = this@parse.map { it.toList() }
    var startPos: Pos? = null

    val l = this.mapIndexed { row, list ->
        list.mapIndexed { column, ch ->
            if (ch == 'S') startPos = Pos(row, column)
            ch == '.' || ch == 'S'
        }
    }
    return startPos!! to l
}

private fun part1(
    input: Pair<Pos, List<List<Boolean>>>,
    steps: Int = 64,
): Int {
    val reachable = mutableSetOf<PosStep>()
    val q = ArrayDeque<PosStep>()
    val (startPos, inputMap) = input
    val first = PosStep(startPos.row, startPos.col, 0)

    q.add(first)
    while (q.isNotEmpty()) {
        val p = q.removeLast()
        reachable += p
        if (p.step == steps) {
            continue
        }
        listOf(
            PosStep(p.row - 1, p.column, p.step + 1),
            PosStep(p.row + 1, p.column, p.step + 1),
            PosStep(p.row, p.column - 1, p.step + 1),
            PosStep(p.row, p.column + 1, p.step + 1),
        ).forEach {
            if (
                it.row in inputMap.indices &&
                it.column in inputMap[0].indices
                && inputMap[it.row][it.column]
                && !reachable.contains(it)
                ) {
                q.add(it)
            }
        }
    }

    return reachable.count { it.step == steps }
}

private data class Part2Stats(
    val steps: Int,
    val reachable: Int,

    val rocksOnMap: Int,
    val unreachableOnMap: Int,


    val rocksOnSquare: Int,
    val unreachableOnSquare: Int,
)

private fun part2(
    input: Pair<Pos, List<List<Boolean>>>,
    steps: Int = 64,
): Part2Stats {
    val reachable = mutableSetOf<PosStep>()
    val q = ArrayDeque<PosStep>()
    val (startPos, inputMap) = input
    val first = PosStep(startPos.row, startPos.col, 0)
    val rows = inputMap.size
    val columns = inputMap[0].size

    q.add(first)
    while (q.isNotEmpty()) {
        val p = q.removeLast()
        reachable += p
        if (p.step == steps) {
            continue
        }
        listOf(
            PosStep(p.row - 1, p.column, p.step + 1),
            PosStep(p.row + 1, p.column, p.step + 1),
            PosStep(p.row, p.column - 1, p.step + 1),
            PosStep(p.row, p.column + 1, p.step + 1),
        ).forEach {
            if (
                inputMap[it.row.mod(rows)][it.column.mod(columns)]
                && !reachable.contains(it)
            ) {
                q.add(it)
            }
        }
    }

    val reachable1 = reachable.count { it.step == steps }
    var rocksOnMap = 0
    var unreachableOnMap = 0
    for (row in inputMap.indices) {
        for (column in inputMap.indices) {
            val p = PosStep(row, column, steps)
            if (!inputMap[row][column]) rocksOnMap += 1
            if (inputMap[row][column] && !reachable.contains(p)) unreachableOnMap += 1
        }
    }

    var rocksOnSquare = 0
    var unreachableOnSquare = 0
    for (row in startPos.row - steps .. startPos.row + steps) {
        for (column in startPos.col - steps .. startPos.col + steps) {
            val p = PosStep(row, column, steps)
            if (!inputMap[row.mod(rows)][column.mod(columns)]) rocksOnSquare += 1
            if (inputMap[row.mod(rows)][column.mod(columns)] && !reachable.contains(p)) unreachableOnSquare += 1
        }
    }

    if (unreachableOnMap == 7559 || unreachableOnMap == 7547) {
        inputMap.printUnreachableMap(steps,reachable)
    }

    return Part2Stats(
        steps,
        reachable1,
        rocksOnMap,
        unreachableOnMap,
        rocksOnSquare,
        unreachableOnSquare
    )

}

private fun List<List<Boolean>>.printUnreachableMap(steps: Int, reachable: Set<PosStep>) {
    kotlin.io.println()
    this.forEachIndexed { row, str ->
        val s = str.mapIndexed {
                               column, isFree ->
            val p = PosStep(row, column, steps)
            if (isFree && !reachable.contains(p)) '*' else '.'
        }.joinToString("")
        println(s)
    }
}
