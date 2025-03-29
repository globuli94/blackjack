package model.fileIOComponent

import model.gameComponent.GameInterface
import model.handComponent.HandInterface

trait FileIOInterface {
  def load: GameInterface
  def save(game: GameInterface): Unit
}
