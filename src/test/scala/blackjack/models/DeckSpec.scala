package blackjack.models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.Queue
import scala.util.Random
import model.cardComponent.{Card, CardInterface}
import model.deckComponent.Deck

class DeckSpec extends AnyWordSpec with Matchers {

  "A Deck" should {

    "be initialized as empty by default" in {
      val deck = Deck()
      deck.cards shouldBe empty
    }

    "contain 52 cards after shuffling" in {
      val deck = Deck().shuffle
      deck.length shouldBe 52
    }

    "have unique cards after shuffling" in {
      val deck = Deck().shuffle
      deck.unique_cards shouldBe 52
    }

    "draw a card and return a new deck with one less card" in {
      val deck = Deck().shuffle
      val (drawnCard, newDeck) = deck.draw

      drawnCard shouldBe a[CardInterface]
      newDeck.length shouldBe (deck.length - 1)
    }

    "throw an exception when drawing from an empty deck" in {
      val deck = Deck(List.empty)
      an[NoSuchElementException] should be thrownBy deck.draw
    }
  }
}

