package model

case class Player(name: String, hand: Hand = Hand(), money: Int = 1000)