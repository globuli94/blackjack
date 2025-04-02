package model.modelComponent

import model.modelComponent.Card

import scala.collection.immutable.Queue
import scala.util.Random

case class Deck(cards: List[Card] = List.empty) {
  private val suits = List("Hearts", "Diamonds", "Clubs", "Spades")
  private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")

  def length: Int = cards.length
  def unique_cards: Int = cards.distinct.size

  // Returns a new deck with freshly shuffled cards
  def shuffle: Deck = {
    val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)))
    val shuffledList = Random.shuffle(cardList)
    copy(shuffledList)
  }

  // Draws a card from the deck, returning a new deck without the drawn card
  def draw: (Card, Deck) = cards match {
    case head :: tail => (head, copy(cards = tail)) // Returns card + new deck
    case Nil => throw new NoSuchElementException("Deck is empty")
  }
}