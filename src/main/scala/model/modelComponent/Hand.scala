package model.modelComponent

// handles adding cards to hand and value logic -> bust, blackjack
case class Hand(cards: List[Card] = List.empty) {

  def addCard(card: Card): Hand = {
    return Hand(card :: cards)
  }

  def getHandValue: Int = {
    // Calculate the total value assuming all Aces are 11
    val values = cards.map(_.getValue)
    var totalValue = values.sum

    // Count how many Aces are in the hand
    var aceAdjustment = cards.count(_.rank == "A")

    // Adjust for Aces if the total exceeds 21
    while (totalValue > 21 && aceAdjustment > 0) {
      totalValue -= 10
      aceAdjustment -= 1
    }

    totalValue
  }

  def isBust: Boolean = getHandValue > 21

  def hasBlackjack: Boolean = getHandValue == 21

  def canHit: Boolean = getHandValue < 21

  def canDoubleDown: Boolean = (getHandValue == 9 || getHandValue == 10 || getHandValue == 11) && cards.length == 2

  def canSplit: Boolean = cards.size == 2 && cards.head.rank == cards(1).rank

  override def toString: String = {
    val stringBuilder = new StringBuilder()
    cards.foreach(card => {
      stringBuilder.append(card.toString)
    })
    stringBuilder.toString()
  }
}
