package util.fileIOComponent.XML

import model.ModelInterface
import model.modelComponent.GameState.{Evaluated, Initialized, Started}

import model.modelComponent.PlayerState.*
import model.modelComponent.{Card, Dealer, DealerState, Deck, Game, GameState, Hand, Player, PlayerState}
import util.FileIOInterface

import scala.xml.{Elem, Node, NodeSeq, PrettyPrinter, XML}

class FileIOXML extends FileIOInterface {

  // PLAYER STATE
  def playerStateFromString(state: String): PlayerState = state match {
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
  def dealerStateFromString(state: String): DealerState = state match {
    case "Idle" => DealerState.Idle
    case "Dealing" => DealerState.Dealing
    case "Bust" => DealerState.Bust
    case "Standing" => DealerState.Standing
    case _ => throw new IllegalArgumentException("Invalid DealerState")
  }

  // GAME STATE
  def gameStateFromString(state: String): GameState = state match {
    case "Initialized" => Initialized
    case "Betting" => GameState.Betting
    case "Started" => Started
    case "Evaluated" => Evaluated
    case _ => throw new IllegalArgumentException("Invalid GameState")
  }

  // CARD
  private def cardToXML(card: Card): Elem = {
    <card>
      <rank>{card.rank}</rank>
      <suit>{card.suit}</suit>
    </card>
  }

  private def cardFromXML(node: Node): Card = {
    val rank = (node \ "rank").text
    val suit = (node \ "suit").text
    Card(rank, suit)
  }

  // HAND
  private def handToXML(hand: Hand): Elem = {
    <cards>
      {hand.cards.map(cardToXML)}
    </cards>
  }

  private def handFromXML(node: Node): Hand = {
    val cardsNode = (node \ "cards").headOption.getOrElse(node)
    val cards = (cardsNode \ "card").map(cardFromXML).toList
    Hand(cards)
  }

  // PLAYER
  private def playerToXML(player: Player): Elem = {
    <player>
      <name>{player.name}</name>
      <hand>{handToXML(player.hand)}</hand>
      <money>{player.money}</money>
      <bet>{player.bet}</bet>
      <state>{player.state.toString}</state>
    </player>
  }

  private def playerFromXML(node: Node): Player = {
    val name = (node \ "name").text
    val hand = handFromXML((node \ "hand").head)
    val money = (node \ "money").text.toInt
    val bet = (node \ "bet").text.toInt
    val state = playerStateFromString((node \ "state").text)
    Player(name, hand, money, bet, state)
  }

  // DEALER
  private def dealerToXML(dealer: Dealer): Elem = {
    <dealer>
      <hand>{handToXML(dealer.hand)}</hand>
      <state>{dealer.state.toString}</state>
    </dealer>
  }

  private def dealerFromXML(node: Node): Dealer = {
    val dealerNode = (node \ "dealer").headOption.getOrElse(node)
    val hand = handFromXML((dealerNode \ "hand").head)
    val state = dealerStateFromString((dealerNode \ "state").text)
    Dealer(hand, state)
  }

  // DECK
  private def deckToXML(deck: Deck): Elem = {
    <cards>
      {deck.cards.map(cardToXML)}
    </cards>
  }

  private def deckFromXML(node: Node): Deck = {
    val cards = (node \ "cards" \ "card").map(cardFromXML).toList
    Deck(cards)
  }

  // GAME
  private def gameToXML(game: ModelInterface): Elem = {
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

  private def gameFromXML(node: Node): ModelInterface = {
    val idx = (node \ "current_idx").text.toInt
    val players = (node \ "players" \ "player").map(playerFromXML).toList
    val deck = deckFromXML((node \ "deck").head)
    val dealer = dealerFromXML((node \ "dealer").head)
    val state = gameStateFromString((node \ "state").text)
    Game(idx, players, deck, dealer, state)
  }

  override def load(path: String = "game.xml"): ModelInterface = {
    val source = XML.loadFile(path)
    gameFromXML(source)
  }

  override def save(game: ModelInterface, path: String = "game.xml"): Unit = {
    val xml = gameToXML(game)
    val prettyPrinter = new PrettyPrinter(120, 4)
    val prettyXml = prettyPrinter.format(xml)
    XML.save(path, XML.loadString(prettyXml), "UTF-8", xmlDecl = true, null)
  }
}