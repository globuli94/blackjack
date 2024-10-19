package view
import controller.Controller

import util.{Event, Observer}
import scala.util.{Failure, Success, Try}

class Tui(controller:Controller) extends Observer {
  controller.add(this)

  def getInputAndPrintLoop(input:String): Unit =
    val splitInput = input.split(" ")
    val command = splitInput(0)

    splitInput(0) match
      case "add" =>
        controller.addPlayer(splitInput(1))
      case "start" =>
        controller.start
      case "exit" =>
        controller.exit()
      case _
      => println("not a valid command!")

  override def update(e: Event): Unit =
    e match {
      case Event.AddPlayer => println(controller.toString)
      case Event.Start => println(controller.toString)
    }
}