package model.deckComponent

import model.cardComponent.CardInterface

trait DeckInterface {
  def shuffle: DeckInterface
  def draw: (CardInterface, DeckInterface)
}
