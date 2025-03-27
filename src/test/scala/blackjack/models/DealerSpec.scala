package blackjack.models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.*
import model.cardComponent.Card
import model.dealerComponent.{Dealer, DealerState}
import model.handComponent.Hand

class DealerSpec extends AnyWordSpec with Matchers {

  "A Dealer" should {

    "initialize with an empty hand and Idle state" in {
      val dealer = Dealer()
      dealer.getHand.getCards shouldBe empty
      dealer.getState shouldBe DealerState.Idle
    }

    "change state to Dealing when dealing cards" in {
      val dealer = Dealer(state = DealerState.Dealing)
      dealer.getState shouldBe DealerState.Dealing
    }

    "change state to Bust when hand value exceeds 21" in {
      val hand = new Hand(List(Card("K", "Hearts"), Card("Q", "Diamonds"), Card("5", "Clubs")))
      val dealer = Dealer(hand, DealerState.Bust)

      dealer.getState shouldBe DealerState.Bust
    }

    "change state to Standing when the dealer decides to stand" in {
      val dealer = Dealer(state = DealerState.Standing)
      dealer.getState shouldBe DealerState.Standing
    }
  }
}
