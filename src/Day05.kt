import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.max
import kotlin.math.min

fun main() {
    val testInput = Path("src/Day05_test.txt").readText().split("\n\n")
    val input = Path("src/Day05.txt").readText().split("\n\n")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 35L)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 282277027L)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 46L)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 11554135L)
    }
}

private data class GardenMapRange(
    val dstStart: Long,
    val srcStart: Long,
    val length: Long,
) {
    val dst = dstStart until dstStart + length
    val src = srcStart until srcStart + length

    fun convert(src: Long): Long = src - this.src.first + this.dst.first
}

private data class GardenMap(
    val ranges: List<GardenMapRange>,
) {
    fun get(src: Long): Long {
        for (range in ranges) {
            if (src in range.src) {
                return range.convert(src)
            }
        }
        return src
    }

    fun get(src: LongRange): List<LongRange> = buildList {
        var range = src
        for (mapRange in this@GardenMap.ranges) {
            val left = range.first..<min(range.last, mapRange.src.first - 1)
            val common = max(range.first, mapRange.src.first)..min(range.last, mapRange.src.last)
            val right = max(range.first, mapRange.src.last + 1)..range.last

            if (!left.isEmpty()) {
                this@buildList.add(range)
            }
            if (!common.isEmpty()) {
                this@buildList.add(mapRange.convert(common.first)..mapRange.convert(common.last))
            }
            range = right
            if (range.isEmpty()) {
                break
            }
        }
        if (!range.isEmpty()) {
            this.add(range)
        }
    }
}


private fun String.parseMap(): GardenMap {
    val strings = this.split("\n").filter { it.isNotEmpty() }
    val list = strings.drop(1)
        .map { range ->
            val numbers = range.split(" ").map { it.toLong() }
            GardenMapRange(
                dstStart = numbers[0],
                srcStart = numbers[1],
                length = numbers[2],
            )
        }
        .sortedBy { it.srcStart }
    return GardenMap(list)
}

private fun part1(input: List<String>): Long {
    val seeds = input[0].substringAfter(": ").split(" ").map { it.toLong() }
    val maps = input.drop(1).map(String::parseMap)
    return seeds.minOf { seed ->
        maps.fold(seed) { acc, gardenMap: GardenMap -> gardenMap.get(acc) }
    }
}

private fun part2(input: List<String>): Long {
    val seeds = input[0].substringAfter(": ").split(" ")
        .map { it.toLong() }
        .windowed(2, 2) {
            it[0]..<it[0] + it[1]
        }
    val maps = input.drop(1).map(String::parseMap)
    return seeds.minOf { seedRange ->
        maps.fold(listOf(seedRange)) { ranges, gardenMap: GardenMap ->
            ranges.flatMap { gardenMap.get(it) }
        }.minOf { it.first }
    }
}
