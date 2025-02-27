package view
import util.{Event, Observer}
import controller.Controller_v2
import util.Event.{AddPlayer, End, Split}

import scala.swing.*
import scala.swing.event.*
import javax.swing.JOptionPane
import javax.swing.BorderFactory
import scala.swing.MenuBar.NoMenuBar.revalidate


class GUI(controller: Controller_v2) extends Frame with Observer {
  controller.add(this)

  preferredSize = new Dimension(900, 600)
  minimumSize = new Dimension(900, 600)
  maximumSize = new Dimension(900, 600)
  resizable = false

  private val poolTableGreen = new Color(0x0e5932)

  title = "Blackjack"
  visible = true // Ensure the window is displayed

  menuBar = new MenuBar {
    contents += new Menu("Game") {
      contents += new MenuItem(Action("Add Player") {
        val playerName = Dialog.showInput[String](
          message = "Enter Player Name",
          title = "New Player",
          messageType = Dialog.Message.Plain,
          initial = ""
        )
      })
      contents += new MenuItem(Action("Exit") {
        controller.exit()
      })
    }
  }

  contents = new BoxPanel(Orientation.Vertical) {
    background = poolTableGreen
    contents += new BoxPanel(Orientation.Horizontal) {

      for (player <- controller.game.players) {
        contents += new PlayerPanel(player)
      }
    }
  }

  private def rebuildUI(): Unit = {
    contents = new BoxPanel(Orientation.Vertical) {
      background = poolTableGreen
      contents += new BoxPanel(Orientation.Horizontal) {
        contents.clear() // Remove old players
        for (player <- controller.game.players) {
          contents += new PlayerPanel(player) // Add new players dynamically
        }

        background = poolTableGreen
        border = Swing.EmptyBorder(10, 10, 10, 10)
        contents += new Button("Hit")
        contents += new Button("Stand")
      }
    }
    revalidate() // Ensures layout is recalculated
    repaint() // Redraws UI
  }

  def update(e: Event): Unit = {
    e match {
      case _ => rebuildUI()
    }
  }
}
