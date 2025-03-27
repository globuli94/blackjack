package view

import controller.controllerComponent.Controller
import model.gameComponent.GameState.{Betting, Evaluated, Initialized, Started}
import model.gameComponent.Game
import model.playerComponent.Player
import util.{Event, Observer}

import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.border.TitledBorder
import scala.swing.{Action, BorderPanel, BoxPanel, Button, Dialog, Dimension, FlowPanel, Font, Orientation, Swing, TextField, Button as player}

case class ControlPanel(controller: Controller) extends FlowPanel {
  private val poolTableGreen = new Color(0x0e5932)
  val player: Player = controller.game.players(controller.game.current_idx)

  background = poolTableGreen
  private val border_color = java.awt.Color.WHITE
  private val thickBorder = BorderFactory.createLineBorder(border_color, 2)
  border = thickBorder
  preferredSize = new Dimension(600, 100)
  minimumSize = new Dimension(600, 100)
  maximumSize = new Dimension(600, 100)

  val button_dimension = new Dimension(100, 50)

  private val bet_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Bet") {
      val bet = Dialog.showInput[String](null,
        null, "Enter Bet Amount: ", Dialog.Message.Plain, Swing.EmptyIcon, Nil, "")
      bet match {
        case Some(bet) =>
          controller.bet(bet)
        case None =>
      }
    }
  }

  private val hit_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Hit") {
      controller.hitNextPlayer()
    }
  }

  private val stand_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Stand") {
      controller.standNextPlayer()
    }
  }

  private val double_down_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Double Down") {
      controller.doubleDown()
    }
  }

  private val continue_button: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Continue") {
      controller.startGame()
    }
  }

  private val leaveButton: Button = new Button {
    preferredSize = button_dimension
    minimumSize = button_dimension
    maximumSize = button_dimension
    action = Action("Leave") {
      controller.leavePlayer()
    }
  }

  contents += new FlowPanel() {
    background = poolTableGreen
    controller.game.state match
      case Betting =>
        contents += bet_button
        contents += leaveButton
      case Started =>
        contents += stand_button
        if (player.hand.canHit) contents += hit_button
        if (player.hand.canDoubleDown) contents += double_down_button
        contents += leaveButton
      case Evaluated =>
        contents += continue_button
      case Initialized =>
        contents += leaveButton


      border = Swing.EmptyBorder(10)
  }
}
