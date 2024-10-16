package model

// handles adding cards to hand and value logic -> bust, blackjack

case class Hand (hand: List[Card] = List.empty) {

    def addCard(card: Card): Hand = {
        return Hand(card :: hand)
    }

    def value: Int = {
        val values = hand.map(_.value)
        val totalValue = values.sum
        if (totalValue > 21 && hand.exists(_.rank == "A")) {
        totalValue - 10 // Count Ace as 1 if total exceeds 21
        } else {
        totalValue
        }
    }

    def isBust: Boolean = value > 21
    def hasBlackjack: Boolean = value == 21
}