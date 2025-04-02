package blackjack.models

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import model.*
import model.modelComponent.{Player, PlayerState}

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" should {

    "initialize with default values" in {
      val player = Player(name = "Alice")

      player.name shouldBe "Alice"
      player.money shouldBe 1000
      player.bet shouldBe 0
      player.state shouldBe PlayerState.Idle
    }

    "update bet and money properly" in {
      val player = Player(name = "Alice", money = 900, bet = 100)

      player.money shouldBe 900
      player.bet shouldBe 100
    }

    "change state correctly" in {
      val player = Player(name = "Alice", state = PlayerState.Playing)
      player.state shouldBe PlayerState.Playing
    }
  }
}

