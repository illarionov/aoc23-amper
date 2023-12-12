import Spring.DMG
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
    OP, DMG, UNK;

    override fun toString(): String {
        return when (this) {
            OP -> "."
            DMG -> "#"
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
        var newGroups = mutableListOf<Int>()
        var currentGroupCounter = 0
        combination.forEach { spring ->
            if (spring == DMG) {
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
                Spring.DMG
            }
        } else {
            spring
        }
    }
}

private fun String.parse(): ConditionRecord {
    val sprints = substringBefore(" ").map {
        when (it) {
            '#' -> Spring.DMG
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
        println("combs for ${it.groups}: $combs")
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

private fun ConditionRecord.getArrangements3(): Int {
    return 0
}

private fun ConditionRecord.getArrangements2(): Int {
    val queue: ArrayDeque<PartialMatchPos> = ArrayDeque()
    val groups = this.groups

    var combinations = 0
//    var combinationsCache: MutableMap<PartialMatchPos, Int> = mutableMapOf()

    var firstPos = PartialMatchPos(
        springValue = UNK,
        springNo = sprigs.lastIndex,
        groupNo = groups.lastIndex,
        groupVal = 0,
        isAfterGroup = true,
        partial = true,
    )
    queue.add(firstPos)
    while (queue.isNotEmpty()) {
        val lastPos = queue.removeFirst()

        if (!lastPos.partial) {
            combinations += 1
            continue
        }
        getNextMatch(lastPos, DMG)?.let {
            if (!it.partial) {
                combinations += 1
            } else {
                queue.add(it)
            }
        }
        getNextMatch(lastPos, OP)?.let {
            if (!it.partial) {
                combinations += 1
            } else {
                queue.add(it)
            }
        }
    }

    return combinations
}

private data class PartialMatchPos(
    val springValue: Spring,
    val springNo: Int,
    val groupNo: Int,
    val groupVal: Int,
    val isAfterGroup: Boolean,
    var partial: Boolean,
)

private fun ConditionRecord.getNextMatch(predMatch: PartialMatchPos, newValue: Spring): PartialMatchPos? {
    var isAfterGroup = predMatch.isAfterGroup
    var groupNo = predMatch.groupNo
    var groupVal = predMatch.groupVal
    var springIndex = predMatch.springNo
    var partial = false

//    run {
//        val springsText = predMatch.springs.joinToString("")
//
////        println("getPartialMatchIn(${springsText}, startPos ${predMatch.springNo} val: ${newValue}; " +
////                "Group: ${predMatch.groupNo}:${predMatch.groupVal}${if (predMatch.isAfterGroup) "]" else ""})")
//    }

    var isFirstReplace = true
    while (springIndex >= 0) {
        var spring = sprigs[springIndex]
        if (spring == UNK && isFirstReplace) {
            spring = newValue
            isFirstReplace = false
        }
        springIndex -= 1
        when (spring) {
            OP -> {
                if (isAfterGroup) {
                    continue
                } else {
                    // Start of group
                    if (groupNo < 0 || groups[groupNo] != groupVal) return null
                    groupNo -= 1
                    groupVal = 0
                    isAfterGroup = true
                }
            }

            DMG -> {
                if (isAfterGroup) {
                    // open group
                    check(groupVal == 0)
                    isAfterGroup = false
                    groupVal = 1
                } else {
                    groupVal += 1
                }
            }

            UNK -> {
                partial = true
                springIndex += 1
                break
            }
        }
    }
    if (!partial) {
        if (groupVal != 0) {
            if (groupNo < 0 || groups[groupNo] != groupVal) return null
            groupNo -= 1
            isAfterGroup = true
        }
        if (groupNo != -1) return null
    }

    return PartialMatchPos(
        groupNo = groupNo,
        groupVal = groupVal,
        isAfterGroup = isAfterGroup,
        springValue = newValue,
        springNo = springIndex,
        partial = partial,
    ).also {
        //println("pos: $it")
    }
}
