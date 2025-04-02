package blackjack.controllers

import controller.ControllerInterface
import controller.controllerComponent.Controller
import model.ModelInterface
import model.modelComponent.PlayerState.{Betting, Idle}
import model.modelComponent.{Card, Deck, Game, GameState, Hand, Player, PlayerState}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.should
import util.FileIOInterface
import util.fileIOComponent.JSON.FileIOJSON

class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {
    val fileIO: FileIOInterface =  FileIOJSON()

    "load the last saved game on load" in {
      val game: ModelInterface = Game()
      val controller: ControllerInterface = Controller(game, fileIO)

      controller.saveGame()
      controller.loadGame()

      controller.getGame should equal(game)
    }

    "initialize a game" in {
      val game: ModelInterface = Game()
      val controller: ControllerInterface = Controller(game, fileIO)

      controller.initializeGame()

      val new_state = controller.getGame.getState

      new_state should be(GameState.Initialized)
    }

    "start not start a game without players" in {
      val game: ModelInterface = Game()
      val controller: ControllerInterface = Controller(game, fileIO)

      controller.initializeGame()
      controller.startGame()

      controller.getGame.getState should be(GameState.Initialized)
    }

    "start a game with players" in {
      val player: Player = Player("Steve")
      val player2: Player = Player("Mark")
      val game1: ModelInterface = Game(players = List(player, player2), state = GameState.Initialized)
      val game2: ModelInterface = Game(players = List(player, player2), state = GameState.Evaluated)
      val controller1: ControllerInterface = Controller(game1, fileIO)
      val controller2: ControllerInterface = Controller(game2, fileIO)

      controller1.startGame()
      controller2.startGame()

      controller1.getGame.getState should be(GameState.Betting)
      controller2.getGame.getState should be(GameState.Betting)
    }

    "add a player" in {
      val game1: ModelInterface = Game(state = GameState.Initialized)
      val game2: ModelInterface = Game(state = GameState.Evaluated)
      val game3: ModelInterface = Game(state = GameState.Betting)

      val controller1: ControllerInterface = Controller(game1, fileIO)
      val controller2: ControllerInterface = Controller(game2, fileIO)
      val controller3: ControllerInterface = Controller(game3, fileIO)

      controller1.addPlayer("Steve")
      controller2.addPlayer("Steve")
      controller2.addPlayer("Steve")
      controller3.addPlayer("Steve")

      controller1.getGame.getPlayers.length should be(1)
      controller2.getGame.getPlayers.length should be(1)
      controller3.getGame.getPlayers.length should be(0)
    }

    "leave a player when game not empty" in {
      val game1: ModelInterface = Game(state = GameState.Initialized)
      val controller1: ControllerInterface = Controller(game1, fileIO)

      controller1.addPlayer("Steve")
      controller1.leavePlayer()

      controller1.getGame.getPlayers.length should be(0)
    }

    "not leave a player when game empty" in {
      val game1: ModelInterface = Game(state = GameState.Initialized)
      val controller1: ControllerInterface = Controller(game1, fileIO)

      controller1.leavePlayer()

      controller1.getGame.getPlayers.length should be(0)
    }

    "hit next player if can hit" in {
      val deck: Deck = Deck().shuffle
      val player: Player = Player("test", state = PlayerState.Playing)
      val hand: Hand = player.hand.addCard(deck.draw(0))
      val player_with_hand = Player(name = player.name, hand = hand)

      val game1: ModelInterface =
        Game(
          state = GameState.Started,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1, fileIO)

      controller.hitPlayer()

      controller.game.getPlayers.head.hand.cards.length should be(2)
    }

    "not hit next player if game not started" in {
      val deck: Deck = Deck().shuffle
      val player: Player = Player("test", state = PlayerState.Playing)
      val hand: Hand = player.hand.addCard(deck.draw(0))
      val player_with_hand = Player(name = player.name, hand = hand)

      val game1: ModelInterface =
        Game(
          state = GameState.Initialized,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1, fileIO)
      controller.hitPlayer()

      controller.getGame.getState should be (GameState.Initialized)
    }

    "stand next player when game state is started" in {
      val deck = Deck().shuffle
      val game1: ModelInterface =
        Game(
          state = GameState.Started,
          players = List(Player("StandingPlayer"), Player("notStanding", state = PlayerState.Playing)),
          deck = deck)
      val controller: ControllerInterface = Controller(game1, fileIO)

      controller.standPlayer()
      controller.getGame.getPlayers.head.state should be (PlayerState.Standing)
    }

    "not stand next player if game not started" in {
      val game1: ModelInterface =
        Game(state = GameState.Initialized)

      val controller = Controller(game1, fileIO)
      controller.standPlayer()

      controller.getGame.getState should be(GameState.Initialized)
    }

    "double down next player if possible" in {
      val deck: Deck = Deck().shuffle
      val player: Player = Player("test")
      val hand: Hand = Hand().addCard(Card("2", "Hearts")).addCard(Card("7", "Hearts"))
      val player_with_hand = Player(name = player.name, hand = hand, bet = 100, money = 200, state = PlayerState.Betting)

      val game1: ModelInterface =
        Game(
          state = GameState.Started,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1, fileIO)
      controller.doubleDown()

      controller.game.getPlayers.head.hand.cards.length should be(3)
    }

    "not double down if not possible" in {
      val deck: Deck = Deck().shuffle
      val player: Player = Player("test")
      val hand: Hand = Hand().addCard(Card("5", "Hearts")).addCard(Card("7", "Hearts"))
      val player_with_hand = Player(name = player.name, hand = hand, bet = 100, money = 200, state = PlayerState.Betting)

      val game1: ModelInterface =
        Game(
          state = GameState.Started,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1, fileIO)
      controller.doubleDown()

      controller.game.getPlayers.head.hand.cards.length should be(2)
    }

    "bet if allowed by game state" in {
      val card: Card = Card("2", "Hearts")
      val deck: Deck = Deck(List(card, card))
      val player: Player = Player(name = "steve", bet = 0, money = 200, state = PlayerState.Betting)

      val game1: ModelInterface =
        Game(
          state = GameState.Betting,
          players = List(player),
          deck = deck)

      val controller = Controller(game1, fileIO)
      controller.bet("100")

      controller.getGame.getPlayers.head.bet should be (100)
    }

    "not bet if bet is invalid" in {
      val deck: Deck = Deck().shuffle
      val player: Player = Player("test")
      val player_with_hand = Player(name = player.name, bet = 0, money = 200, state = PlayerState.Betting)

      val game1: ModelInterface =
        Game(
          state = GameState.Betting,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1, fileIO)
      controller.bet("1000")

      controller.getGame.getPlayers.head.state should be(PlayerState.Betting)

      controller.bet("test")

      controller.getGame.getPlayers.head.state should be(PlayerState.Betting)
    }

    "not bet if not allowed by game state" in {
      val deck: Deck = Deck().shuffle
      val player: Player = Player("test")
      val player_with_hand = Player(name = player.name, bet = 0, money = 200, state = PlayerState.Betting)

      val game1: ModelInterface =
        Game(
          state = GameState.Started,
          players = List(player_with_hand, player),
          deck = deck)

      val controller = Controller(game1, fileIO)
      controller.bet("100")

      controller.getGame.getPlayers.head.bet should be(0)
    }

    "create a string on tostring" in {
      val game : ModelInterface = Game()
      val controller : ControllerInterface = Controller(game, fileIO)

      controller.toString should be (a[String])
    }
  }
}
