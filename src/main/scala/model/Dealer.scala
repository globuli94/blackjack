package model

enum DealerState { case Idle, Dealing, Bust, Standing }

case class Dealer(hand: Hand = new Hand(), state:DealerState = DealerState.Idle)