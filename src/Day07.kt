import Card.J

fun main() {
    val testInput = readInput("Day07_test")
    val input = readInput("Day07")

    part2(testInput).also {
        println("Part 2, test input: $it")
        check(it == 5905)
    }

    part2(input).also {
        println("Part 2, real input: $it")
        check(it == 254494947)
    }
}

enum class Card(val char: Char) {
    J('J'),
    C2('2'),
    C3('3'),
    C4('4'),
    C5('5'),
    C6('6'),
    C7('7'),
    C8('8'),
    C9('9'),
    T('T'),
    Q('Q'),
    K('K'),
    A('A'),
}

enum class HandType(val predicate: (Hand) -> Boolean) : Comparable<HandType> {
    FIVE_OF_A_KIND(5),
    FOUR_OF_A_KIND(4, 1),
    FULL_HOUSE(3, 2),
    THREE_OF_A_KIND(3, 1, 1),
    TWO_PAIR(2, 2, 1),
    ONE_PAIR(2, 1, 1, 1),
    HIGH_CARD(1, 1, 1, 1, 1),
    ;

    constructor(vararg expectedCombination: Int) : this({ hand: Hand -> hand.cards.hasCombinationWithJokers(counts = expectedCombination) })
}

private fun List<Card>.hasCombinationWithJokers(vararg counts: Int): Boolean {
    val groups = this.groupingBy { it }.eachCount()
    val countsList = counts.toList()
    return if (groups.values.sortedDescending() == countsList) {
        true
    } else {
        val jokers = groups.getOrDefault(J, 0)
        val groupsNoJokers = groups - J
        for (jokerCard in groupsNoJokers.keys) {
            val newGroups: Map<Card, Int> = groupsNoJokers + (jokerCard to groupsNoJokers[jokerCard]!! + jokers)
            if (newGroups.values.sortedDescending() == countsList) return true
        }
        return false
    }
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

private fun part2(input: List<String>): Int {
    val cards = input.map { it.parseHandBind() }
    val sortedCards = cards.sortedByDescending { it.hand }
    return sortedCards.foldIndexed(0) { index, acc, handBid ->
        acc + (1 + index) * handBid.bid
    }
}
