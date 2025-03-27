package blackjack.models

import model.cardComponent.Card
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {
    "return the correct suit" in {
      Card("A", "Hearts").getSuit shouldBe("Hearts")
    }

    "return the correct getValue for face cards" in {
      Card("A", "Hearts").getValue shouldBe 11
      Card("K", "Diamonds").getValue shouldBe 10
      Card("Q", "Clubs").getValue shouldBe 10
      Card("J", "Spades").getValue shouldBe 10
    }

    "return the correct getValue for numbered cards" in {
      Card("2", "Hearts").getValue shouldBe 2
      Card("7", "Diamonds").getValue shouldBe 7
      Card("10", "Clubs").getValue shouldBe 10
    }

    "return zero for blank cards" in {
      Card("blank", "Spades").getValue shouldBe 0
    }

    "display the correct string representation" in {
      Card("A", "Hearts").toString shouldBe "[♥ A]"
      Card("10", "Diamonds").toString shouldBe "[♦ 10]"
      Card("K", "Clubs").toString shouldBe "[♣ K]"
      Card("3", "Spades").toString shouldBe "[♠ 3]"
    }
  }
}
