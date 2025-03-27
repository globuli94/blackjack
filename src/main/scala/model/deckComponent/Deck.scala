package model.deckComponent

import model.cardComponent.{Card, CardInterface}

import scala.collection.immutable.Queue
import scala.util.Random

case class Deck(deck: Queue[CardInterface] = Queue.empty) extends DeckInterface {
  private val suits = List("Hearts", "Diamonds", "Clubs", "Spades")
  private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")

  // Returns a new deck with freshly shuffled cards
  override def shuffle: DeckInterface = {
    val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)))
    val shuffledList = Random.shuffle(cardList)
    val queue = Queue.from(shuffledList) // Immutable queue
    Deck(queue)
  }

  // Draws a card from the deck, returning a new deck without the drawn card
  override def draw: (CardInterface, DeckInterface) = deck.dequeueOption match {
    case Some((card, remainingDeck)) => (card, copy(deck = remainingDeck)) // Returns card + new deck
    case None => throw new NoSuchElementException("Deck is empty")
  }
}

