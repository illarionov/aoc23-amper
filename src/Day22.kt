fun main() {
    val testInput = readInput("Day22_test").mapIndexed { index, t -> t.parseBrick(index) }
    val input = readInput("Day22").mapIndexed { index, t -> t.parseBrick(index) }

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 5)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        check(it == 405)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 7)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 61297)
    }
}

private data class Brick(
    val no: Int,
    val x: IntRange,
    val y: IntRange,
    val z: IntRange,
)

private fun String.parseBrick(brickNo: Int): Brick {
    val (p1, p2) = this.split("~")
    val (x1, y1, z1) = p1.split(",").map { it.toInt() }
    val (x2, y2, z2) = p2.split(",").map { it.toInt() }
    return Brick(
        brickNo,
        x = x1..x2,
        y = y1..y2,
        z = z1..z2,
    )
}

private fun part1(input: List<Brick>): Int {
    val allBricksFallen = fall(input)

    return input.count { brickToDisintegrate ->
        val testBricks = input - brickToDisintegrate
        val testBricksFallen = fall(testBricks)

        val fallenBricksWithoutDisintegrate = allBricksFallen.filter { it.no != brickToDisintegrate.no }
        testBricksFallen == fallenBricksWithoutDisintegrate
    }
}

private fun fall(inputBricks: List<Brick>): List<Brick> {
    val xMax = inputBricks.maxOf { it.x.last }
    val yMax = inputBricks.maxOf { it.y.last }
    val zMax = inputBricks.maxOf { it.z.last }

    var cup = inputBricks.getCup(xMax, yMax, zMax)
    var bricks = inputBricks
    while (true) {
        val newBricks = bricks.map { getNewPosition(cup, it) }
        if (newBricks != bricks) {
            bricks = newBricks
            cup = newBricks.getCup(xMax, yMax, zMax)
        } else {
            break
        }
    }
    return bricks
}

private fun List<Brick>.getCup(
    xMax: Int,
    yMax: Int,
    zMax: Int,
): Array<Array<Array<Int>>> {
    val cup = Array(xMax + 1) { _ ->
        Array(yMax + 1) { _ ->
            Array(zMax) { _ ->
                -1
            }
        }
    }

    forEach { brick ->
        for (x in brick.x) {
            for (y in brick.y) {
                for (z in brick.z) {
                    cup[x][y][z-1] = brick.no
                }
            }
        }
    }
    return cup
}

private fun getNewPosition(
    brickIndex: Array<Array<Array<Int>>>,
    brick: Brick,
): Brick {
    val newBrick = brick.copy(
        z = brick.z.start - 1..brick.z.last - 1,
    )
    if (newBrick.z.first == 0) return brick
    for (x in newBrick.x) {
        for (y in newBrick.y) {
            for (z in newBrick.z) {
                val old = brickIndex[x][y][z-1]
                if (old != -1 && old != brick.no) {
                    return brick
                }
            }
        }
    }
    return newBrick
}

private fun part2(input: List<Brick>): Int {
    val allBricksFallen = fall(input)

    return input.sumOf { brickToDisintegrate ->
        val testBricks = input - brickToDisintegrate
        val testBricksFallen = fall(testBricks)

        val fallenBricksWithoutDisintegrate = allBricksFallen.filter { it.no != brickToDisintegrate.no }
        fallenBricksWithoutDisintegrate.indices.count { i ->
            fallenBricksWithoutDisintegrate[i] != testBricksFallen[i]
        }
    }
}
