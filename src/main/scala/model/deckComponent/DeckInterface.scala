package model.deckComponent

import model.cardComponent.CardInterface

trait DeckInterface {
  def unique_cards: Int
  def length: Int
  def shuffle: DeckInterface
  def draw: (CardInterface, DeckInterface)
}
