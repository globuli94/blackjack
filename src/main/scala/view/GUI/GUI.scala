package view.GUI

import controller.ControllerInterface
import model.modelComponent.GameState.Initialized
import model.modelComponent.{GameState, Player}
import util.Event.*
import util.{Event, Observer}
import view.GUI.{ControlPanel, DealerPanel}

import java.awt.{Color, Font, Graphics2D, RenderingHints}
import java.net.URL
import javax.swing.{ImageIcon, WindowConstants}
import scala.swing.*
import scala.swing.MenuBar.NoMenuBar.revalidate
import scala.swing.event.WindowClosing

class GUI(controller: ControllerInterface) extends Frame with Observer {
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

  menuBar = new MenuBar {
    contents += new Menu("Game") {
      contents += new MenuItem(Action("Load") { controller.loadGame() })
    }
  }

  peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
  // Listen to the WindowClosing event (triggered by the close button)
  reactions += {
    case WindowClosing(_) =>
      // Call controller.exit() when the window close button is clicked
      controller.exit() // Handle exit logic in the controller
      sys.exit(0) // Exit the application
  }

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

  private val start_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Start") {
      controller.startGame()
    }
  }

  private val reset_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Reset") {
      controller.initializeGame()
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


    contents +=
      new BoxPanel(Orientation.Vertical) {
        background = poolTableGreen
        preferredSize = new Dimension(220,400)
        minimumSize = new Dimension(220,400)
        maximumSize = new Dimension(220,400)

        contents += Swing.HStrut(5)
        if (controller.getGame.getState == GameState.Initialized && controller.getGame.getPlayers.nonEmpty) contents += start_button
        contents += Swing.HStrut(5)
        if(controller.getGame.getState == GameState.Initialized) contents += add_player_button
        contents += Swing.HStrut(5)
        contents += reset_button

        contents += Swing.HStrut(20)
        contents += exit_button
        contents += Swing.HStrut(20)
        border = Swing.LineBorder(Color.black)
      }
  }

  private def player_panel: FlowPanel = new FlowPanel() {
    preferredSize = new Dimension(980, 400)
    minimumSize = new Dimension(980, 400)
    maximumSize = new Dimension(980, 400)
    background = poolTableGreen
    contents.clear() // Remove old players
    for ((player, index) <- controller.getGame.getPlayers.zipWithIndex) {
      val panel = if (controller.getGame.getIndex == index) {
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

    val player: Player = controller.getGame.getPlayers(controller.getGame.getIndex)

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
      if (controller.getGame.getPlayers.nonEmpty) controller.getGame.getPlayers(controller.getGame.getIndex)


    contents = new BoxPanel(Orientation.Horizontal) {
      background = poolTableGreen
      contents += control_panel
      contents +=
        new BorderPanel() {
          background = poolTableGreen
          if(controller.getGame.getDealer.hand.cards.nonEmpty) add(DealerPanel(controller.getGame.getDealer), BorderPanel.Position.North)
          if(controller.getGame.getPlayers.nonEmpty) add(player_panel, BorderPanel.Position.Center)
          if(controller.getGame.getPlayers.nonEmpty) add(player_control_panel, BorderPanel.Position.South)
          //border = Swing.EmptyBorder(10, 0 , 0, 10)
        }
      border = Swing.EmptyBorder(10, 10, 10, 10)
    }

    revalidate() // Ensures layout is recalculated
    repaint() // Redraws UI
  }

  def update(e: Event): Unit = {
    e match {
      case Event.invalidBet =>
        Dialog.showMessage(
          message = "Insufficient Funds!",
          title = "Error",
          messageType = Dialog.Message.Error
        )
      case Event.errPlayerNameExists => 
        Dialog.showMessage(
        message = "Player already exists!",
        title = "Error",
        messageType = Dialog.Message.Error
      )
      case _ => rebuildUI()
    }
  }
}
