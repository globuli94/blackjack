package util.fileIOComponent.JSON

import model.cardComponent.{Card, CardInterface}
import model.dealerComponent.*
import model.deckComponent.*
import model.gameComponent.GameState.{Evaluated, Initialized, Started}
import model.gameComponent.{Game, GameInterface, GameState}
import model.handComponent.{Hand, HandInterface}
import model.playerComponent.*
import model.playerComponent.PlayerState.{Betting, Blackjack, Busted, DoubledDown, Idle, LOST, Playing, Split, Standing, WON}
import play.api.libs.json.{JsError, JsPath, JsString, JsSuccess, JsValue, Json, Reads, Writes}
import play.api.libs.functional.syntax.*
import util.fileIOComponent.FileIOInterface

import java.io.PrintWriter
import scala.io.Source


class FileIOJSON extends FileIOInterface {

  // PLAYER STATE
  implicit val playerStateReads: Reads[PlayerState] = Reads[PlayerState] {
    case JsString("Playing") => JsSuccess(Playing)
    case JsString("Standing") => JsSuccess(Standing)
    case JsString("DoubledDown") => JsSuccess(DoubledDown)
    case JsString("Busted") => JsSuccess(Busted)
    case JsString("Blackjack") => JsSuccess(Blackjack)
    case JsString("WON") => JsSuccess(WON)
    case JsString("LOST") => JsSuccess(LOST)
    case JsString("Betting") => JsSuccess(Betting)
    case JsString("Idle") => JsSuccess(Idle)
    case JsString("Split") => JsSuccess(Split)
    case _ => JsError("Invalid PlayerState")
  }

  // DEALER STATE
  implicit val dealerStateReads: Reads[DealerState] = Reads[DealerState] {
    case JsString("Idle") => JsSuccess(DealerState.Idle)
    case JsString("Dealing") => JsSuccess(DealerState.Dealing)
    case JsString("Bust") => JsSuccess(DealerState.Bust)
    case JsString("Standing") => JsSuccess(DealerState.Standing)
    case _ => JsError("Invalid PlayerState")
  }

  // GAME STATE
  implicit val gameStateReads: Reads[GameState] = Reads[GameState] {
    case JsString("Initialized") => JsSuccess(Initialized)
    case JsString("Betting") => JsSuccess(GameState.Betting)
    case JsString("Started") => JsSuccess(Started)
    case JsString("Evaluated") => JsSuccess(Evaluated)
    case _ => JsError("Invalid PlayerState")
  }


  // CARD
  implicit val cardWrites: Writes[CardInterface] = (card: CardInterface) => Json.obj(
    "rank" -> card.getRank,
    "suit" -> card.getSuit
  )
  implicit val cardReads: Reads[CardInterface] = (
    (JsPath \ "rank").read[String] and
      (JsPath \ "suit").read[String]
    ) ((rank, suit) => Card(rank, suit))

  // HAND
  implicit val handWrites: Writes[HandInterface] = (hand: HandInterface) => Json.obj(
    "cards" -> hand.getCards,           // Serializes the list of cards
  )
  implicit val handReads: Reads[HandInterface] = (json: JsValue) => {
    (json \ "cards").validate[List[CardInterface]].map(cards => Hand(cards))
  }        // Create a Hand object from the parsed values

  // PLAYER
  implicit val playerWrites: Writes[PlayerInterface] = (player: PlayerInterface) => Json.obj(
    "name" -> player.getName,
    "hand" -> player.getHand,
    "money" -> player.getMoney,
    "bet" -> player.getBet,
    "state" -> player.getState.toString
  )
  implicit val playerReads: Reads[PlayerInterface] = (
    (JsPath \ "name").read[String] and // Reads the list of cards
      (JsPath \ "hand").read[HandInterface] and
      (JsPath \ "money").read[Int] and
      (JsPath \ "bet").read[Int] and
      (JsPath \ "state").read[PlayerState]
    // Reads the HandState (Play, Stand, etc.)
    ) ((name, hand, money, bet, state) => Player(name, hand, money, bet, state))

  // DEALER
  implicit val dealerWrites: Writes[DealerInterface] = (dealer: DealerInterface) => Json.obj(
    "hand" -> dealer.getHand,
    "state" -> dealer.getState.toString
  )
  implicit val dealerReads: Reads[DealerInterface] = (
    (JsPath \ "hand").read[HandInterface] and // Reads the list of cards
      (JsPath \ "state").read[DealerState]
    // Reads the HandState (Play, Stand, etc.)
    )((hand, state) => Dealer(hand, state))

  // DECK
  // DECK
  implicit val deckWrites: Writes[DeckInterface] = (deck: DeckInterface) => Json.obj(
    "cards" -> Json.toJson(deck.getCards)
  )

  implicit val deckReads: Reads[DeckInterface] = (json: JsValue) => {
    (json \ "cards").validate[List[CardInterface]].map(cards => Deck(cards))
  }

  // GAME
  implicit val gameWrites: Writes[GameInterface] = (game: GameInterface) => Json.obj(
    "current_idx" -> game.getIndex,
    "players" -> game.getPlayers,
    "deck" -> game.getDeck,
    "dealer" -> game.getDealer,
    "state" -> game.getState.toString
  )

  implicit val gameReads: Reads[GameInterface] = (
    (JsPath \ "current_idx").read[Int] and // Reads the list of cards
      (JsPath \ "players").read[List[PlayerInterface]] and
      (JsPath \ "deck").read[DeckInterface] and
      (JsPath \ "dealer").read[DealerInterface] and
      (JsPath \ "state").read[GameState]
    // Reads the HandState (Play, Stand, etc.)
    )((idx, players, deck, dealer, state) => Game(idx, players, deck, dealer, state))

  override def load(path: String = "game.json"): GameInterface = {
    val source = Source.fromFile(path) // open source

    try {
      // get json
      val json = Json.parse(source.getLines.mkString)

      // try convert to game using validate
      json.validate[GameInterface] match {
        case JsSuccess(game, _) => game
        case JsError(errors) =>
          throw new Exception("Error parsing JSON: " + errors.toString)
      }
    } finally {
      source.close() // close source
    }
  }

  override def save(game: GameInterface, path: String = "game.json"): Unit = {
    val jsonString = Json.stringify(Json.toJson(game))
    new PrintWriter(path) {
      write(jsonString);
      close()
    }
  }
}