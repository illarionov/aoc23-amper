fun main() {
    val testInput = readInput("Day15_test")
    val input = readInput("Day15")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 1320)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 513172)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 145)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 237806)
    }
}

private fun part1(input: List<String>): Int {
    val seq = input.joinToString("").split(",")
    return seq.sumOf(String::hash)
}

private fun String.hash(): Int = fold(0) { acc, char -> (acc + char.code) * 17 % 256 }

private fun part2(input: List<String>): Int {
    val seq = input.joinToString("").split(",").map(String::parseOp)
    val boxes: MutableList<MutableMap<String, Op>> = MutableList(256) { mutableMapOf() }
    seq.forEach { s ->
        val boxNo = s.label.hash()
        when (s.op) {
            '-' -> boxes[boxNo].remove(s.label)
            '=' -> {
                val olsSlot = boxes[boxNo][s.label]
                if (olsSlot != null) {
                    olsSlot.focalLength = s.focalLength
                } else {
                    boxes[boxNo][s.label] = s
                }
            }
        }
    }

    var sum = 0
    boxes.forEachIndexed { boxNo, ops ->
        ops.values.forEachIndexed { slotNo, op ->
            sum += (boxNo + 1) * (slotNo + 1) * op.focalLength
        }
    }

    return sum
}

private data class Op(
    val label: String,
    val op: Char,
    var focalLength: Int = -1,
)

private fun String.parseOp(): Op = if (endsWith("-")) {
    Op(label = dropLast(1), op = '-')
} else {
    val (label, length) = split("=")
    Op(label, '=', length.toInt())
}
