import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val testInput = Path("src/Day19_test.txt").readText().split("\n\n")
    val input = Path("src/Day19.txt").readText().split("\n\n")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 19114)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 382440)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 167409079868000L)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 136394217540123L)
    }
}

private fun part1(input: List<String>): Int {
    val (prog, partsStrings) = input

    val workflows: List<Workflow> = prog.split("\n").filter(String::isNotEmpty).map(String::parseWorkflow)
    val parts = partsStrings.split("\n").filter(String::isNotEmpty).map(String::parseParts)

    return parts.sumOf { ratings ->
        val accepted = workflows.run(ratings)
        if (accepted) {
            ratings.values.sum()
        } else {
            0
        }
    }
}

private val acceptedWorkflow = Workflow("A", emptyList())
private val rejectedWorkflow = Workflow("R", emptyList())

private fun List<Workflow>.run(ratings: Map<String, Int>): Boolean {
    val workflows: MutableMap<String, Workflow> = this.associateBy { it.name }.toMutableMap()
    workflows["A"] = acceptedWorkflow
    workflows["R"] = rejectedWorkflow
    var w = workflows["in"]!!
    while (w != acceptedWorkflow && w != rejectedWorkflow) {
        val next = w.getNext(ratings)
        w = workflows[next]!!
    }
    return w == acceptedWorkflow
}

private fun Workflow.getNext(ratings: Map<String, Int>): String {
    rules.forEach { rule ->
        if (rule.part == "") {
            return rule.nextWorkflow
        }
        val rating = ratings.getOrDefault(rule.part, 0)
        val match = when (rule.cond) {
            '<' -> rating < rule.condVal
            '>' -> rating > rule.condVal
            else -> error("Unknown rule")
        }
        if (match) return rule.nextWorkflow
    }
    error("Rule not found")
}

private data class Workflow(
    val name: String,
    val rules: List<Rule>,
)

private data class Rule(
    val part: String,
    val cond: Char,
    val condVal: Int,
    val nextWorkflow: String,
)

private fun String.parseWorkflow(): Workflow {
    val name = this.substringBefore("{")
    val rules = this.substringAfter("{").substringBefore("}").split(",")
    return Workflow(
        name = name,
        rules = rules.map(String::parseRule),
    )
}

private val ruleRegex = """(\w+)([<>])(\d+):(\w+)""".toRegex()

private fun String.parseRule(): Rule {
    val match = ruleRegex.matchEntire(this)
    if (match != null) {
        val (_, part, cond, condVal, nextWorkflow) = match.groupValues
        return Rule(
            part = part,
            cond = cond[0],
            condVal = condVal.toInt(),
            nextWorkflow = nextWorkflow,
        )
    } else {
        return Rule(
            part = "",
            cond = '!',
            condVal = 0,
            nextWorkflow = this,
        )
    }
}

private fun String.parseParts(): Map<String, Int> {
    return substring(1, this.lastIndex)
        .split(",")
        .associate {
            val (partName, count) = it.split("=")
            partName to count.toInt()
        }
}

private fun part2(input: List<String>): Long {
    val (prog, _) = input

    val workflows: List<Workflow> = prog.split("\n").filter(String::isNotEmpty).map(String::parseWorkflow)

    val workflowsMap = workflows.associateBy(Workflow::name).toMutableMap()
    workflowsMap["A"] = acceptedWorkflow
    workflowsMap["R"] = rejectedWorkflow

    val conditions = mapOf<Char, ClosedRange<Int>>(
        'x' to 1..4000,
        'm' to 1..4000,
        'a' to 1..4000,
        's' to 1..4000,
    )

    return workflowsMap.getCombinations("in", conditions)
}

private fun Map<String, Workflow>.getCombinations(current: String, startConditions: Map<Char, ClosedRange<Int>>): Long {
    if (current == "R") {
        return 0
    }
    if (current == "A") {
        return startConditions.values.fold(1L) { acc, closedRange -> acc * closedRange.size() }
    }

    var sum: Long = 0
    var conds = startConditions
    val workflow = this[current]!!
    for (rule in workflow.rules) {
        if (rule.part == "") {
            sum += getCombinations(rule.nextWorkflow, conds)
            break
        }
        val oldRange = conds[rule.part[0]]!!
        val newRangeMatch = when (rule.cond) {
            '<' -> oldRange.start..< rule.condVal
            '>' -> (rule.condVal + 1)..oldRange.endInclusive
            else -> error("Unknown cond")
        }
        if (!newRangeMatch.isEmpty()) {
            val newConds = conds.toMutableMap().apply {
                this[rule.part[0]] = newRangeMatch
            }
            sum += getCombinations(rule.nextWorkflow, newConds)
        }

        val newRangeNotMatch = when (rule.cond) {
            '<' -> rule.condVal..oldRange.endInclusive
            '>' -> oldRange.start..rule.condVal
            else -> error("Unknown cond")
        }
        if (newRangeNotMatch.isEmpty()) {
            break
        }
        conds = conds.toMutableMap().apply {
            this[rule.part[0]] = newRangeNotMatch
        }
    }
    return sum
}

private fun ClosedRange<Int>.size(): Long = endInclusive - start + 1L
