import model.{Game, Game_v2}
import controller.{Controller, Controller_v2}
import view.{GUI, TUI}

import scala.collection.immutable.LazyList.cons
import scala.io.StdIn.readLine

object Main {

  private val game: Game_v2 = Game_v2()
  private val controller: Controller_v2 = Controller_v2(game)
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