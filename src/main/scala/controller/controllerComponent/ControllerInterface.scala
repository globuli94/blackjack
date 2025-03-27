package controller.controllerComponent

trait ControllerInterface {
  def initializeGame(): Unit
  def startGame(): Unit
  def addPlayer(name: String): Unit
  def leavePlayer(): Unit
  def hitNextPlayer(): Unit
  def standNextPlayer(): Unit
  def doubleDown(): Unit
  def bet(amount: String): Unit
  def exit(): Unit
  def toString: String
}
