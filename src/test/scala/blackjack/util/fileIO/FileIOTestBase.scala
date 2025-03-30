package blackjack.util.fileIO

import model.cardComponent.Card
import model.dealerComponent.{Dealer, DealerInterface, DealerState}
import model.deckComponent.{Deck, DeckInterface}
import model.gameComponent.{Game, GameInterface, GameState}
import model.handComponent.{Hand, HandInterface}
import model.playerComponent.{Player, PlayerInterface, PlayerState}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, JsSuccess, Json}
import util.fileIOComponent.JSON.FileIOJSON

import scala.io.{BufferedSource, Source}

trait FileIOTestBase extends AnyWordSpec with Matchers {
  // Helper method to compare games (ignoring some fields if needed)
  def gamesAreSimilar(g1: GameInterface, g2: GameInterface): Boolean = {
    g1.getIndex == g2.getIndex &&
      g1.getPlayers.size == g2.getPlayers.size &&
      g1.getState == g2.getState
  }
}
