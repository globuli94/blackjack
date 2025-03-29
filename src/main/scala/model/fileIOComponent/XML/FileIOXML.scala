package model.fileIOComponent.XML

import model.fileIOComponent.FileIOInterface
import model.gameComponent.{Game, GameInterface}
import model.handComponent.HandInterface

class FileIOXML extends FileIOInterface {
  override def load: GameInterface = {
    Game()
  }

  override def save(hand: HandInterface): Unit = {

  }
}
