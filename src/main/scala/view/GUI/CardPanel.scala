package view.GUI

import model.modelComponent.Card

import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Image}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import scala.swing.*

class CardPanel(card: Card, scalePercent: Double = 0.5) extends Label {
  background = new Color(0x0e5932)

  val suit: String = card.suit
  val rank: String =
    card.rank match {
      case "J" => "Jack"
      case "Q" => "Queen"
      case "K" => "King"
      case "A" => "Ace"
      case "blank" => "back"
      case _ => card.rank
    }

  val path: String =
    if(card.rank == "blank") s"src/main/resources/deck_pngs/back.png" else s"src/main/resources/deck_pngs/$suit$rank.png"


  // Function to resize image by percentage
  private def resizeImage(path: String, scale: Double): ImageIcon = {
    val originalImage: BufferedImage = ImageIO.read(new File(path)) // Load original image

    // Calculate new dimensions
    val newWidth = (originalImage.getWidth * scale).toInt
    val newHeight = (originalImage.getHeight * scale).toInt

    // Create resized BufferedImage
    val resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = resizedImage.createGraphics()
    g.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
    g.dispose()

    new ImageIcon(resizedImage) // Return resized image as ImageIcon
  }

  // Apply resizing with the given percentage
  icon = resizeImage(path, scalePercent)
}

