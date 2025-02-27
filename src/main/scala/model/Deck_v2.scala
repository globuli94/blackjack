package model

import scala.collection.immutable.Queue
import scala.util.Random

case class Deck_v2(deck: Queue[Card] = Queue.empty) {
  private val suits = List("Hearts", "Diamonds", "Clubs", "Spades")
  private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")

  // Returns a new deck with freshly shuffled cards
  def shuffle: Deck_v2 = {
    val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)))
    val shuffledList = Random.shuffle(cardList)
    val queue = Queue.from(shuffledList) // Immutable queue
    Deck_v2(queue)
  }

  // Draws a card from the deck, returning a new deck without the drawn card
  def draw(): (Card, Deck_v2) = deck.dequeueOption match {
    case Some((card, remainingDeck)) => (card, Deck_v2(remainingDeck)) // Returns card + new deck
    case None => throw new NoSuchElementException("Deck is empty")
  }
}

