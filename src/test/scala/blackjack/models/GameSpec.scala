package blackjack.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.*
import model.DealerState.Bust
import model.PlayerState.*

class GameSpec extends AnyWordSpec with Matchers {

  "A Game" should {

    "initially have no players and a new dealer" in {
      val game = Game()

      game.players shouldBe empty
      game.dealer should equal(new Dealer())
      game.state shouldBe GameState.Initialized
    }

    "correctly add players up to the maximum of 4" in {
      val game = Game()
      val game1 = game.createPlayer("Alice")
      val game2 = game1.createPlayer("Bob")
      val game3 = game2.createPlayer("Charlie")
      val game4 = game3.createPlayer("David")
      val game5 = game4.createPlayer("Eve") // Should not add a 5th player

      game5.players.size shouldBe 4
      game5.players.map(_.name) should contain allElementsOf List("Alice", "Bob", "Charlie", "David")
    }

    "correctly remove the current player and adjust index" in {
      val player1 = Player("Alice")
      val player2 = Player("Bob")
      val player3 = Player("Charlie")
      val game = Game(players = List(player1, player2, player3), current_idx = 2)

      val updatedGame = game.leavePlayer() // Should remove Charlie

      updatedGame.players should not contain player3
      updatedGame.players should contain allElementsOf List(player1, player2)
      updatedGame.current_idx shouldBe 0 // Index resets

      val empty_game = Game(players = List(player1)).leavePlayer()
      empty_game.players.length shouldBe 0
    }

    "correctly remove a player by name" in {
      val player1 = Player("Player1", money = 100)
      val player2 = Player("Player2", money = 100)
      val game = Game(players = List(player1, player2))

      val newGame = game.leavePlayer("Player1")

      newGame.players should contain theSameElementsAs List(player2)
    }

    "deal cards to each player and the dealer" in {
      val game = Game().createPlayer("Player1")
      val dealtGame = game.deal

      dealtGame.state shouldBe GameState.Started
    }

    "dealer should hit when hand value is less than 17" in {
      val initialDealerHand = Hand(List(Card("5", "Hearts"), Card("6", "Diamonds")))// Value = 11 (Must hit)
      val game = Game().copy(dealer = Dealer(initialDealerHand)).startGame // Next card = 7

      val updatedGame = game.hitDealer
      updatedGame.dealer.hand.hand.length == 3
    }

    "stand dealer when at or above 17" in {
      val game = Game().copy(dealer = Dealer(Hand(List(Card("10", "Hearts"), Card("7", "Diamonds"))))).startGame
      val updatedGame = game.evaluate

      updatedGame.dealer.state shouldBe DealerState.Standing
    }

    "mark dealer busted over 21" in {
      val game = Game().copy(dealer = Dealer(Hand(List(Card("10", "Hearts"), Card("10", "Diamonds"), Card("10", "Diamonds"))))).startGame
      val updatedGame = game.evaluate

      updatedGame.dealer.state shouldBe Bust
    }

    "correctly allow a player to hit" in {
      val player = Player("Alice", hand = Hand(List(Card("5", "Hearts"), Card("6", "Diamonds"))))
      val player2 = Player("Alice2", hand = Hand(List(Card("5", "Hearts"), Card("6", "Diamonds"))))

      val game = Game(players = List(player, player2)).deal

      val updatedGame = game.hitPlayer

      updatedGame.players.head.hand.hand.length == 3
      updatedGame.players(1).hand.hand.length == 2
    }

    "return input game when acting on empty game" in {
      val game = Game()
      val hit_game = game.hitPlayer
      val stand_game = game.standPlayer
      val bet_game = game.betPlayer

      hit_game should equal(game)
      stand_game should equal(game)
      bet_game should equal(game)
    }

    "mark a player as busted when they exceed 21" in {
      val player = Player("Alice", hand = Hand(List(Card("10", "Hearts"), Card("8", "Diamonds"), Card("5", "Clubs"))))
      val game = Game(players = List(player)).startGame

      val updatedGame = game.hitPlayer

      updatedGame.players.head.state shouldBe PlayerState.Busted
    }

    "mark a player as blackjack when they have 21" in {
      val player = Player("Alice", hand = Hand(List(Card("10", "Hearts"), Card("A", "Diamonds"), Card("5", "Clubs"))))
      val game = Game(players = List(player)).startGame

      val updatedGame = game.evaluate

      updatedGame.players.head.state shouldBe PlayerState.Blackjack
    }

    "allow a player to stand" in {
      val player = Player("Alice", state = Playing)
      val game = Game(players = List(player)).startGame

      val updatedGame = game.standPlayer

      updatedGame.players.head.state shouldBe PlayerState.Standing
    }

    "allow a player to bet and subtract money correctly" in {
      val player = Player("Alice", money = 100)
      val game = Game(players = List(player)).startGame

      val updatedGame = game.betPlayer(20)

      updatedGame.players.head.money shouldBe 80
      updatedGame.players.head.bet shouldBe 20
      updatedGame.players.head.state shouldBe PlayerState.Playing
      updatedGame.current_idx shouldBe game.current_idx + 1
    }

    "not allow a player to bet more than they have" in {
      val player = Player("Alice", money = 10)
      val game = Game(players = List(player)).startGame

      val isValid = game.isValidBet(20)

      isValid shouldBe false
    }

    "allow a player to double down" in {
      val player = Player("Alice", money = 100, bet = 20, hand = Hand(List(Card("5", "Hearts"), Card("6", "Diamonds"))))
      val game = Game(players = List(player)).startGame

      val updatedGame = game.doubleDownPlayer

      updatedGame.players.head.money shouldBe 60 // 100 - 40 (double bet)
      updatedGame.players.head.bet shouldBe 40
      updatedGame.players.head.hand.hand.length shouldBe 3 // One extra card
      updatedGame.players.head.state shouldBe PlayerState.DoubledDown
    }

    "evaluate the game correctly when all players have finished" in {
      val player1 = Player("Alice", money = 100, bet = 10, state = Standing, hand = Hand(List(Card("10", "Hearts"), Card("7", "Diamonds"))))
      val player2 = Player("Bob", money = 100, bet = 10, state = Busted, hand = Hand(List(Card("10", "Hearts"), Card("9", "Diamonds"), Card("5", "Clubs"))))
      val dealer = Dealer(Hand(List(Card("10", "Hearts"), Card("8", "Diamonds"))))
      val game = Game(players = List(player1, player2), dealer = dealer, state = GameState.Started)

      val evaluatedGame = game.evaluate

      evaluatedGame.state shouldBe GameState.Evaluated
      evaluatedGame.players.head.state shouldBe PlayerState.LOST // Dealer had 18, player had 17
      evaluatedGame.players(1).state shouldBe PlayerState.LOST // Busted player always loses
    }

    "reset to initialized state after evaluation" in {
      val player = Player("Alice", state = PlayerState.LOST)
      val game = Game(players = List(player), state = GameState.Evaluated)

      val newGame = game.evaluate

      newGame.state shouldBe GameState.Initialized
    }
  }
}
