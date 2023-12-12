import Spring.DAM
import Spring.OP
import Spring.UNK

fun main() {
    val testInput = readInput("Day12_test").map { it.parse() }
    val input = readInput("Day12").map { it.parse() }
    val testInput2 = testInput.map { record ->
        ConditionRecord(
            sprigs = record.sprigs.expand(),
            groups = (1..5).flatMap { record.groups },
        )
    }
    val input2 = input.map { record ->
        ConditionRecord(
            sprigs = record.sprigs.expand(),
            groups = (1..5).flatMap { record.groups },
        )
    }

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 21)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 7236)
    }

    part2(testInput2).also {
        println("Part 2, test input: $it")
        check(it == 525152L)
    }

    part2(input2).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}

private enum class Spring {
    OP, DAM, UNK;

    override fun toString(): String {
        return when (this) {
            OP -> "."
            DAM -> "#"
            UNK -> "?"
        }
    }
}

private fun List<Spring>.expand(): List<Spring> {
    val result = mutableListOf<Spring>()
    repeat(4) {
        result.addAll(this)
        result.add(UNK)
    }
    result.addAll(this)
    return result
}

private data class ConditionRecord(
    val sprigs: List<Spring>,
    val groups: List<Int>,
) {
    fun isMatchGroups(combination: List<Spring>): Boolean {
        var pos = 0
        var predGroupStart = Spring.UNK

        var newGroups = mutableListOf<Int>()
        var currentGroupCounter = 0
        combination.forEach { spring ->
            if (spring == DAM) {
                currentGroupCounter += 1
            } else {
                if (currentGroupCounter != 0) {
                    newGroups.add(currentGroupCounter)
                    currentGroupCounter = 0
                }
            }
        }
        if (currentGroupCounter != 0) {
            newGroups.add(currentGroupCounter)
        }

        if (newGroups == this.groups) {
            return true
        } else {
            return false
        }
    }
}

private fun ConditionRecord.getArrangementsBruteforce(): Int {
    val unks = sprigs.count { it == UNK }
    check(unks < 62)
    println("unks: $unks")
    val combs: Long = 1L shl unks

    return (0L until combs).count {
        val comb = this.sprigs.getCombination(it)
        // println("testing comb ${comb.joinToString("")} ${this.groups}")
        this.isMatchGroups(comb)
    }
}

private fun List<Spring>.getCombination(no: Long): List<Spring> {
    var n = no
    return this.map { spring ->
        if (spring == UNK) {
            val bit = n % 2
            n /= 2
            if (bit == 0L) {
                Spring.OP
            } else {
                Spring.DAM
            }
        } else {
            spring
        }
    }
}

private fun String.parse(): ConditionRecord {
    val sprints = substringBefore(" ").map {
        when (it) {
            '#' -> Spring.DAM
            '.' -> Spring.OP
            '?' -> Spring.UNK
            else -> error("$it")
        }
    }
    val groups = substringAfter(" ").split(",").map { it.toInt() }
    return ConditionRecord(sprints, groups)
}

private fun part1(input: List<ConditionRecord>): Int {
    return input.sumOf {
        // val combs = it.getArrangementsBruteforce()
        val combs = it.getArrangements2()
        // println("combs for ${it.groups}: $combs")
        combs
    }
}


private fun part2(input: List<ConditionRecord>): Long {
    // println("input: $input")

    var idx = 0
    return input.sumOf {
        val combs = it.getArrangements2().toLong()
        println("combs for ${idx++} ${it.groups}: $combs")
        combs
    }
}

private fun ConditionRecord.getArrangements2(): Int {
    val queue: ArrayDeque<PartialMatchPos> = ArrayDeque()
    val groups = this.groups

    var combinations = 0

    val op = this.sprigs.getNext(OP)
    op.getPartialMatchIn(groups)?.let { queue.add(it) }
    val dam = this.sprigs.getNext(DAM)
    dam.getPartialMatchIn(groups)?.let { queue.add(it) }

    while (queue.isNotEmpty()) {
        val lastPos = queue.removeLast()
        // println("lastPost: $lastPos, groups: ${groups}")

        if (lastPos.isComplete) {
            combinations += 1
            continue
        }
        val nextOp = lastPos.sprigs.getNext(OP)
        nextOp.getPartialMatchIn(groups)?.let {
            if (lastPos.isComplete) {
                combinations += 1
            } else {
                queue.add(it)
            }
        }

        val nextDam = lastPos.sprigs.getNext(DAM)
        nextDam.getPartialMatchIn(groups)?.let {
            if (lastPos.isComplete) {
                combinations += 1
            } else {
                queue.add(it)
            }
        }
    }

    return combinations
}

private fun List<Spring>.getNext(s: Spring): List<Spring> {
    val firstUnk = indexOf(UNK)
    check(firstUnk >= 0)
    return this.toMutableList().apply { this[firstUnk] = s }
}

//private fun getNextMathingPos(
//    olsPos: PartialMatchPos,
//    newSprings: S
//)

private fun List<Spring>.getPartialMatchIn(groups: List<Int>): PartialMatchPos? {
    var isBeforeGroup = true
    var groupNo = 0
    var brokenCounter = 0
    var partial = false

    // println("getPartialMatchIn(${this.joinToString("")} groups: $groups)")

    for (spring in this) {
        when (spring) {
            OP -> {
                if (isBeforeGroup) {
                    continue
                } else {
                    // End of group
                    if (brokenCounter == 0) {
                        continue
                    } else {
                        if (groupNo >= groups.size || groups[groupNo] != brokenCounter) return null
                        groupNo += 1
                        brokenCounter = 0
                        isBeforeGroup = true
                    }
                }
            }

            DAM -> {
                if (isBeforeGroup) {
                    // Open group
                    check(brokenCounter == 0)
                    isBeforeGroup = false
                    brokenCounter = 1
                } else {
                    brokenCounter += 1
                }
            }

            UNK -> {
                partial = true
                break
            }
        }
    }
    if (!partial) {
        if (brokenCounter != 0) {
            if (groupNo >= groups.size || groups[groupNo] != brokenCounter) return null
            groupNo += 1
            isBeforeGroup = true
        }
        if (groupNo != groups.size) return null
    }

    return PartialMatchPos(
        sprigs = this,
        groupNo = groupNo,
        isBeforeGroup = isBeforeGroup,
        isComplete = !partial,
    )
}

private data class PartialMatchPos(
    val sprigs: List<Spring>,
    val groups: List<String>,
    val springNo: Int,
    val groupNo: Int,
    val isBeforeGroup: Boolean,
    var isComplete: Boolean = false,
) {
    override fun toString(): String {
        return "PartialMatchPos(sprigs=${sprigs.joinToString("")}, groupNo=$groupNo, isBeforeGroup=$isBeforeGroup)"
    }
}
