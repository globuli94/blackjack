package util.fileIOComponent

import model.gameComponent.GameInterface
import model.handComponent.HandInterface

trait FileIOInterface {
  def load(path: String = ""): GameInterface
  def save(game: GameInterface): Unit
}
