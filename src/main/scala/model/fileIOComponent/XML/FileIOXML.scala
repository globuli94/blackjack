package model.fileIOComponent.XML

import model.fileIOComponent.FileIOInterface
import model.gameComponent.{Game, GameInterface}
import model.handComponent.HandInterface

abstract class FileIOXML extends FileIOInterface {
  override def load: GameInterface = {
    Game()
  }

  override def save(game: GameInterface): Unit = {
    
  }
}
