import controller.controllerComponent.Controller
import model.gameComponent.Game
import view.{GUI, TUI}

import scala.collection.immutable.LazyList.cons
import scala.io.StdIn.readLine

object Main {

  private val game: Game = Game()
  private val controller: Controller = Controller(game)
  private val tui: TUI = TUI(controller)
  private val gui: GUI = GUI(controller)

  println(game.toString())

  def main(args: Array[String]): Unit = {

    var input: String = ""

    while(input != "exit") {
      input = readLine();
      tui.getInputAndPrintLoop(input)
    }
  }
}