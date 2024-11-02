package model

enum PlayerState { case Playing, Standing, DoubledDown, Busted, Blackjack, WON, LOST, Betting, Idle}

case class Player(name: String, hand: Hand = Hand(),  money: Double = 1000, bet: Double = 0, state: PlayerState = PlayerState.Idle)