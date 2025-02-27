package view

import model.{Card, Hand, Player}
import view.CardPanel

import javax.swing.BorderFactory
import scala.swing.{BoxPanel, Color, Dimension, Frame, Label, Orientation, Swing}

class PlayerPanel(player: Player) extends BoxPanel(Orientation.Vertical) {
  preferredSize = new Dimension(300, 200)
  minimumSize = new Dimension(300, 200)
  maximumSize = new Dimension(300, 200)
  background = new Color(0x0e5932)

  private val cards: Seq[Card] = player.hand.hand

  private val thickBorder = BorderFactory.createLineBorder(java.awt.Color.BLACK, 3) // 3px thick border
  border = BorderFactory.createTitledBorder(thickBorder ,player.name)


  contents += new BoxPanel(Orientation.Horizontal) {
    background = new Color(0x0e5932)

    val numCards: Int = cards.length
    var scale: Double = 0

    numCards match
      case 2 => scale = 0.5
      case 3 => scale = 0.4
      case 4 => scale = 0.3
      case _ => scale = 0.2

    contents += Swing.HStrut(10)
    for (card <- cards) {
      contents += new CardPanel(card, scale)
      contents += Swing.HStrut(5)
    }
    contents += Swing.HStrut(10)
  }


  contents += new Label(s"Bank: ${player.money}")
  contents += new Label(s"Bet: ${player.bet}")
  contents += Swing.VGlue // Pushes everything to center

}