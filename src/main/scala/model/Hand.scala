package model

enum HandSate {
    case Play
    case Stand
}

// handles adding cards to hand and value logic -> bust, blackjack

case class Hand(hand: List[Card] = List.empty, state: HandSate = HandSate.Play) {

    def addCard(card: Card): Hand = {
        return Hand(card :: hand)
    }

    def value: Int = {
        // Calculate the total value assuming all Aces are 11
        val values = hand.map(_.value)
        var totalValue = values.sum

        // Count how many Aces are in the hand
        var aceAdjustment = hand.count(_.rank == "A")

        // Adjust for Aces if the total exceeds 21
        while (totalValue > 21 && aceAdjustment > 0) {
            totalValue -= 10
            aceAdjustment -= 1
        }

        totalValue
    }

    def isBust: Boolean = value > 21
    def hasBlackjack: Boolean = value == 21

    def canHit: Boolean = value < 21
    def canDoubleDown: Boolean =
        (value == 9 || value == 10 || value == 11) && hand.length == 2
    def canSplit: Boolean = hand.size == 2 && hand.head.rank == hand(1).rank

    override def toString: String = {
        val stringBuilder = new StringBuilder()
        hand.foreach(card => {
            stringBuilder.append(card.toString())
        })
        stringBuilder.toString()
    }
}
