fun main() {
    val testInput = readInput("Day25_test").parseWires()
    val input = readInput("Day25").parseWires()

    printCsvForGephi(input)

    bruteforce(testInput).also {
        println("Part 1, test input: $it")
        check(it == 54)
    }

    bruteforce(input).also {
        println("Part 1, real input: $it")
        check(it == 544523)
    }
}

data class Wire(
    val component1: String,
    val component2: String,
)

data class Graph(
    val wires: List<Wire>,
) {
    val from: Map<String, MutableSet<String>> = buildMap {
        wires.forEach {
            this.getOrPut(it.component1) { mutableSetOf() }.add(it.component2)
            this.getOrPut(it.component2) { mutableSetOf() }.add(it.component1)
        }
    }
}

private fun Graph.getGroupSizes(): List<Int> {
    val components = this.from.keys.toSet()
    if (components.isEmpty()) return emptyList()
    val groups = mutableListOf<Int>()
    var leftNodes = components
    while (leftNodes.isNotEmpty()) {
        val nextGroup = findConnectedComponents(leftNodes)
        groups.add(nextGroup.size)
        leftNodes = leftNodes - nextGroup
    }

    return groups
}

private fun Graph.findConnectedComponents(nodes: Set<String>): Set<String> {
    check(nodes.isNotEmpty())
    if (nodes.size == 1) return nodes
    val visited = mutableSetOf<String>()
    val q = ArrayDeque<String>()
    q.add(nodes.first())
    while (q.isNotEmpty()) {
        val node = q.removeFirst()
        visited.add(node)
        for (childNode in this.from[node]!!) {
            if (childNode !in visited && childNode in nodes) {
                q.add(childNode)
            }
        }
    }
    return visited
}

fun List<String>.parseWires(): List<Wire> = this.flatMap { str ->
    val fromComponent = str.substringBefore(": ")
    val to = str.substringAfter(": ").split(" ")
    to.map { toComponent ->
        if (fromComponent < toComponent) {
            Wire(fromComponent, toComponent)
        } else if (fromComponent > toComponent) {
            Wire(toComponent, fromComponent)
        } else {
            error("Link to the same component")
        }
    }
}.distinct()

private fun printCsvForGephi(input: List<Wire>) {
    input.forEach {
        println("${it.component1};${it.component2}")
    }
}

private fun bruteforce(input: List<Wire>): Int {
    val graph = Graph(input)
    println("from: ${graph.from}")

    val comparator = compareByDescending<Wire> { graph.from[it.component1]!!.size }
        .thenByDescending { graph.from[it.component1]!!.size }

    val wiresSorted = input
        .sortedWith(comparator)

    println("Wires sorted: ${wiresSorted}")

    (0..input.lastIndex - 2).forEach { wire1Index ->
        (wire1Index + 1..input.lastIndex - 1).forEach { wire2Index ->
            (wire2Index + 1 .. input.lastIndex).forEach { wire3Index ->
                val wiresToRemove = setOf(wiresSorted[wire1Index], wiresSorted[wire2Index], wiresSorted[wire3Index])
                val wires = input.filter { it !in wiresToRemove }
                val graph = Graph(wires)
                val threeLinksFroms = graph.from.count { it.value.size == 3 }

                if (threeLinksFroms == 2) {
                    val groupSizes = graph.getGroupSizes()
                    if (groupSizes.size == 2) {
                        return groupSizes[0] * groupSizes[1]
                    }
                }
            }
        }
    }
    return -1
}

private fun part2(input: List<Wire>): Int {
    return input.size
}
