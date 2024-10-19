package model

case class Card(rank: String, suit: String) {
    def value: Int = this.rank match {
        case "Ace" => 11
        case "King" | "Queen" | "Jack" => 10
        case _ => rank.toInt
    }

    override def toString(): String = {
        val suit_icon = this.suit.match {
            case "Hearts" => '\u2665'
            case "Diamonds" => '\u2666'
            case "Clubs" => '\u2663'
            case "Spades" => '\u2660'
        }
        val stringBuilder = new StringBuilder()
        stringBuilder.append(s"[$suit_icon $rank]")
        stringBuilder.toString()
    }
}