package util

import model.ModelInterface


trait FileIOInterface {
  def load(path: String = ""): ModelInterface
  def save(game: ModelInterface, path: String = ""): Unit
}
