package model.modelComponent

enum PlayerState { case Playing, Standing, DoubledDown, Busted, Blackjack, WON, LOST, Betting, Idle, Split }

case class Player(
                   name: String,
                   hand: Hand = Hand(),
                   money: Int = 1000,
                   bet: Int = 0,
                   state: PlayerState = PlayerState.Idle
                 )