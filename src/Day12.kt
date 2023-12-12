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
        val combs = it.getArrangementsBruteforce()
        println("combs for ${it.groups}: $combs")
        combs
    }
}


private fun part2(input: List<ConditionRecord>): Long {
    // println("input: $input")

    var idx = 0
//    return input.sumOf {
//        val combs = it.getArrangements3().toLong()
//        println("combs for ${idx++} ${it.groups}: $combs")
//        combs
//    }
}


