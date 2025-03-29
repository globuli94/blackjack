package model.handComponent

import model.cardComponent.CardInterface

trait HandInterface {
  def getCards: Seq[CardInterface]
  def getState: HandState
  def length: Int
  def getHandValue: Int
  def addCard(card: CardInterface): Hand
  def isBust: Boolean
  def hasBlackjack: Boolean
  def canHit: Boolean
  def canDoubleDown: Boolean
  def canSplit: Boolean
  def toString: String
}
