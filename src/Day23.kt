fun main() {
    val testInput = readInput("Day23_test")
    val input = readInput("Day23")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 94)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 2362)
    }

    part2_2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 154)
    }

    part2_2(input).also {
        println("Part 2, real input: $it")
        check(it == 6538)
    }
}

private class DequeItem(
    val pos: Pos,
    val oldPath: Set<Pos>,
    val length: Int,
)

private fun part1(input: List<String>): Int {
    val tiles: Map<Pos, Char> = input.flatMapIndexed { row, str ->
        str.mapIndexed { column, c ->
            val pos = Pos(row, column)
            pos to c
        }
    }.toMap()
    val columnRange = tiles.minOf { it.key.col }..tiles.maxOf { it.key.col }
    val rowRange = tiles.minOf { it.key.row }..tiles.maxOf { it.key.row }

    val start = Pos(0, 1)
    val endPos = Pos(rowRange.last, columnRange.last - 1)
    val q = ArrayDeque<DequeItem>()
    q.add(DequeItem(start, setOf(), 0))
    var max = 0
    while (q.isNotEmpty()) {
        val i = q.removeLast()
        if (i.pos == endPos) {
            max = maxOf(max, i.length)
            continue
        }
        val next = when (tiles.getOrDefault(Pos(i.pos.row, i.pos.col), '#')) {
            '#' -> emptyList()
            '.' -> listOf(
                Pos(i.pos.row - 1, i.pos.col),
                Pos(i.pos.row + 1, i.pos.col),
                Pos(i.pos.row, i.pos.col - 1),
                Pos(i.pos.row, i.pos.col + 1),
            )

            '>' -> listOf(
                Pos(i.pos.row, i.pos.col + 1),
            )

            '<' -> listOf(
                Pos(i.pos.row, i.pos.col - 1),
            )

            '^' -> listOf(
                Pos(i.pos.row - 1, i.pos.col),
            )

            'v' -> listOf(
                Pos(i.pos.row + 1, i.pos.col),
            )

            else -> error("Unknown sybol")
        }
        next.filter { it !in i.oldPath }.forEach {
            q.add(
                DequeItem(
                    pos = it,
                    oldPath = i.oldPath + i.pos,
                    length = i.length + 1,
                ),
            )
        }
    }

    return max
}

private fun Map<Pos, Char>.getOutputPaths(pos: Pos): Set<Pos> = listOf(
    Pos(pos.row - 1, pos.col),
    Pos(pos.row + 1, pos.col),
    Pos(pos.row, pos.col - 1),
    Pos(pos.row, pos.col + 1),
).filter {
    this.getOrDefault(it, '#') != '#'
}.toSet()


val startPosition = Pos(0, 1)

private data class GraphPath(
    val from: Pos,
    val to: Pos,
    val cost: Int,
    val pred: Pos?,
)

private data class LGraph(
    val paths: List<GraphPath>,
) {
    val from: Map<Pos, MutableMap<Pos, Int>> = run {
        val map = mutableMapOf<Pos, MutableMap<Pos, Int>>()
        paths.forEach { g ->
            map.getOrPut(g.from) { mutableMapOf() }[g.to] = g.cost
            map.getOrPut(g.to) { mutableMapOf() }[g.from] = g.cost
        }
        map
    }
}

private fun getGraph(
    tiles: Map<Pos, Char>,
): LGraph {
    val columnRange = tiles.minOf { it.key.col }..tiles.maxOf { it.key.col }
    val rowRange = tiles.minOf { it.key.row }..tiles.maxOf { it.key.row }
    val endPos = Pos(rowRange.last, columnRange.last - 1)

    val visitedNodes = mutableSetOf<Pos>()
    val branches = mutableListOf<GraphPath>()
    val q = ArrayDeque<GraphPath>()
    q.add(GraphPath(from = startPosition, to = startPosition, cost = 0, pred = Pos(0, 0)))
    while (q.isNotEmpty()) {
        val i = q.removeLast()
        visitedNodes.add(i.pred!!)

        if (i.to == endPos) {
            branches.add(i)
            continue
        }

        val dst = (tiles.getOutputPaths(i.to) - i.pred).toList()
        if (dst.size == 1) {
            val dst1 = dst.first()
            val new = GraphPath(from = i.from, to = dst1, cost = i.cost + 1, pred = i.to)
            q.add(new)
        } else if (dst.size > 1) {
            branches.add(i)
            for (dstItem in dst) {
                if (!visitedNodes.contains(dstItem) && !visitedNodes.contains(i.to)) {
                    q.add(GraphPath(from = i.to, to = dstItem, cost = 1, pred = i.to))
                }
            }
        }
    }
    return LGraph(branches.map { it.copy(pred = null) })
}

private fun part2_2(input: List<String>): Int {
    val tiles: Map<Pos, Char> = input.flatMapIndexed { row, str ->
        str.mapIndexed { column, c ->
            val pos = Pos(row, column)
            pos to c
        }
    }.toMap()
    val columnRange = tiles.minOf { it.key.col }..tiles.maxOf { it.key.col }
    val rowRange = tiles.minOf { it.key.row }..tiles.maxOf { it.key.row }
    val endPos = Pos(rowRange.last, columnRange.last - 1)

    val graph = getGraph(tiles)
    val q = ArrayDeque<DequeItem>()
    q.add(DequeItem(startPosition, setOf(startPosition), 0))
    var max = 0
    while (q.isNotEmpty()) {
        val i = q.removeLast()
        if (i.pos == endPos) {
            max = maxOf(max, i.length)
            continue
        }

        val next2: MutableMap<Pos, Int> = graph.from[i.pos]!!
        next2.forEach {
            val newDst = it.key
            val costToDst = it.value
            if (newDst !in i.oldPath) {
                q.add(
                    DequeItem(
                        pos = it.key,
                        oldPath = i.oldPath + i.pos,
                        length = i.length + costToDst,
                    ),
                )
            }
        }
    }
    return max
}
