package blackjack.models.fileIO

import model.cardComponent.Card
import model.dealerComponent.{Dealer, DealerInterface, DealerState}
import model.deckComponent.{Deck, DeckInterface}
import model.gameComponent.{Game, GameInterface, GameState}
import model.handComponent.{Hand, HandInterface}
import model.playerComponent.{Player, PlayerInterface, PlayerState}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

trait FileIOTestBase extends AnyWordSpec with Matchers {
  // Common test data
  val testCard: Card = Card("A", "Spades")
  val testHand: HandInterface = Hand(List(testCard))
  val testPlayer: PlayerInterface = Player("TestPlayer", testHand,1000, 100, PlayerState.Playing)
  val testDealer: DealerInterface = Dealer(testHand, DealerState.Dealing)
  val testDeck: DeckInterface = Deck(List.fill(52)(testCard))
  val testGame: GameInterface = Game(0, List(testPlayer), testDeck, testDealer, GameState.Started)

  // Helper method to compare games (ignoring some fields if needed)
  def gamesAreSimilar(g1: GameInterface, g2: GameInterface): Boolean = {
    g1.getIndex == g2.getIndex &&
      g1.getPlayers.size == g2.getPlayers.size &&
      g1.getState == g2.getState
  }
}
