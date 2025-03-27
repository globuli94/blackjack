package model.deckComponent

trait DeckInterface {
  def shuffle: Deck
  def draw: (Card, Deck)
}
