package model.handComponent

import model.cardComponent.CardInterface

// handles adding cards to hand and value logic -> bust, blackjack
case class Hand(cards: List[CardInterface] = List.empty) extends HandInterface {

    override def getCards: Seq[CardInterface] = cards
    override def length: Int = cards.length


    override def addCard(card: CardInterface): Hand = {
        return Hand(card :: cards)
    }

    override def getHandValue: Int = {
        // Calculate the total value assuming all Aces are 11
        val values = cards.map(_.getValue)
        var totalValue = values.sum

        // Count how many Aces are in the hand
        var aceAdjustment = cards.count(_.getRank == "A")

        // Adjust for Aces if the total exceeds 21
        while (totalValue > 21 && aceAdjustment > 0) {
            totalValue -= 10
            aceAdjustment -= 1
        }

        totalValue
    }

    override def isBust: Boolean = getHandValue > 21
    override def hasBlackjack: Boolean = getHandValue == 21
    override def canHit: Boolean = getHandValue < 21
    override def canDoubleDown: Boolean = (getHandValue == 9 || getHandValue == 10 || getHandValue == 11) && cards.length == 2
    override def canSplit: Boolean = cards.size == 2 && cards.head.getRank == cards(1).getRank

    override def toString: String = {
        val stringBuilder = new StringBuilder()
        cards.foreach(card => {
            stringBuilder.append(card.toString)
        })
        stringBuilder.toString()
    }
}
