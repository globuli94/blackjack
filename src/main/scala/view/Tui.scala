package view
import controller.Controller
import util.{Event, Observer}

import scala.util.{Failure, Success, Try}

class TUI(controller:Controller) extends Observer {
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
        controller.hitNextPlayer()
      case "stand" =>
        controller.standNextPlayer()
      case "double" =>
        controller.doubleDown()
      case "bet" =>
        controller.bet(splitInput)
      case "leave" =>
        controller.leavePlayer()
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
      case Event.AddPlayer => println(controller.toString)
      case Event.Start => println(controller.toString)
      case Event.hitNextPlayer => println(controller.toString)
      case Event.standNextPlayer => println(controller.toString)
      case Event.bet => println(controller.toString) 
      case Event.continue => println(controller.toString)
      case Event.doubleDown => println(controller.toString)
      case Event.leavePlayer => println(controller.toString)
      case Event.invalidCommand => println("Error: Invalid Command")
      case Event.invalidBet => println("Error: Insufficent Funds")
      case Event.Create =>
      case _ => ???
    }
}