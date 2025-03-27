package model.handComponent

import model.HandState
import model.cardComponent.CardInterface

enum HandState {
    case Play
    case Stand
}

// handles adding cards to hand and value logic -> bust, blackjack
case class Hand(hand: List[CardInterface] = List.empty, state: HandState = HandState.Play) extends HandInterface {

    override def addCard(card: CardInterface): Hand = {
        return Hand(card :: hand)
    }

    override def value: Int = {
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

    override def isBust: Boolean = value > 21
    override def hasBlackjack: Boolean = value == 21
    override def canHit: Boolean = value < 21
    override def canDoubleDown: Boolean =
        (value == 9 || value == 10 || value == 11) && hand.length == 2
    override def canSplit: Boolean = hand.size == 2 && hand.head.rank == hand(1).rank

    override def toString: String = {
        val stringBuilder = new StringBuilder()
        hand.foreach(card => {
            stringBuilder.append(card.toString)
        })
        stringBuilder.toString()
    }
}
