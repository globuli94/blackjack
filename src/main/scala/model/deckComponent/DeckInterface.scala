package model.deckComponent

import model.cardComponent.CardInterface

import scala.collection.immutable.Queue

trait DeckInterface {
  def getDeck: List[CardInterface]
  def unique_cards: Int
  def length: Int
  def shuffle: DeckInterface
  def draw: (CardInterface, DeckInterface)
}
