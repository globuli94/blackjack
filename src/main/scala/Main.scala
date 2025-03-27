import controller.controllerComponent.*
import model.gameComponent.*
import view.{GUI, TUI}

import scala.collection.immutable.LazyList.cons
import scala.io.StdIn.readLine

object Main {

  private val game: GameInterface = Game()
  private val controller: ControllerInterface = Controller(game)
  private val tui: TUI = TUI(controller)
  private val gui: GUI = GUI(controller)

  println(game.toString)

  def main(args: Array[String]): Unit = {

    var input: String = ""

    while(input != "exit") {
      input = readLine();
      tui.getInputAndPrintLoop(input)
    }
  }
}