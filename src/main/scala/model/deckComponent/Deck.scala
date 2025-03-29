package model.deckComponent

import model.cardComponent.{Card, CardInterface}

import scala.collection.immutable.Queue
import scala.util.Random

case class Deck(deck: List[CardInterface] = List.empty) extends DeckInterface {
  private val suits = List("Hearts", "Diamonds", "Clubs", "Spades")
  private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")

  override def getDeck: List[CardInterface] = deck
  override def length: Int = deck.length
  override def unique_cards: Int = deck.distinct.size

  // Returns a new deck with freshly shuffled cards
  override def shuffle: DeckInterface = {
    val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)))
    val shuffledList = Random.shuffle(cardList)
    Deck(shuffledList)
  }

  // Draws a card from the deck, returning a new deck without the drawn card
  override def draw: (CardInterface, DeckInterface) = deck match {
    case head :: tail => (head, copy(deck = tail)) // Returns card + new deck
    case Nil => throw new NoSuchElementException("Deck is empty")
  }
}