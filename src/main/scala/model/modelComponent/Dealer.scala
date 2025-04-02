package model.modelComponent

enum DealerState { case Idle, Dealing, Bust, Standing }

case class Dealer(hand: Hand = Hand(), state:DealerState = DealerState.Idle)