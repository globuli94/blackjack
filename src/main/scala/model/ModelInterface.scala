package model

import model.modelComponent.{Dealer, Deck, GameState, Player}

trait ModelInterface {
  
  def getIndex: Int
  def getPlayers: List[Player]
  def getDeck: Deck
  def getDealer: Dealer
  def getState: GameState

  def initialize: ModelInterface
  def evaluate: ModelInterface
  def createPlayer(name: String): ModelInterface
  def leavePlayer(name: String = ""): ModelInterface
  def deal: ModelInterface
  def hitDealer: ModelInterface
  def hitPlayer: ModelInterface
  def standPlayer: ModelInterface
  def betPlayer(amount: Int): ModelInterface
  def isValidBet(amount: Int): Boolean
  def doubleDownPlayer: ModelInterface
  def startGame: ModelInterface
  def getPlayerOptions: List[String]
  def toString: String
}
