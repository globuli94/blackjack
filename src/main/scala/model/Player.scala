package model

case class Player(name: String, hand: Hand = Hand(), money: Int = 1000) {
    override def toString(): String = {
        val stringBuilder = new StringBuilder()
        stringBuilder.append(s"\n Player $name \t Bank: $money\n")
        stringBuilder.append(hand.toString())
        stringBuilder.append("-------------------------------")
        stringBuilder.toString()
    }
}