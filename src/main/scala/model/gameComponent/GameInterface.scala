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

  def initialize: GameInterface
  def evaluate: GameInterface
  def createPlayer(name: String): GameInterface
  def leavePlayer(name: String = ""): GameInterface
  def deal: GameInterface
  def hitDealer: GameInterface
  def hitPlayer: GameInterface
  def standPlayer: GameInterface
  def betPlayer(amount: Int): GameInterface
  def isValidBet(amount: Int): Boolean
  def doubleDownPlayer: GameInterface
  def startGame: GameInterface
  def getPlayerOptions: List[String]
  def toString: String
}
