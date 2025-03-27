package model.playerComponent

trait PlayerInterface {
  def getName: String
  def getHand: Hand
  def getSplitHand: List[Hand]
  def getMoney: Int
  def getBet: Int
  def getState: PlayerState
}
