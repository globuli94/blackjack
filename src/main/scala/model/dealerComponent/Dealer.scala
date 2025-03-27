package model.dealerComponent

import model.handComponent.{Hand, HandInterface}

enum DealerState { case Idle, Dealing, Bust, Standing }

case class Dealer(hand: HandInterface = Hand(), state:DealerState = DealerState.Idle) extends DealerInterface {
  override def getHand: HandInterface = hand
  override def getState: DealerState = state
}