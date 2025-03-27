package model.dealerComponent

import model.DealerState
import model.handComponent.Hand

enum DealerState { case Idle, Dealing, Bust, Standing }

case class Dealer(hand: Hand = new Hand(), state:DealerState = DealerState.Idle) extends DealerInterface {
  def getHand: Hand = hand
  def getState: DealerState = state
}