package model.fileIOComponent.JSON

import scala.collection.immutable.Queue
import model.cardComponent.{Card, CardInterface}
import model.deckComponent.*
import model.fileIOComponent.FileIOInterface
import model.gameComponent.{Game, GameInterface}
import model.handComponent.HandState.{Play, Stand}
import model.handComponent.{Hand, HandInterface, HandState}
import model.playerComponent.PlayerInterface
import play.api.libs.json.*
import play.api.libs.functional.syntax.*

import java.io.PrintWriter


abstract class FileIOJSON extends FileIOInterface {

  // HAND STATE
  implicit val handStateReads: Reads[HandState] = Reads[HandState] {
    case JsString("Play") => JsSuccess(Play)
    case JsString("Stand") => JsSuccess(Stand)
    case _ => JsError("Invalid HandState")
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
    "hand" -> hand.getCards,           // Serializes the list of cards
    "state" -> hand.getState.toString  // Serializes the HandState as a string
  )
  implicit val handReads: Reads[HandInterface] = (
    (JsPath \ "hand").read[List[CardInterface]] and // Reads the list of cards
      (JsPath \ "state").read[HandState]             // Reads the HandState (Play, Stand, etc.)
    ) ((cards, state) => Hand(cards, state))          // Create a Hand object from the parsed values

  /*
  implicit val deckWrites: Writes[DeckInterface] = (deck: DeckInterface) => Json.obj(
    "deck" -> deck.getDeck
  )
  implicit val deckReads: Reads[DeckInterface] = (
    (JsPath \ "deck").read[Queue[CardInterface]]
    )((deck) => Deck(deck = deck))


  override def load: GameInterface = Game()

  override def save(hand: HandInterface): Unit = {
    val jsonString = Json.stringify(Json.toJson(hand))
    new PrintWriter("game.json") {
      write(jsonString); close()
    }
  }
  */
}