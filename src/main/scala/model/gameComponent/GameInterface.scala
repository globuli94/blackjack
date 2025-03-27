package model.gameComponent

trait GameInterface {
  def createPlayer: Game
  def leavePlayer(name: String): Game
  def deal: Game
  def hitDealer: Game
  def hitPlayer: Game
  def standPlayer: Game
  def betPlayer: Game
  def isValidBet: Boolean
  def doubleDownPlayer: Game
  def startGame: Game
  def getPlayerOptions: Game
  def toString: String
}
