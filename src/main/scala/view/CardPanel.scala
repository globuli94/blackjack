package view

import model.cardComponent.{Card, CardInterface}

import scala.swing.*
import javax.swing.ImageIcon
import java.awt.{Graphics2D, Image}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

class CardPanel(card: CardInterface, scalePercent: Double = 0.5) extends Label {
  background = new Color(0x0e5932)

  val suit: String = card.getSuit
  val rank: String =
    card.getRank match {
      case "J" => "Jack"
      case "Q" => "Queen"
      case "K" => "King"
      case "A" => "Ace"
      case "blank" => "back"
      case _ => card.getRank
    }

  val path: String =
    if(card.getRank == "blank") s"src/main/resources/deck_pngs/back.png" else s"src/main/resources/deck_pngs/$suit$rank.png"


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

