package blackjack.controllers

import controller.controllerComponent.{Controller, ControllerInterface}
import model.deckComponent.{Deck, DeckInterface}
import model.gameComponent.{Game, GameInterface, GameState}
import model.handComponent.HandInterface
import model.playerComponent.PlayerState.Betting
import model.playerComponent.{Player, PlayerInterface, PlayerState}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.should

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    "initialize a game" in {
      val game: GameInterface = Game()
      val controller: ControllerInterface = Controller(game)

      controller.initializeGame()

      val new_state = controller.getGame.getState

      new_state should be(GameState.Initialized)
    }

    "start not start a game without players" in {
      val game: GameInterface = Game()
      val controller: ControllerInterface = Controller(game)

      controller.initializeGame()
      controller.startGame()

      controller.getGame.getState should be(GameState.Initialized)
    }

    "start a game with players" in {
      val player: PlayerInterface = Player("Steve")
      val player2: PlayerInterface = Player("Mark")
      val game1: GameInterface = Game(players = List(player, player2), state = GameState.Initialized)
      val game2: GameInterface = Game(players = List(player, player2), state = GameState.Evaluated)
      val controller1: ControllerInterface = Controller(game1)
      val controller2: ControllerInterface = Controller(game2)

      controller1.startGame()
      controller2.startGame()

      controller1.getGame.getState should be(GameState.Betting)
      controller2.getGame.getState should be(GameState.Betting)
    }

    "add a player" in {
      val game1: GameInterface = Game(state = GameState.Initialized)
      val game2: GameInterface = Game(state = GameState.Evaluated)
      val game3: GameInterface = Game(state = GameState.Betting)

      val controller1: ControllerInterface = Controller(game1)
      val controller2: ControllerInterface = Controller(game2)
      val controller3: ControllerInterface = Controller(game3)

      controller1.addPlayer("Steve")
      controller2.addPlayer("Steve")
      controller2.addPlayer("Steve")
      controller3.addPlayer("Steve")

      controller1.getGame.getPlayers.length should be(1)
      controller2.getGame.getPlayers.length should be(1)
      controller3.getGame.getPlayers.length should be(0)
    }

    "leave a player when game not empty" in {
      val game1: GameInterface = Game(state = GameState.Initialized)
      val controller1: ControllerInterface = Controller(game1)

      controller1.addPlayer("Steve")
      controller1.leavePlayer()

      controller1.getGame.getPlayers.length should be(0)
    }

    "not leave a player when game empty" in {
      val game1: GameInterface = Game(state = GameState.Initialized)
      val controller1: ControllerInterface = Controller(game1)

      controller1.leavePlayer()

      controller1.getGame.getPlayers.length should be(0)
    }

    "hit next player if can hit" in {
      val deck: DeckInterface = Deck().shuffle
      val player: PlayerInterface = Player("test", state = PlayerState.Playing)
      val hand: HandInterface = player.getHand.addCard(deck.draw(0))
      val player_with_hand = Player(name = player.getName, hand = hand)

      val game1: GameInterface =
        Game(
          state = GameState.Started,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1)

      controller.hitNextPlayer()

      controller.game.getPlayers.head.getHand.length should be(2)
    }

    "not hit next player if game not started" in {
      val deck: DeckInterface = Deck().shuffle
      val player: PlayerInterface = Player("test", state = PlayerState.Playing)
      val hand: HandInterface = player.getHand.addCard(deck.draw(0))
      val player_with_hand = Player(name = player.getName, hand = hand)

      val game1: GameInterface =
        Game(
          state = GameState.Initialized,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1)
      controller.hitNextPlayer()

      controller.getGame.getState should be (GameState.Initialized)
    }

    "stand next player when game state is started" in {
      val deck = Deck().shuffle
      val game1: GameInterface =
        Game(
          state = GameState.Started,
          players = List(Player("StandingPlayer"), Player("notStanding", state = PlayerState.Playing)),
          deck = deck)
      val controller: ControllerInterface = Controller(game1)

      controller.standNextPlayer()
      controller.getGame.getPlayers.head.getState should be (PlayerState.Standing)
    }

    "not stand next player if game not started" in {
      val game1: GameInterface =
        Game(state = GameState.Initialized)

      val controller = Controller(game1)
      controller.standNextPlayer()

      controller.getGame.getState should be(GameState.Initialized)
    }


    "double down next player if possible" in {
      val deck: DeckInterface = Deck().shuffle
      val player: PlayerInterface = Player("test", state = PlayerState.Playing)
      val hand: HandInterface = player.getHand.addCard(deck.draw(0))
      val player_with_hand = Player(name = player.getName, hand = hand)

      val game1: GameInterface =
        Game(
          state = GameState.Started,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1)

      controller.hitNextPlayer()

      controller.game.getPlayers.head.getHand.length should be(2)
    }
  }
}
