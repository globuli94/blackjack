package model.handComponent

trait HandInterface {
  def addCard(card: Card): Hand
  def isBust: Boolean
  def hasBlackjack: Boolean
  def canHit: Boolean
  def canDoubleDown: Boolean
  def canSplit: Boolean
  def toString: String
}
