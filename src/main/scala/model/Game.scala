package model;
import scala.collection.mutable.Queue
import scala.compiletime.ops.string
import scala.collection.immutable.LazyList.cons
import scala.annotation.constructorOnly
import scala.collection.mutable

enum GameState { case Initialized, Betting, Started, Evaluated}

// Initialized -> 
// Betting ->
// Started ->
// Evaluated ->


case class Game(queue: mutable.Queue[Player] = mutable.Queue.empty, deck: Deck = new Deck(), dealer: Dealer = new Dealer(), state: GameState = GameState.Initialized) {

    def getPlayerOptions: List[String] = {
        var options: List[String]= List()
        options = "exit" :: options


        if(state == GameState.Initialized) {
            options = "add <player>" :: options
            if(queue.nonEmpty) {
                options = "start" :: options
            }
        } else if(state == GameState.Betting) {
            options = "exit" :: options
            options =  "bet <amount>" :: options
            options = "leave" :: options
        } else if (state == GameState.Started){
            options = "exit" :: options
            options = "stand" :: options
            val player = queue.head
        
            if (player.hand.canHit) options = "hit" :: options
            if (player.hand.canDoubleDown && player.money >= player.bet) options = "double (down)" :: options
            //if (player.hand.canSplit) options = "split" :: options
        } else if(state == GameState.Evaluated) {
            options = "add <player>" :: options
            options = "continue" :: options
        }
        
        return options
    }

    def startGame:Game = {
        val game_with_shuffled_cards = copy(deck = deck.shuffle, dealer = Dealer(), state = GameState.Betting)        
        
        val length = game_with_shuffled_cards.queue.length
        val this_queue = game_with_shuffled_cards.queue
        
        for(i <- 0 until length) {
            val player = game_with_shuffled_cards.queue.dequeue()
            game_with_shuffled_cards.queue.enqueue(player.copy(hand = Hand(), state = PlayerState.Betting))
        }

        game_with_shuffled_cards.evaluate
    }

    // game logic // everytime a move is made (hit, stand, bet, etc) the game is evaluated depending on the game state
    def evaluate: Game = {
        val any_playing = queue.exists(player => player.state == PlayerState.Playing)
        val any_betting = queue.exists(player => player.state == PlayerState.Betting)

        if (!any_betting && state == GameState.Betting) { // if betting is done -> game state started
            dealCards.copy(state = GameState.Started) 
        } else if (!any_playing && state == GameState.Started) { // if all players are standing or have blackjack -> evaluate the hands
            if (dealer.state != DealerState.Standing && dealer.state != DealerState.Bust) { // add cards to dealer according to rules
                hitDealer.evaluate
            } else {
                // All players have played, and dealer is standing/busted, evaluate each player
                val evaluatedQueue = queue.map { player =>
                    player.state match {
                        case PlayerState.Blackjack =>
                            if (dealer.hand.hasBlackjack) {
                                player.copy(bet = 0, state = PlayerState.LOST)
                            } else {
                                player.copy(money = player.money + player.bet + player.bet * 1.5, bet = 0, state = PlayerState.Blackjack)
                            }
                        case PlayerState.Standing =>
                            if (dealer.hand.value >= player.hand.value && !dealer.hand.isBust) {
                                player.copy(bet = 0, state = PlayerState.LOST)
                            } else {
                                player.copy(money = player.money + player.bet * 2, bet = 0, state = PlayerState.WON)
                            }
                        case PlayerState.Busted =>
                            player.copy(bet = 0, state = PlayerState.LOST)
                        case PlayerState.DoubledDown => 
                            if(dealer.hand.value > player.hand.value) {
                                player.copy(bet = 0, state = PlayerState.LOST)
                            } else if (dealer.hand.value == player.hand.value) {
                                player.copy(money = player.money + player.bet, bet = 0, state = PlayerState.Idle)
                            } else {
                                player.copy(money = player.money + player.bet *2, bet = 0, state = PlayerState.WON)
                            }
                        case _ => player
                    }
                }
                Game(queue = evaluatedQueue, dealer = dealer, state = GameState.Evaluated)
            }
        } else if (state == GameState.Evaluated) {
            this.copy(state = GameState.Initialized);
        } else {
            this
        }
    }

    def createPlayer(name:String): Game = {
        this.copy(queue = queue.enqueue(Player(name))).evaluate
    }

    def leavePlayer: Game = {
        val player = queue.dequeue()
        this.copy(queue = queue).evaluate
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

            if(second_hand.hasBlackjack) {
                game_with_shuffled_cards.queue.enqueue(player.copy(hand = second_hand, state = PlayerState.Blackjack))
            } else {
                game_with_shuffled_cards.queue.enqueue(player.copy(hand = second_hand, state = PlayerState.Playing))
            }
        )

        game_with_shuffled_cards.copy(deck = deck, dealer = Dealer(dealer_hand)).evaluate
        
    }

    def hitDealer: Game = {
        if(dealer.hand.value < 17) {
            val new_hand = dealer.hand.addCard(deck.draw())
            val new_dealer = dealer.copy(hand = new_hand)
            copy(dealer = new_dealer, deck = deck)
        } else if(dealer.hand.isBust) {
            copy(dealer = dealer.copy(state = DealerState.Bust))
        } else {
            copy(dealer = dealer.copy(state = DealerState.Standing))
        }
    }

    def hit: Game = {
        val player = queue.dequeue()
        val new_hand = player.hand.addCard(deck.draw())

        if(new_hand.isBust) {
            queue.enqueue(player.copy(hand = new_hand, state = PlayerState.Busted))
        } else if (new_hand.hasBlackjack) {
            queue.enqueue(player.copy(hand = new_hand, state = PlayerState.Blackjack))
        } else {
            if(player.state == PlayerState.DoubledDown) {
                queue.enqueue(player.copy(hand = new_hand , state = PlayerState.DoubledDown))
            } else {
                queue.prepend(player.copy(hand = new_hand , state = PlayerState.Playing))
            }
        }

        copy(queue = queue, deck = deck).evaluate
    }

    def stand: Game = {
        val player = queue.dequeue()

        if(player.hand.hasBlackjack) {
            queue.enqueue(player.copy(state = PlayerState.Blackjack))
        } else {
            queue.enqueue(player.copy(state = PlayerState.Standing))
        }
        copy(queue, deck).evaluate
    }

    def doubleDown: Game = {
        val player = queue.dequeue()
        val bet = player.bet * 2
        val money = player.money - player.bet

        this.copy(queue = queue.prepend(player.copy(bet = bet, money = money, state = PlayerState.DoubledDown))).hit
    }

    def split: Game = {
        val player = queue.dequeue()
        val hand = player.hand

        val split_hand_1 = Hand(List(hand.hand(0), deck.draw()))
        val split_hand_2 = Hand(List(hand.hand(1), deck.draw()))
        
        val hand_list = List(split_hand_1, split_hand_2)

        this.copy(queue = queue.prepend(player.copy(hand = Hand(), state = PlayerState.Split)))
    }

    def bet(amount: Int): Game = {
        val player = queue.dequeue()

        val bet = amount
        val new_money = player.money - bet

        queue.enqueue(player.copy(money = new_money, bet = bet, state = PlayerState.Playing))

        copy(queue, deck).evaluate
    }

    def isValidBet(amount: Int): Boolean = {
        amount <= queue.front.money
    }

    override def toString(): String = {
        // clear console
        println("\u001b[H\u001b[2J")
        println(
        "         ____  __           __     _            __  \n" +
        "        / __ )/ /___ ______/ /__  (_)___ ______/ /__\n" +
        "       / __  / / __ `/ ___/ //_/ / / __ `/ ___/ //_/\n" +
        "      / /_/ / / /_/ / /__/ ,<   / / /_/ / /__/ ,<   \n" +
        "     /_____/_/\\__,_/\\___/_/|_|_/ /\\__,_/\\___/_/|_|  \n" +
        "                            /___/                    "
        )
        println();
        println();

        val stringBuilder = new StringBuilder()

        stringBuilder.append("---------------------- Dealer ------------------------\n")
        
        
        val box_width = 39 // Adjusted width for the boxes
        val box_top = s"+${"-" * (box_width - 2)}+"
        val box_bottom = box_top

        val dealer_hand_line = 
            if(dealer.hand.hand.length == 1) {
                f"[* *] ${dealer.hand.toString()}"// Align left
            } else {
                f" ${dealer.hand.toString()} " // Align left
            }

        val dealer_value_line = f" Value: ${
            if(dealer.hand.isBust) {
                "Busted"
            } else if(dealer.hand.hasBlackjack) {
                "Blackjack"
            } else {
                dealer.hand.value
            }
            } " // Align left

        if(dealer.hand.hand.nonEmpty) {
            stringBuilder.append(            
                s"""
                    $dealer_hand_line\n
                    $dealer_value_line
                """)
        }

        // Separator for better visibility
        stringBuilder.append("\n")

        // Table Section
        stringBuilder.append("---------------------- Table ------------------------- \t Queue: \n")
        
        // Prepare Player Information in Box Format
        val playerBoxes = queue.map { player =>
            val playerName = s"Player: ${player.name}"
            val playerBank = "Bank: $ " + player.money
            val playerHand = s"Hand: ${player.hand.toString()}"
            val playerBet = "Bet: $ " + player.bet
            val playerValue = s"Value: ${player.hand.value}"
            val playerState = s"State: ${player.state}"

            // Format each line inside the box
            val nameLine = f"| $playerName%-35s |" // Align left
            val bankLine = f"| $playerBank%-35s |" // Align left
            val handLine = f"| $playerHand%-35s |" // Align left
            val betLine = f"| $playerBet%-35s |" 
            val valueLine = f"| $playerValue%-35s |" // Align left
            val stateLine = f"| $playerState%-35s |" // Align left

            if(state == GameState.Initialized) {
                s"""
                $box_top
                $nameLine
                $bankLine
                $stateLine
                $box_bottom
                """
            } else if(state == GameState.Betting) {
                s"""
                $box_top
                $nameLine
                $bankLine
                $betLine
                $stateLine
                $box_bottom
                """
            } else if(state == GameState.Evaluated) {
                s"""
                $box_top
                $nameLine
                $bankLine
                $handLine
                $valueLine
                $stateLine
                $box_bottom
                """
            } else {
                s"""
                $box_top
                $nameLine
                $bankLine
                $handLine
                $valueLine
                $betLine
                $stateLine
                $box_bottom
                """
            }
        }

            // Concatenate box lines into a complete box for this player



        // Combine all player boxes side by side
        val combinedBoxLines = playerBoxes.map(_.split("\n")).transpose.map(_.mkString("  "))

        // Append the combined boxes to the string builder
        combinedBoxLines.foreach(line => stringBuilder.append(line + "\n"))

        // Adding a bottom separator for aesthetics
        stringBuilder.append("------------------------------------------------------\n")

        stringBuilder.append("Options: ")
        getPlayerOptions.foreach(option => stringBuilder.append(s"\t$option"))
        stringBuilder.append("\n")
        
        stringBuilder.append("------------------------------------------------------\n")
        stringBuilder.toString()
    }
} 