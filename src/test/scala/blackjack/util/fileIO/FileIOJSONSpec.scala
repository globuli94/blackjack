package blackjack.util.fileIO

import model.modelComponent.{DealerState, GameState, PlayerState}
import util.fileIOComponent.JSON.*
import org.scalamock.function.MockFunction0
import org.scalamock.proxy.MockFunction
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsError, JsString}
import util.fileIOComponent.JSON.FileIOJSON
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers

class FileIOJSONSpec extends FileIOTestBase {
  val fileIO = new FileIOJSON
  import fileIO.playerStateReads
  import fileIO.dealerStateReads
  import fileIO.gameStateReads

  "FileIOJSON" should {

    "read a JSON File and validate states correctly" in {
      JsString("Playing").validate[PlayerState].get should be(PlayerState.Playing)
      JsString("Standing").validate[PlayerState].get should be(PlayerState.Standing)
      JsString("DoubledDown").validate[PlayerState].get should be(PlayerState.DoubledDown)
      JsString("Busted").validate[PlayerState].get should be(PlayerState.Busted)
      JsString("Blackjack").validate[PlayerState].get should be(PlayerState.Blackjack)
      JsString("WON").validate[PlayerState].get should be(PlayerState.WON)
      JsString("LOST").validate[PlayerState].get should be(PlayerState.LOST)
      JsString("Betting").validate[PlayerState].get should be(PlayerState.Betting)
      JsString("Idle").validate[PlayerState].get should be(PlayerState.Idle)
      JsString("Split").validate[PlayerState].get should be(PlayerState.Split)
      JsString("Invalid").validate[PlayerState] shouldBe a[JsError]

      JsString("Idle").validate[DealerState].get should be(DealerState.Idle)
      JsString("Dealing").validate[DealerState].get should be(DealerState.Dealing)
      JsString("Bust").validate[DealerState].get should be(DealerState.Bust)
      JsString("Standing").validate[DealerState].get should be(DealerState.Standing)
      JsString("Invalid").validate[DealerState] shouldBe a[JsError]

      JsString("Initialized").validate[GameState].get should be(GameState.Initialized)
      JsString("Betting").validate[GameState].get should be(GameState.Betting)
      JsString("Started").validate[GameState].get should be(GameState.Started)
      JsString("Evaluated").validate[GameState].get should be(GameState.Evaluated)
      JsString("Invalid").validate[GameState] shouldBe a[JsError]
    }
  }

  "throw an exception when parsing invalid JSON" in {
    // Create a temporary test file with invalid JSON
    val tempFile = java.io.File.createTempFile("test", ".json")
    val writer = new java.io.PrintWriter(tempFile)
    try {
      writer.write("""{"invalid": "json", "missing": "required fields"}""")
    } finally {
      writer.close()
    }

    // Test that loading throws an exception
    val exception = intercept[Exception] {
      fileIO.load(tempFile.getAbsolutePath)
    }

    // Verify the exception message contains the expected error
    exception.getMessage should include("Error parsing JSON")

    // Clean up
    tempFile.delete()
  }
}
