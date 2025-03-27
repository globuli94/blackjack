package model.dealerComponent

import model.DealerState
import model.handComponent.{Hand, HandInterface}

enum DealerState { case Idle, Dealing, Bust, Standing }

case class Dealer(hand: HandInterface = Hand(), state:DealerState = DealerState.Idle) extends DealerInterface {
  def getHand: Hand = hand
  def getState: DealerState = state
}