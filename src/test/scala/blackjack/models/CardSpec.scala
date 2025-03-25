package blackjack.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import model.Card

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {
    "return the correct value for face cards" in {
      Card("A", "Hearts").value shouldBe 11
      Card("K", "Diamonds").value shouldBe 10
      Card("Q", "Clubs").value shouldBe 10
      Card("J", "Spades").value shouldBe 10
    }

    "return the correct value for numbered cards" in {
      Card("2", "Hearts").value shouldBe 2
      Card("7", "Diamonds").value shouldBe 7
      Card("10", "Clubs").value shouldBe 10
    }

    "return zero for blank cards" in {
      Card("blank", "Spades").value shouldBe 0
    }

    "display the correct string representation" in {
      Card("A", "Hearts").toString shouldBe "[♥ A]"
      Card("10", "Diamonds").toString shouldBe "[♦ 10]"
      Card("K", "Clubs").toString shouldBe "[♣ K]"
      Card("3", "Spades").toString shouldBe "[♠ 3]"
    }
  }
}
