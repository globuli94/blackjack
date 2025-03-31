import controller.controllerComponent.ControllerInterface
import view.{GUI, TUI}
import com.google.inject.{Guice, Injector}

import scala.collection.immutable.LazyList.cons
import scala.io.StdIn.readLine
import model.handComponent.*
import model.cardComponent.*
import model.gameComponent.GameInterface
import util.fileIOComponent.JSON.FileIOJSON

import scala.collection.immutable.Queue

object Main {
  private val injector: Injector = Guice.createInjector(new BlackjackModule)
  private val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  private val tui: TUI = TUI(controller)
  private val gui: GUI = GUI(controller)

  print(controller.toString)

  def main(args: Array[String]): Unit = {

    var input: String = ""

    while(input != "exit") {
      input = readLine();
      tui.getInputAndPrintLoop(input)
    }
  }
}