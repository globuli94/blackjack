package view
import util.{Event, Observer}
import controller.Controller
import model.Player
import util.Event.{AddPlayer, End, Split}

import scala.swing.*
import scala.swing.MenuBar.NoMenuBar.revalidate
import javax.swing.ImageIcon
import java.awt.{Color, Font, Graphics2D, RenderingHints}
import java.net.URL

class GUI(controller: Controller) extends Frame with Observer {
  private val poolTableGreen = new Color(0x0e5932)
  background = poolTableGreen
  private val width = 1200
  private val height = 700
  preferredSize = new Dimension(width, height)
  minimumSize = new Dimension(width, height)
  maximumSize = new Dimension(width, height)
  resizable = false
  title = "Blackjack"
  visible = true
  centerOnScreen()
  controller.add(this)

  private val button_dimension = new Dimension(200,50)

  private val add_player_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Add Player") {
        val playerName = Dialog.showInput[String](null,
          null, "Enter Player Name", Dialog.Message.Plain, Swing.EmptyIcon, Nil,"")
        playerName match {
          case Some(name) =>
            if(name.equals("")) {
              Dialog.showMessage(null, "No Player Added!", "Error", Dialog.Message.Plain, Swing.EmptyIcon)
            } else {
              controller.addPlayer(name)
            }
          case None =>
        }
    }
  }

  private val exit_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Exit") {
      controller.exit()
    }
  }

  val imagePath: URL = getClass.getResource("/blackjack_logo.png")
  private val blackjack_icon = new ImageIcon(imagePath)
  private val blackjack_icon_label: Label = new Label {
    icon = blackjack_icon
  }

  private def control_panel: BoxPanel = new BoxPanel(Orientation.Vertical) {
    background = poolTableGreen
    val panel_size = new Dimension(220, height)
    preferredSize = panel_size
    minimumSize = panel_size
    maximumSize = panel_size

    contents += blackjack_icon_label

    contents += Swing.HStrut(20)

    contents += add_player_button

    contents += Swing.HStrut(20)
    contents += exit_button
    contents += Swing.HStrut(20)
    border = Swing.LineBorder(Color.black)
  }

  private def player_panel: FlowPanel = new FlowPanel() {
    preferredSize = new Dimension(980, 400)
    minimumSize = new Dimension(980, 400)
    maximumSize = new Dimension(980, 400)
    background = poolTableGreen
    contents.clear() // Remove old players
    for ((player, index) <- controller.game.players.zipWithIndex) {

      val panel = if (controller.game.current_idx == index) {
        new PlayerPanel(player, true)
      } else {
        new PlayerPanel(player, false)
      }

      contents += panel
    }
  }

  private def player_control_panel: FlowPanel = new FlowPanel() {
    preferredSize = new Dimension(980, 100)
    minimumSize = new Dimension(980, 100)
    maximumSize = new Dimension(980, 100)
    background = poolTableGreen
    contents.clear() // Remove old players

    val player: Player = controller.game.players(controller.game.current_idx)

    contents += ControlPanel(controller)
  }

  contents = new BoxPanel(Orientation.Horizontal) {
    background = poolTableGreen
    contents += control_panel
    contents += player_panel
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }

  centerOnScreen()

  private def rebuildUI(): Unit = {
    val current_player =
      if (controller.game.players.nonEmpty) controller.game.players(controller.game.current_idx)


    contents = new BoxPanel(Orientation.Horizontal) {
      background = poolTableGreen
      contents += control_panel
      contents +=
        new BorderPanel() {
          background = poolTableGreen
          add(DealerPanel(controller.game.dealer), BorderPanel.Position.North)
          add(player_panel, BorderPanel.Position.Center)
          add(player_control_panel, BorderPanel.Position.South)
          //border = Swing.EmptyBorder(10, 0 , 0, 10)
        }
      border = Swing.EmptyBorder(10, 10, 10, 10)
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
