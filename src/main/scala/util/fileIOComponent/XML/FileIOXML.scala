package util.fileIOComponent.XML

import model.cardComponent.{Card, CardInterface}
import model.dealerComponent.*
import model.deckComponent.*
import model.gameComponent.GameState.{Evaluated, Initialized, Started}
import model.gameComponent.{Game, GameInterface, GameState}
import model.handComponent.{Hand, HandInterface}
import model.playerComponent.*
import model.playerComponent.PlayerState.{Betting, Blackjack, Busted, DoubledDown, Idle, LOST, Playing, Split, Standing, WON}
import util.fileIOComponent.FileIOInterface

import scala.xml.{Elem, Node, NodeSeq, PrettyPrinter, XML}

class FileIOXML extends FileIOInterface {

  // PLAYER STATE
  private def playerStateFromString(state: String): PlayerState = state match {
    case "Playing" => Playing
    case "Standing" => Standing
    case "DoubledDown" => DoubledDown
    case "Busted" => Busted
    case "Blackjack" => Blackjack
    case "WON" => WON
    case "LOST" => LOST
    case "Betting" => Betting
    case "Idle" => Idle
    case "Split" => Split
    case _ => throw new IllegalArgumentException("Invalid PlayerState")
  }

  // DEALER STATE
  private def dealerStateFromString(state: String): DealerState = state match {
    case "Idle" => DealerState.Idle
    case "Dealing" => DealerState.Dealing
    case "Bust" => DealerState.Bust
    case "Standing" => DealerState.Standing
    case _ => throw new IllegalArgumentException("Invalid DealerState")
  }

  // GAME STATE
  private def gameStateFromString(state: String): GameState = state match {
    case "Initialized" => Initialized
    case "Betting" => GameState.Betting
    case "Started" => Started
    case "Evaluated" => Evaluated
    case _ => throw new IllegalArgumentException("Invalid GameState")
  }

  // CARD
  private def cardToXML(card: CardInterface): Elem = {
    <card>
      <rank>{card.getRank}</rank>
      <suit>{card.getSuit}</suit>
    </card>
  }

  private def cardFromXML(node: Node): CardInterface = {
    val rank = (node \ "rank").text
    val suit = (node \ "suit").text
    Card(rank, suit)
  }

  // HAND
  private def handToXML(hand: HandInterface): Elem = {
    <cards>
      {hand.getCards.map(cardToXML)}
    </cards>
  }

  private def handFromXML(node: Node): HandInterface = {
    val cardsNode = (node \ "cards").headOption.getOrElse(node)
    val cards = (cardsNode \ "card").map(cardFromXML).toList
    Hand(cards)
  }

  // PLAYER
  private def playerToXML(player: PlayerInterface): Elem = {
    <player>
      <name>{player.getName}</name>
      <hand>{handToXML(player.getHand)}</hand>
      <money>{player.getMoney}</money>
      <bet>{player.getBet}</bet>
      <state>{player.getState.toString}</state>
    </player>
  }

  private def playerFromXML(node: Node): PlayerInterface = {
    val name = (node \ "name").text
    val hand = handFromXML((node \ "hand").head)
    val splitHands = (node \ "split_hands" \ "hand").map(handFromXML).toList
    val money = (node \ "money").text.toInt
    val bet = (node \ "bet").text.toInt
    val state = playerStateFromString((node \ "state").text)
    Player(name, hand, money, bet, state)
  }

  // DEALER
  private def dealerToXML(dealer: DealerInterface): Elem = {
    <dealer>
      <hand>{handToXML(dealer.getHand)}</hand>
      <state>{dealer.getState.toString}</state>
    </dealer>
  }

  private def dealerFromXML(node: Node): DealerInterface = {
    val dealerNode = (node \ "dealer").headOption.getOrElse(node)
    val hand = handFromXML((dealerNode \ "hand").head)
    val state = dealerStateFromString((dealerNode \ "state").text)
    Dealer(hand, state)
  }

  // DECK
  private def deckToXML(deck: DeckInterface): Elem = {
    <cards>
      {deck.getCards.map(cardToXML)}
    </cards>
  }

  private def deckFromXML(node: Node): DeckInterface = {
    val cards = (node \ "cards" \ "card").map(cardFromXML).toList
    Deck(cards)
  }

  // GAME
  private def gameToXML(game: GameInterface): Elem = {
    <game>
      <current_idx>{game.getIndex}</current_idx>
      <players>
        {game.getPlayers.map(playerToXML)}
      </players>
      <deck>{deckToXML(game.getDeck)}</deck>
      <dealer>{dealerToXML(game.getDealer)}</dealer>
      <state>{game.getState.toString}</state>
    </game>
  }

  private def gameFromXML(node: Node): GameInterface = {
    val idx = (node \ "current_idx").text.toInt
    val players = (node \ "players" \ "player").map(playerFromXML).toList
    val deck = deckFromXML((node \ "deck").head)
    val dealer = dealerFromXML((node \ "dealer").head)
    val state = gameStateFromString((node \ "state").text)
    Game(idx, players, deck, dealer, state)
  }

  override def load: GameInterface = {
    val source = XML.loadFile("game.xml")
    gameFromXML(source)
  }

  override def save(game: GameInterface): Unit = {
    val xml = gameToXML(game)
    val prettyPrinter = new PrettyPrinter(120, 4)
    val prettyXml = prettyPrinter.format(xml)
    XML.save("game.xml", XML.loadString(prettyXml), "UTF-8", xmlDecl = true, null)
  }
}