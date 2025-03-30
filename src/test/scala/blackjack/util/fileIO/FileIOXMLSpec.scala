package blackjack.util.fileIO

import model.dealerComponent.DealerState
import util.fileIOComponent.XML.FileIOXML
import model.gameComponent.{GameInterface, GameState}
import model.playerComponent.PlayerState

class FileIOXMLSpec extends FileIOTestBase {
  val fileIO: FileIOXML = new FileIOXML
  import fileIO.gameStateFromString
  import fileIO.dealerStateFromString
  import fileIO.playerStateFromString

  "A FileIOXML Interface" should {

    "load a GameInterface from xml" in {
      val game: GameInterface = fileIO.load("game_test.xml")

      game.getState should be (GameState.Started)
    }

    "parse player state from string" in {
      playerStateFromString("Playing") should be (PlayerState.Playing)
      playerStateFromString("Standing") should be (PlayerState.Standing)
      playerStateFromString("DoubledDown") should be (PlayerState.DoubledDown)
      playerStateFromString("Busted") should be (PlayerState.Busted)
      playerStateFromString("Blackjack") should be (PlayerState.Blackjack)
      playerStateFromString("WON") should be (PlayerState.WON)
      playerStateFromString("LOST") should be (PlayerState.LOST)
      playerStateFromString("Betting") should be (PlayerState.Betting)
      playerStateFromString("Idle") should be (PlayerState.Idle)
      playerStateFromString("Split") should be (PlayerState.Split)
      an[IllegalArgumentException] should be thrownBy playerStateFromString("")
    }

    "parse a dealer state from string" in {
      dealerStateFromString("Idle") should be(DealerState.Idle)
      dealerStateFromString("Dealing") should be(DealerState.Dealing)
      dealerStateFromString("Bust") should be(DealerState.Bust)
      dealerStateFromString("Standing") should be(DealerState.Standing)
      an[IllegalArgumentException] should be thrownBy dealerStateFromString("")
    }

    "parse a game state from string" in {
      gameStateFromString("Initialized") should be(GameState.Initialized)
      gameStateFromString("Betting") should be(GameState.Betting)
      gameStateFromString("Started") should be(GameState.Started)
      gameStateFromString("Evaluated") should be(GameState.Evaluated)
      an[IllegalArgumentException] should be thrownBy gameStateFromString("")
    }

    "convert a game into xml" in {
      val game: GameInterface = fileIO.load("game_test.xml")

      fileIO.save(game)
      val game_loaded: GameInterface = fileIO.load("game.xml")

      game_loaded.getState should be (game.getState)
    }
  }
}
