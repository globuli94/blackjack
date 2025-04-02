package blackjack.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.*
import model.modelComponent.{Card, Hand}

class HandSpec extends AnyWordSpec with Matchers {

  "A Hand" should {

    "correctly add a card" in {
      val hand = Hand()
      val card = Card("2", "Hearts")
      hand.addCard(card).cards should contain theSameElementsAs List(card)
    }

    "calculate the correct value for different hands" in {
      Hand(List(Card("10", "Hearts"), Card("7", "Diamonds"))).getHandValue shouldBe 17
      Hand(List(Card("A", "Hearts"), Card("7", "Diamonds"))).getHandValue shouldBe 18  // Ace as 11
      Hand(List(Card("A", "Hearts"), Card("A", "Diamonds"), Card("10", "Clubs"))).getHandValue shouldBe 12  // Aces as 1
    }

    "identify if the hand is bust or has blackjack" in {
      Hand(List(Card("A", "Hearts"), Card("K", "Diamonds"), Card("10", "Clubs"))).isBust shouldBe false
      Hand(List(Card("A", "Hearts"), Card("K", "Diamonds"))).hasBlackjack shouldBe true
    }

    "correctly identify if the hand can double down" in {
      // Hand: Ace + 7 => Total: 18, cannot double down (18 is not 9, 10, or 11)
      Hand(List(Card("A", "Hearts"), Card("7", "Diamonds"))).canDoubleDown shouldBe false
      // Hand: 9 + Ace => Total: 20, cannot double down (20 is not 9, 10, or 11)
      Hand(List(Card("9", "Clubs"), Card("A", "Hearts"))).canDoubleDown shouldBe false
      // Hand: 5 + 5 => Total: 10, can double down (10 is valid)
      Hand(List(Card("5", "Hearts"), Card("5", "Diamonds"))).canDoubleDown shouldBe true
      // Hand: Ace + 10 => Total: 21, cannot double down (already blackjack)
      Hand(List(Card("A", "Hearts"), Card("10", "Diamonds"))).canDoubleDown shouldBe false
    }

    "correctly identify if the hand can hit" in {
      Hand(List(Card("A", "Hearts"), Card("A", "Diamonds"))).canHit shouldBe true
      Hand(List(Card("10", "Hearts"), Card("7", "Diamonds"), Card("7", "Clubs"))).canHit shouldBe false
    }

    "correctly identify if the hand can split" in {
      Hand(List(Card("A", "Hearts"), Card("A", "Diamonds"))).canSplit shouldBe true
      Hand(List(Card("10", "Hearts"), Card("7", "Diamonds"))).canSplit shouldBe false
    }

    "return correct string representation" in {
      Hand(List(Card("A", "Hearts"), Card("K", "Diamonds"))).toString shouldBe "[♥ A][♦ K]"
    }
  }
}

