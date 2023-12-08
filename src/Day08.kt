import Instruction.LEFT
import Instruction.RIGHT

fun main() {
    val testInput = readInput("Day08_test").parse()
    val input = readInput("Day08").parse()

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 6L)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 21366921060721)
    }
}

private val networkNodeRegex = """(\w+) = \((\w+), (\w+)\)""".toRegex()

private fun List<String>.parse(): PuzzleMap {
    val instructions = this.first().map { if (it == 'L') LEFT else RIGHT }
    val network = this.drop(2).associate {
        val (src, leftNode, rightNode) = networkNodeRegex.find(it)?.destructured ?: error("No match `$it`")
        src to (leftNode to rightNode)
    }
    return PuzzleMap(instructions, network)
}

private enum class Instruction { LEFT, RIGHT }

private class PuzzleMap(
    val instructions: List<Instruction>,
    val network: Map<String, Pair<String, String>>,
)

private data class NodeInstruction(
    val node: String,
    val instructionNo: Int,
)

private data class CycleInfo(
    val node: String,
    val cycleStartStep: Long,
    val cycleEndStep: Long,
    val endElementStep: Long,
) {
    val size = cycleEndStep - cycleStartStep
    val endElementCyclePosition = endElementStep - cycleStartStep
}

private fun part1(input: PuzzleMap): Int {
    var steps = 0
    var node = "AAA"
    while (node != "ZZZ") {
        val instruction = input.instructions[steps % input.instructions.size]
        node = when (instruction) {
            LEFT -> input.network[node]!!.first
            RIGHT -> input.network[node]!!.second
        }
        steps += 1
    }
    return steps
}

private fun part2(input: PuzzleMap): Long {
    val startNodes = input.network.keys.filter { it.endsWith("A") }.toSet()
    val nodeCycles = startNodes.map { findCycle(input, it) }

    val minStep = nodeCycles.minOfOrNull { it.endElementCyclePosition } ?: error("No min")
    var step = nodeCycles.map { it.endElementStep }.lcm()

    while (nodeCycles.any { !isEndPositionAtStep(it, step) }) {
        step += minStep
    }

    return step
}

private fun isEndPositionAtStep(cycle: CycleInfo, step: Long): Boolean {
    if (step <= cycle.cycleStartStep) return step == cycle.endElementStep
    return cycle.endElementCyclePosition == (step - cycle.cycleStartStep) % cycle.size
}

private fun findCycle(
    puzzleMap: PuzzleMap,
    startNode: String,
): CycleInfo {
    val nodeHistory: MutableMap<NodeInstruction, Long> = mutableMapOf()
    var step = 0L
    var node: String = startNode
    var instructionNo = 0
    var endWithZPosition = -1L

    while (true) {
        val instruction = puzzleMap.instructions[instructionNo]
        val nodeInstruction = NodeInstruction(node, instructionNo)
        val oldPosition = nodeHistory[nodeInstruction]
        if (oldPosition != null) {
            return CycleInfo(
                node = startNode,
                cycleStartStep = oldPosition,
                cycleEndStep = step,
                endElementStep = endWithZPosition,
            )
        }
        if (node.endsWith("Z")) endWithZPosition = step

        nodeHistory[nodeInstruction] = step
        node = when (instruction) {
            LEFT -> puzzleMap.network[node]!!.first
            RIGHT -> puzzleMap.network[node]!!.second
        }
        step += 1
        instructionNo = (instructionNo + 1) % puzzleMap.instructions.size
    }
}

private fun List<Long>.lcm() = this.reduce { acc, item -> lcm(acc, item) }

private fun lcm(a: Long, b: Long): Long = a * (b / gdc(a, b))

private fun gdc(a: Long, b: Long): Long = if (b == 0L) a else gdc(b, a.mod(b))
