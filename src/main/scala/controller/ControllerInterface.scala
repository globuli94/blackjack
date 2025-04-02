package controller

import model.ModelInterface
import util.Observable

trait ControllerInterface extends Observable {
  def loadGame(): Unit
  def saveGame(): Unit
  def getGame: ModelInterface
  def initializeGame(): Unit
  def startGame(): Unit
  def addPlayer(name: String): Unit
  def leavePlayer(): Unit
  def hitPlayer(): Unit
  def standPlayer(): Unit
  def doubleDown(): Unit
  def bet(amount: String): Unit
  def exit(): Unit
  def toString: String
}
