package model.deckComponent

import model.cardComponent.{Card, CardInterface}

import scala.collection.immutable.Queue
import scala.util.Random

case class Deck(cards: List[CardInterface] = List.empty) extends DeckInterface {
  private val suits = List("Hearts", "Diamonds", "Clubs", "Spades")
  private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")

  override def getCards: Seq[CardInterface] = cards
  override def length: Int = cards.length
  override def unique_cards: Int = cards.distinct.size

  // Returns a new deck with freshly shuffled cards
  override def shuffle: DeckInterface = {
    val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)))
    val shuffledList = Random.shuffle(cardList)
    copy(shuffledList)
  }

  // Draws a card from the deck, returning a new deck without the drawn card
  override def draw: (CardInterface, DeckInterface) = cards match {
    case head :: tail => (head, copy(cards = tail)) // Returns card + new deck
    case Nil => throw new NoSuchElementException("Deck is empty")
  }
}