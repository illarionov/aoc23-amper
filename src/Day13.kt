import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val testInput = read("src/Day13_test.txt")
    val input = read("src/Day13.txt")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 405)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 37381)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 400)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 28210)
    }
}

private fun read(file: String): List<List<List<Char>>> = Path(file).readText()
    .split("\n\n")
    .map {
        it.split("\n").filter(String::isNotEmpty).map(String::toList)
    }

private fun part1(input: List<List<List<Char>>>): Int {
    return input.sumOf { chars ->
        var vertReflect = chars.vertReflect()
        if (vertReflect < 0) vertReflect = 0
        var horReflect = chars.horizReflect()
        if (horReflect < 0) horReflect = 0
        100 * horReflect + vertReflect
    }
}

private fun List<List<Char>>.vertReflect(skipColumn: Int = -1): Int {
    val chars: List<List<Char>> = this
    var l = 1
    while (l <= chars[0].lastIndex) {
        var left = l - 1
        var right = l
        var all = true
        if (l == skipColumn) {
            l += 1
            continue
        }
        while (left >= 0 && right <= chars[0].lastIndex) {
            if (!chars.all { it[left] == it[right] }) {
                all = false
                break
            }
            left -= 1
            right += 1
        }
        if (!all) {
            l += 1
        } else {
            return l
        }
    }
    return -1
}

private fun List<List<Char>>.horizReflect(skipLine: Int = -1): Int {
    var l = 1
    while (l <= this.lastIndex) {
        var top = l - 1
        var bottom = l
        if (l == skipLine) {
            l += 1
            continue
        }
        var all = true
        while (top >= 0 && bottom <= this.lastIndex) {
            if (this[top] != this[bottom]) {
                all = false
                break
            }
            top -= 1
            bottom += 1
        }
        if (!all) {
            l += 1
        } else {
            return l
        }
    }
    return -1
}

private fun part2(input: List<List<List<Char>>>): Int {
    return input.sumOf { chars ->
        var vertReflect =
            chars.vertReflect().let { original -> chars.findAlternateReflect(original, isHorizontal = false) }
        if (vertReflect < 0) vertReflect = 0

        var horReflect =
            chars.horizReflect().let { original -> chars.findAlternateReflect(original, isHorizontal = true) }
        if (horReflect < 0) horReflect = 0

        100 * horReflect + vertReflect
    }
}

private fun List<List<Char>>.findAlternateReflect(
    old: Int,
    isHorizontal: Boolean = false,
): Int {
    val height = size
    val width = this[0].size
    for (row in 0 until height) {
        for (col in 0 until width) {
            val newChar = when (this[row][col]) {
                '.' -> '#'
                '#' -> '.'
                else -> error("wrong char")
            }
            val newMap = this.toMutableList().map { it.toMutableList() }.apply { this[row][col] = newChar }
            val newReflLine = if (isHorizontal) {
                newMap.horizReflect(old)
            } else {
                newMap.vertReflect(old)
            }
            if (newReflLine >= 0) return newReflLine
        }
    }
    return -1
}
