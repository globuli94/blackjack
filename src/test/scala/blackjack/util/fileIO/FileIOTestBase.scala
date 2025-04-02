package blackjack.util.fileIO

import model.modelComponent.{Card, Dealer, DealerState, Deck, Game, GameState, Hand, Player, PlayerState}
import model.ModelInterface

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, JsSuccess, Json}
import util.fileIOComponent.JSON.FileIOJSON

import scala.io.{BufferedSource, Source}

trait FileIOTestBase extends AnyWordSpec with Matchers {
  // Helper method to compare games (ignoring some fields if needed)
  def gamesAreSimilar(g1: ModelInterface, g2: ModelInterface): Boolean = {
    g1.getIndex == g2.getIndex &&
      g1.getPlayers.size == g2.getPlayers.size &&
      g1.getState == g2.getState
  }
}
