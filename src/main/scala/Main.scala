import model.Deck
import model.Game
import controller.Controller
import view.Tui


import scala.collection.immutable.LazyList.cons
import scala.io.StdIn.readLine


object Main {

  val game: Game = Game()
  val controller: Controller = Controller(game)
  val tui: Tui = Tui(controller)
  println("Welcome to Blackjack:")
  println("Add Players using ")

  def main(args: Array[String]): Unit = {

    var input: String = ""
    
    while(input != "exit") {
      input = readLine();
      tui.getInputAndPrintLoop(input)
    }
  }
}