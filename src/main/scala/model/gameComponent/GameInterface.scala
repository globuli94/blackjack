package model.gameComponent

import model.dealerComponent.DealerInterface
import model.deckComponent.DeckInterface
import model.playerComponent.PlayerInterface

trait GameInterface {
  
  def getIndex: Int
  def getPlayers: List[PlayerInterface]
  def getDeck: DeckInterface
  def getState: GameState
  def getDealer: DealerInterface
  
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
