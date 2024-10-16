package model

case class Card(rank: String, suit: String) {
    def value: Int = this.rank match {
        case "A" => 11
        case "K" | "Q" | "J" => 10
        case _ => rank.toInt
    }
}