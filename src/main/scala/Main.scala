import model.ModelInterface
import util.fileIOComponent.JSON.FileIOJSON
import view.TUI

import com.google.inject.{Guice, Injector}
import controller.ControllerInterface
import view.GUI.GUI
import scala.collection.immutable.LazyList.cons
import scala.io.StdIn.readLine
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