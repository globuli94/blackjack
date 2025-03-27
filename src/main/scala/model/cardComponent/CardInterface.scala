package model.cardComponent

trait CardInterface {
  def getRank: String
  def getSuit: String
  def getValue: Int
  def toString: String
}
