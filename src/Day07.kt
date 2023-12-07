fun main() {
    val testInput = readInput("Day07_test")
    val input = readInput("Day07")

    part1(testInput).also {
        println("Part 1, test input: $it")
        check(it == 6440)
    }

    part1(input).also {
        println("Part 1, real input: $it")
        // check(it == 1)
    }

    part2(testInput).also {
        println("Part 2, test input: $it")
        // check(it == 1)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        // check(it == 1)
    }
}

enum class Card(val char: Char, val order: Int) {
    C2('2', 2),
    C3('3', 3),
    C4('4', 4),
    C5('5', 5),
    C6('6', 6),
    C7('7', 7),
    C8('8', 8),
    C9('9', 9),
    T('T', 10),
    J('J', 11),
    Q('Q', 12),
    K('K', 13),
    A('A', 14),
}

enum class HandType(val predicate: (Hand) -> Boolean) : Comparable<HandType> {
    FIVE_OF_A_KIND({ hand: Hand -> hand.cards.all { it == hand.cards[0] } }),
    FOUR_OF_A_KIND(
        { hand ->
            hand.cards.groupingBy { it }.eachCount().any { it.component2() == 4 }
        },
    ),
    FULL_HOUSE(
        { hand ->
            val groups = hand.cards.groupingBy { it }.eachCount().values.sortedDescending()
            groups == listOf(3, 2)
        },
    ),
    THREE_OF_A_KIND(
        { hand ->
            val groups = hand.cards.groupingBy { it }.eachCount().values.sortedDescending()
            groups == listOf(3, 1, 1)
        },
    ),
    TWO_PAIR(
        { hand ->
            val groups = hand.cards.groupingBy { it }.eachCount().values.sortedDescending()
            groups == listOf(2, 2, 1)
        },
    ),
    ONE_PAIR(
        { hand ->
            val groups = hand.cards.groupingBy { it }.eachCount().values.sortedDescending()
            groups == listOf(2, 1, 1, 1)
        },
    ),
    HIGH_CARD(
        { hand ->
            val groups = hand.cards.groupingBy { it }.eachCount().values
            groups.size == 5
        },
    ),
    OTHER(predicate = { hand -> true }),
}

data class Hand(
    val cards: List<Card>,
) : Comparable<Hand> {
    init {
        check(cards.size == 5)
    }

    val type = HandType.entries.first {
        it.predicate(this)
    }

    override fun compareTo(other: Hand): Int {
        this.type.compareTo(other.type).let {
            if (it != 0) return it
        }
        this.cards.forEachIndexed { index, card ->
            other.cards[index].compareTo(card).let {
                if (it != 0) return it
            }
        }
        return 0
    }
}

data class HandBid(
    val hand: Hand,
    val bid: Int,
)

fun String.parseHand(): Hand {
    val cards = this.map { cardChar ->
        Card.entries.first { it.char == cardChar }
    }
    return Hand(cards)
}

fun String.parseHandBind(): HandBid {
    val (cards, bid) = this.split(" ", limit = 2)
    return HandBid(
        cards.parseHand(),
        bid.toInt(),
    )
}

private fun part1(input: List<String>): Int {
    val cards = input.map { it.parseHandBind() }
    val sortedCards = cards.sortedByDescending { it.hand }
    return sortedCards.foldIndexed(0) { index, acc, handBid ->
        acc + (1 + index) * handBid.bid
    }
}

private fun part2(input: List<String>): Int {
    return input.size
}
