package view

import controller.ControllerInterface
import controller.controllerComponent.Controller
import util.{Event, Observer}

import scala.util.{Failure, Success, Try}

class TUI(controller:ControllerInterface) extends Observer {
  controller.add(this)

  def getInputAndPrintLoop(input:String): Unit =
    val splitInput = input.split(" ")
    val command = splitInput(0)

    splitInput(0) match
      case "add" =>
        controller.addPlayer(splitInput(1))
      case "start" =>
        controller.startGame()
      case "continue" =>
        controller.startGame()
      case "hit" =>
        controller.hitPlayer()
      case "stand" =>
        controller.standPlayer()
      case "double" =>
        controller.doubleDown()
      case "bet" =>
        controller.bet(splitInput(1))
      case "leave" =>
        controller.leavePlayer()
      case "undo" =>
        controller.loadGame()
      case "exit" =>
        controller.exit()
      case _
      => println("not a valid command!")

  def clearConsole(): Unit = {
    println("\u001b[H\u001b[2J") // ANSI escape codes to clear console
    println("Console has been cleared!")
  }

  override def update(e: Event): Unit =
    e match {
      case Event.addPlayer => println(controller.toString)
      case Event.start => println(controller.toString)
      case Event.hitNextPlayer => println(controller.toString)
      case Event.standNextPlayer => println(controller.toString)
      case Event.bet => println(controller.toString) 
      case Event.continue => println(controller.toString)
      case Event.doubleDown => println(controller.toString)
      case Event.leavePlayer => println(controller.toString)
      case Event.invalidCommand => println("Error: Invalid Command")
      case Event.invalidBet => println("Error: Insufficent Funds")
      case Event.create =>
      case Event.errPlayerNameExists => println("Error: Player name already exists")
      case Event.load => println(controller.toString)
      case _ =>
    }
}