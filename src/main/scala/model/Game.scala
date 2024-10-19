package model;
import scala.collection.mutable.Queue;
import scala.compiletime.ops.string
import scala.collection.immutable.LazyList.cons
import scala.annotation.constructorOnly


case class Game(queue: Queue[Player] = Queue.empty, deck: Deck = new Deck(), dealer: Dealer = new Dealer()) {

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
        game_with_shuffled_cards.dealInitialCards
    }
    
    def createPlayer(name:String): Game = {
        this.copy(queue = queue.enqueue(Player(name)))
    }

    def dealInitialCards: Game = {
        val dealer_hand = Hand().addCard(deck.draw())

        queue.foreach( player =>
            val player = queue.dequeue()
            val first_card = deck.draw()
            val second_card = deck.draw()
            
            val first_hand = player.hand.addCard(first_card)
            val second_hand = first_hand.addCard(second_card)

            queue.enqueue(player.copy(hand = second_hand))
        )
        copy(queue, deck, Dealer(dealer_hand))
    }

    def hit: Game = {
        val player = queue.dequeue()
        val new_hand = player.hand.addCard(deck.draw())
        
        queue.enqueue(player.copy(hand = new_hand))

        copy(queue = queue, deck = deck)
    }

    def stand: Game = {
        // dequeue and enqueue player
        queue.enqueue(queue.dequeue())
        copy(queue, deck)
    }

    override def toString(): String = {
        // clear console
        println("\u001b[H\u001b[2J")

        val stringBuilder = new StringBuilder()

        stringBuilder.append("---------------------- Dealer ------------------\n")
        
        
        val box_width = 26 // Adjusted width for the boxes
        val box_top = s"+${"-" * (box_width - 2)}+"
        val box_bottom = box_top
        val name_line = f"| ${"Dealer"}%-22s |" // Align left

        var dealer_hand_line: String = ""
        if(dealer.hand.hand.length == 1) {
            dealer_hand_line = f"|[* *] ${dealer.hand.toString()}%-18s|" // Align left
        } else {
            dealer_hand_line = f"| ${dealer.hand.toString()}%-22s |" // Align left
        }
        
        val dealer_value = s"Value: ${dealer.hand.value}"

        stringBuilder.append(            
            s"""
                $box_top
                $name_line
                $dealer_hand_line
                $box_bottom
            """)

        // Separator for better visibility
        stringBuilder.append("\n")

        // Table Section
        stringBuilder.append("---------------------- Table -------------------- \t Queue: \n")
        
        // Prepare Player Information in Box Format
        val playerBoxes = queue.filter(_.name != "Dealer").map { player =>
            val playerName = s"Player: ${player.name}"
            val playerBank = s"Bank: ${player.money}"
            val playerHand = s"Hand: ${player.hand.toString()}"
            val playerValue = s"Value: ${player.hand.value}"

            // Format each line inside the box
            val nameLine = f"| $playerName%-22s |" // Align left
            val bankLine = f"| $playerBank%-22s |" // Align left
            val handLine = f"| $playerHand%-22s |" // Align left
            val valueLine = f"| $playerValue%-22s |" // Align left

            // Concatenate box lines into a complete box for this player
            s"""
            $box_top
            $nameLine
            $bankLine
            $handLine
            $valueLine
            $box_bottom
            """
        }

        // Combine all player boxes side by side
        val combinedBoxLines = playerBoxes.map(_.split("\n")).transpose.map(_.mkString("  "))

        // Append the combined boxes to the string builder
        combinedBoxLines.foreach(line => stringBuilder.append(line + "\n"))

        // Adding a bottom separator for aesthetics
        stringBuilder.append("-------------------------------------------------\n")

        stringBuilder.append("Options: ")
        getPlayerOptions.foreach(option => stringBuilder.append(s"\t$option"))
        stringBuilder.append("\n")
        
        stringBuilder.append("-------------------------------------------------\n")
        stringBuilder.toString()
    }
} 