package model.playerComponent

import model.handComponent.HandInterface

enum PlayerState {
  case Playing, Standing, DoubledDown, Busted, Blackjack, WON, LOST, Betting, Idle, Split}

case class Player(
                   name: String,
                   hand: HandInterface = Hand(),
                   split_hand: List[HandInterface] = List(),
                   money: Int = 1000,
                   bet: Int = 0,
                   state: PlayerState = PlayerState.Idle
                 ) extends PlayerInterface {
  override def getName: String = name
  override def getHand: Hand = hand
  override def getSplitHand: List[HandInterface] = splitHand
  override def getMoney: Int = money
  override def getBet: Int = bet
  override def getState: PlayerState = state
}