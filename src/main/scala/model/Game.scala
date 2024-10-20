package model;
import scala.collection.mutable.Queue;
import scala.compiletime.ops.string
import scala.collection.immutable.LazyList.cons
import scala.annotation.constructorOnly

enum GameState { case Initialized, Betting, Started}


case class Game(queue: Queue[Player] = Queue.empty, deck: Deck = new Deck(), dealer: Dealer = new Dealer(), state: GameState = GameState.Initialized) {

    def getPlayerOptions: List[String] = {
        
        var options: List[String]= List()

        if(state == GameState.Initialized) {
            options = "add <player>" :: options
            options = "start round" :: options
        } else if(state == GameState.Betting) {
            options =  "bet <amount>" :: options
            options = "leave table" :: options
        } else if (state == GameState.Started){
            options = "Stand" :: options
            val player = queue.head
        
            if (player.hand.canHit) options = "Hit" :: options
            if (player.hand.canDoubleDown) options = "Double Down" :: options
            if (player.hand.canSplit) options = "Split" :: options
        }
        
        return options
    }

    def finishDealer = {
        println(deck)
        println(deck.deck.length)
    }

    def startGame:Game = {
        val game_with_shuffled_cards = copy(deck = deck.shuffle, state = GameState.Betting)        
        
        val length = game_with_shuffled_cards.queue.length
        val this_queue = game_with_shuffled_cards.queue
        
        for(i <- 0 until length) {
            val player = game_with_shuffled_cards.queue.dequeue()
            game_with_shuffled_cards.queue.enqueue(player.copy(state = PlayerState.Betting))
        }

        game_with_shuffled_cards.evaluate
    }

    def evaluate: Game = {
        val any_playing = queue.exists(player => player.state == PlayerState.Playing)
        
        val any_betting = queue.exists(player => player.state == PlayerState.Betting)

        if(!any_betting && state == GameState.Betting) {

            dealCards.copy(state = GameState.Started)

        } else if(!any_playing && state == GameState.Started) {
            
            finishDealer
            
            println("EVALUATING ROUND")

            val length = queue.length

            for(i <- 0 until length) {
                val player = queue.dequeue()

                player.state match {
                    case PlayerState.Blackjack =>
                        val player_money = player.money + player.bet + player.bet * 1.5
                        queue.enqueue(player.copy(money = player_money, hand = Hand(), bet = 0, state = PlayerState.WON))
                    case PlayerState.Standing =>
                        var player_money:Double = 0
                        var player_state: PlayerState = PlayerState.LOST
                        
                        if(dealer.hand.value > player.hand.value) {
                            player_money = player.money - player.bet
                        } else {
                            player_money = player.money + player.bet * 2
                            player_state = PlayerState.WON
                        }
                        
                        queue.enqueue(player.copy(money = player_money, hand = Hand(), bet = 0, state = player_state))
                    case PlayerState.Busted =>
                        val player_money = player.money - player.bet

                        queue.enqueue(player.copy(money = player_money, hand = Hand(), bet = 0, state = PlayerState.LOST))
                    case _ =>
                }
            }
            Game(queue = queue, deck = new Deck(), dealer = new Dealer(), state = GameState.Initialized)
        } else {
            this
        }        
    }
    

    def createPlayer(name:String): Game = {
        this.copy(queue = queue.enqueue(Player(name)))
    }

    def dealCards: Game = {
        val game_with_shuffled_cards = copy(deck = deck.shuffle, state = GameState.Started)        

        val dealer_hand = Hand().addCard(deck.draw())

        game_with_shuffled_cards.queue.foreach( player =>
            val player = queue.dequeue()
            val first_card = deck.draw()
            val second_card = deck.draw()
            
            val first_hand = player.hand.addCard(first_card)
            val second_hand = first_hand.addCard(second_card)

            game_with_shuffled_cards.queue.enqueue(player.copy(hand = second_hand, state = PlayerState.Playing))
        )

        game_with_shuffled_cards.copy(deck = deck, dealer = Dealer(dealer_hand))
        
    }

    def hit: Game = {
        val player = queue.dequeue()
        println(deck)

        val new_hand = player.hand.addCard(deck.draw())

        var state: PlayerState = PlayerState.Playing

        if(new_hand.isBust) {
            state = PlayerState.Busted
            queue.enqueue(player.copy(hand = new_hand, state = state))
        } else if (new_hand.hasBlackjack) {
            state = PlayerState.Blackjack
            queue.enqueue(player.copy(hand = new_hand, state = state))
        } else {
            val new_player = player.copy(hand = new_hand , state = state)
            queue.prepend(new_player)
        }

        copy(queue = queue, deck = deck).evaluate
    }

    def stand: Game = {
        val player = queue.dequeue()
        queue.enqueue(player.copy(state = PlayerState.Standing))
        copy(queue, deck).evaluate
    }

    def bet(amount: Int): Game = {
        val player = queue.dequeue()

        val bet = amount
        val new_money = player.money - bet

        queue.enqueue(player.copy(money = new_money, bet = bet, state = PlayerState.Playing))

        copy(queue, deck).evaluate
    }









    override def toString(): String = {
        // clear console
        println("\u001b[H\u001b[2J")

        val stringBuilder = new StringBuilder()

        stringBuilder.append("---------------------- Dealer ------------------\n")
        
        
        val box_width = 30 // Adjusted width for the boxes
        val box_top = s"+${"-" * (box_width - 2)}+"
        val box_bottom = box_top
        val name_line = f"| ${"Dealer"}%-26s |" // Align left

        var dealer_hand_line: String = ""
        if(dealer.hand.hand.length == 1) {
            dealer_hand_line = f"| [* *] ${dealer.hand.toString()}%-22s|" // Align left
        } else {
            dealer_hand_line = f"| Hand: ${dealer.hand.toString()}%-22s |" // Align left
        }
        
        val dealer_value_line = f"| Value: ${dealer.hand.value}%-22s|" // Align left

        stringBuilder.append(            
            s"""
                $box_top
                $name_line
                $dealer_hand_line
                $dealer_value_line
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
            val playerState = s"State: ${player.state}"

            // Format each line inside the box
            val nameLine = f"| $playerName%-26s |" // Align left
            val bankLine = f"| $playerBank%-26s |" // Align left
            val handLine = f"| $playerHand%-26s |" // Align left
            val valueLine = f"| $playerValue%-26s |" // Align left
            val stateLine = f"| $playerState%-26s |" // Align left

            // Concatenate box lines into a complete box for this player
            s"""
            $box_top
            $nameLine
            $bankLine
            $handLine
            $valueLine
            $stateLine
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