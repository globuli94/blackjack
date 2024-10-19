package model;
import scala.collection.mutable.Queue;
import scala.compiletime.ops.string


case class Game(queue: Queue[Player] = Queue.empty, deck: Deck = new Deck()) {

    def getPlayerOptions: List[String] = {
        val player = queue.head
        var options = List("Stand")

        if (player.hand.canHit) options = "Hit" :: options
        if (player.hand.canDoubleDown) options = "Double Down" :: options
        if (player.hand.canSplit) options = "Split" :: options

        return options
    }
    
    def startGame: Game = {
        val game_with_shuffled_cards = copy(deck = deck.shuffle)
        game_with_shuffled_cards.dealInitialCards.addDealer
    }

    def addDealer: Game = {
        Game(queue.enqueue(Player("Dealer")))
    }
    
    def createPlayer(name:String): Game = {
        Game(queue.enqueue(Player(name)))
    }

    def dealInitialCards: Game = {
        queue.foreach( player =>
            val deck_after_first_draw = deck.draw()
            val hand_with_first_card = player.hand.addCard(deck_after_first_draw._1)

            val deck_after_second_draw = deck_after_first_draw._2
            val hand_with_second_card = hand_with_first_card.addCard(deck_after_second_draw._1)
        )
        Game(queue, deck);
    }

    def hit: Game = {
        val player = queue.dequeue()
        val new_hand = player.hand.addCard(deck.draw())
        
        queue.enqueue(player.copy(hand = new_hand))

        Game(queue, deck)
    }

    def stand: Game = {
        // dequeue and enqueue player
        queue.enqueue(queue.dequeue())
        this.copy(queue, deck)
    }

    override def toString(): String = {
        val stringBuilder = new StringBuilder()
        stringBuilder.append("---------------------- Table ----------------------\n")
        queue.foreach(player => {
            stringBuilder.append(player.toString())
        })
        stringBuilder.toString()
    }
} 