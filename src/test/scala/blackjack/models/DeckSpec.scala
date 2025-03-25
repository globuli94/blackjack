package blackjack.models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.immutable.Queue
import scala.util.Random

import model.Deck
import model.Card

class DeckSpec extends AnyWordSpec with Matchers {

  "A Deck" should {

    "be initialized as empty by default" in {
      val deck = Deck()
      deck.deck shouldBe empty
    }

    "contain 52 cards after shuffling" in {
      val deck = Deck().shuffle
      deck.deck.size shouldBe 52
    }

    "have unique cards after shuffling" in {
      val deck = Deck().shuffle
      deck.deck.distinct.size shouldBe 52
    }

    "draw a card and return a new deck with one less card" in {
      val deck = Deck().shuffle
      val (drawnCard, newDeck) = deck.draw()

      drawnCard shouldBe a[Card]
      newDeck.deck.size shouldBe (deck.deck.size - 1)
    }

    "throw an exception when drawing from an empty deck" in {
      val deck = Deck(Queue.empty)
      an[NoSuchElementException] should be thrownBy deck.draw()
    }
  }
}

