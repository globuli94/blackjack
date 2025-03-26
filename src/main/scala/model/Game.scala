package model

import model.DealerState.Standing
import model.PlayerState.{Betting, Playing}

enum GameState {
  case Initialized, Betting, Started, Evaluated
}

case class Game(current_idx: Int = 0, players: List[Player] = List.empty, deck: Deck = new Deck(), dealer: Dealer = new Dealer(), state: GameState = GameState.Initialized) {

  def createPlayer(name: String): Game = {
    if (players.length < 4) {
      this.copy(players = Player(name) :: players).evaluate
    } else {
      this
    }
  }

  def leavePlayer(name: String = ""): Game = {
    val idx = if (current_idx == players.length - 1) 0 else current_idx

    if(players.length == 1) {
      Game()
    } else if(name.trim.isEmpty) {
      copy(players = players.patch(current_idx, Nil, 1), current_idx = idx).evaluate
    } else {
      copy(players = players.filterNot(_.name == name), current_idx = idx).evaluate
    }
  }

  // performs the initial deal -> dealer 1 card, all players 2 cards, player state set to playing
  def deal: Game = {
    val shuffled_deck = deck.shuffle

    val (card, deck_after_dealer) = shuffled_deck.draw()
    val dealer_hand = Hand().addCard(card)

    val (updated_players, final_deck) = players.foldLeft((List.empty[Player], deck_after_dealer)) {
      case ((playersAcc, currentDeck), player) =>
        // Draw two cards for each player
        val (first_card, deck_after_first_draw) = currentDeck.draw()
        val (second_card, finalDeck) = deck_after_first_draw.draw()

        // Add cards to player's hand
        val updatedHand = player.hand.addCard(first_card).addCard(second_card)

        // Update player's state to playing
        val updatedPlayer =
          player.copy(
            hand = updatedHand,
            state = PlayerState.Playing
          )

        // Accumulate the updated player and deck for the next iteration
        (playersAcc :+ updatedPlayer, finalDeck)
    }

    copy(current_idx = 0, players = updated_players, deck = final_deck, dealer = Dealer(dealer_hand), state = GameState.Started)
  }

  // hits dealer if possible, else changes dealers state to bust or standing
  def hitDealer: Game = {
      val (card, new_deck) = deck.draw()
      val new_dealer = dealer.copy(hand = dealer.hand.addCard(card))
      copy(dealer = new_dealer, deck = new_deck).evaluate
  }

  // hits current player and sets player state to blackjack playing or busted, updates deck
  def hitPlayer: Game = {
    players.lift(current_idx) match {
    case Some(player) =>
      val (card, new_deck) = deck.draw()
      val new_player_hand = player.hand.addCard(card)

      val updated_players: List[Player] = players.map({
        p => {
          if(p == player) p.copy(
            hand = new_player_hand,
          ) else p
        }
      })

      copy(
        players = updated_players,
        deck = new_deck
      ).evaluate
    case None => this
    }
  }

  // stands current player = updates player state to standing
  def standPlayer: Game = {
    players.lift(current_idx) match {
      case Some(player) =>
        val updated_players: List[Player] = players.map({
          p => {
            if(p == player) p.copy(state = PlayerState.Standing) else p
          }
        })

        val next_idx = (current_idx + 1) % players.length
        copy(players = updated_players, current_idx = next_idx).evaluate

      case None => this
    }
  }

  // subtracts amount of money from player, updates money, bet and player state to playing
  def betPlayer(amount: Int): Game = {
    players.lift(current_idx) match {
      case Some(player) =>
        val new_balance = player.money - amount

        val updated_players: List[Player] = players.map({
          p => {
            if (p == player) p.copy(money = new_balance, bet = amount, state = PlayerState.Playing) else p
          }
        })
        copy(
          players = updated_players,
          current_idx = if(current_idx == players.length - 1) current_idx else current_idx + 1
        ).evaluate
      case None => this
    }
  }

  // checks if the bet is valid
  def isValidBet(amount: Int): Boolean = {
    players.lift(current_idx) match {
      case Some(player) =>
        amount <= player.money
      case None => false
    }
  }

  def doubleDownPlayer: Game = {
    players.lift(current_idx) match {
      case Some(player) =>
        val new_bet = player.bet * 2
        val new_balance = player.money - player.bet

        val (card, new_deck) = deck.draw()
        val new_player_hand = player.hand.addCard(card)

        val updated_players: List[Player] = players.map({
          p => {
            if (p == player) p.copy(hand = new_player_hand, money = new_balance, bet = new_bet, state = PlayerState.DoubledDown) else p
          }
        })
        copy(
          players = updated_players,
          current_idx = if(current_idx == players.length - 1) current_idx else current_idx + 1,
          deck = new_deck
        ).evaluate
      case None => this
    }
  }

  def startGame: Game = {
    val updated_players = players.map(
      player =>
        player.copy(state = PlayerState.Betting, hand = Hand())
    )

    copy(
      players = updated_players,
      state = GameState.Betting,
      current_idx = 0,
      dealer = Dealer()
    ).evaluate
  }

  def evaluate: Game = {
    // Evaluate player states
    val evaluated_players: List[Player] = players.map {
      case player if player.state == PlayerState.Standing => player
      case player if player.hand.isBust => player.copy(state = PlayerState.Busted)
      case player if player.hand.hasBlackjack => player.copy(state = PlayerState.Blackjack)
      case player => player
    }

    val evaluated_dealer: Dealer = dealer match {
      case d if d.hand.isBust => d.copy(state = DealerState.Bust)
      case d if d.hand.value >= 17 => d.copy(state = DealerState.Standing)
      case _ => dealer
    }

    val any_playing = evaluated_players.exists(_.state == PlayerState.Playing)
    val any_betting = evaluated_players.exists(_.state == PlayerState.Betting)

    state match {
      case GameState.Betting if !any_betting => deal.evaluate

      case GameState.Started if any_playing => {
        val current_player = evaluated_players(current_idx)
        val new_index =
          if(current_player.state == PlayerState.Blackjack || current_player.state == PlayerState.Busted) {
            if (current_idx == players.length - 1) current_idx else current_idx + 1
          } else {
            current_idx
          }

        copy(current_idx = new_index, players = evaluated_players)
      }

      case GameState.Started if !any_playing && evaluated_dealer.state != DealerState.Standing && evaluated_dealer.state != DealerState.Bust =>
        copy(dealer = evaluated_dealer).hitDealer.evaluate // Ensure evaluation continues after hitting dealer

      case GameState.Started if !any_playing && (evaluated_dealer.state == DealerState.Bust || evaluated_dealer.state == DealerState.Standing) =>
        val evaluated_players_bets = evaluated_players.map { player =>
          player.state match {
            case PlayerState.Blackjack if dealer.hand.hasBlackjack =>
              player.copy(bet = 0, state = PlayerState.LOST)
            case PlayerState.Blackjack =>
              player.copy(money = player.money + player.bet * 2, bet = 0, state = PlayerState.WON)
            case PlayerState.Standing | PlayerState.DoubledDown if dealer.hand.value >= player.hand.value && !dealer.hand.isBust =>
              player.copy(bet = 0, state = PlayerState.LOST)
            case PlayerState.Standing | PlayerState.DoubledDown =>
              player.copy(money = player.money + player.bet * 2, bet = 0, state = PlayerState.WON)
            case PlayerState.Busted =>
              player.copy(bet = 0, state = PlayerState.LOST)
            case _ => player
          }
        }
        copy(players = evaluated_players_bets, state = GameState.Evaluated, dealer = evaluated_dealer)

      case GameState.Evaluated => copy(state = GameState.Initialized)

      case _ => this
    }
  }


  def getPlayerOptions: List[String] = {
    val baseOptions = List("exit")

    val playerOpt: Option[Player] = players.lift(current_idx)

    val options = state match {
      case GameState.Initialized =>
        baseOptions ++ (if (players.nonEmpty) List("add <player>", "start") else List("add <player>"))

      case GameState.Betting =>
        baseOptions ++ List("bet <amount>", "leave")

      case GameState.Started =>
        playerOpt match {
          case Some(player) =>
            baseOptions ++ List("stand") ++
              (if (player.hand.canHit) List("hit") else Nil) ++
              (if (player.hand.canDoubleDown && player.money >= player.bet) List("double (down)") else Nil)
          // ++ (if (player.hand.canSplit) List("split") else Nil)  // Uncomment if needed
          case None => baseOptions
        }

      case GameState.Evaluated =>
        baseOptions ++ List("add <player>", "continue")

      case null => baseOptions
    }

    options
  }

  override def toString: String = {
    println("\u001b[H\u001b[2J") // Clear console

    // ASCII Art Title
    println(
      "         ____  __           __     _            __  \n" +
        "        / __ )/ /___ ______/ /__  (_)___ ______/ /__\n" +
        "       / __  / / __ `/ ___/ //_/ / / __ `/ ___/ //_/\n" +
        "      / /_/ / / /_/ / /__/ ,<   / / /_/ / /__/ ,<   \n" +
        "     /_____/_/\\__,_/\\___/_/|_|_/ /\\__,_/\\___/_/|_|  \n" +
        "                            /___/                    "
    )
    println("\n")

    val stringBuilder = new StringBuilder()

    // Dealer Box Centered
    val dealerHand = if (dealer.hand.hand.length == 1) f"[* *] ${dealer.hand.toString()}" else f" ${dealer.hand.toString()} "
    val dealerValue = f"Value: ${
      if (dealer.hand.isBust) "Busted"
      else if (dealer.hand.hasBlackjack) "Blackjack"
      else dealer.hand.value
    }"

    def centerText(text: String, width: Int): String = {
      val padding = (width - text.length) / 2
      " " * padding + text + " " * padding
    }
    val dealer_string = "---------------------- Dealer ------------------------\n"
    val boxWidth = dealer_string.length

    stringBuilder.append(s"${centerText(dealer_string, boxWidth)}\n")
    stringBuilder.append(s"${centerText(dealerHand, boxWidth)}\n")
    stringBuilder.append(s"${centerText(dealerValue, boxWidth)}\n\n")

    stringBuilder.append(s"State: ${state}\n")

    // Player Table Header
    stringBuilder.append("---------------------- Table -------------------------\n")

    val boxWidthPlayer = 35 // Adjusted width for the boxes
    val middle = boxWidthPlayer / 2 // Find the middle of the box width
    val reset = "\u001b[0m"
    val yellow = "\u001b[33m"
    val currentBoxTop = s"${yellow}+${"-" * (middle - 2)}*${"-" * (boxWidthPlayer - middle - 1)}+${reset}"
    val boxTop = s"+${"-" * (boxWidthPlayer - 2)}+"
    val boxBottom = boxTop

    // Formatting Player Boxes
    val playerBoxes = players.map { player =>
      val playerBoxTop = if(players.indexOf(player) == current_idx) currentBoxTop else boxTop

      val playerName  = s"Player: ${player.name.take(25)}"  // Truncate to 35 chars
      val playerBank  = s"Bank: $$${player.money.toString.take(25)}"  // Truncate to 35 chars
      val playerHand  = s"Hand: ${player.hand.toString.take(25)}"  // Truncate to 35 chars
      val playerBet   = s"Bet: $$${player.bet.toString.take(25)}"  // Truncate to 35 chars
      val playerValue = s"Value: ${player.hand.value.toString.take(25)}"  // Truncate to 35 chars
      val playerState = s"State: ${player.state.toString.take(25)}"  // Truncate to 35 chars

      // Format each line inside the box
      val nameLine  = f"| $playerName%-31s |"
      val bankLine  = f"| $playerBank%-31s |"
      val handLine  = f"| $playerHand%-31s |"
      val betLine   = f"| $playerBet%-31s |"
      val valueLine = f"| $playerValue%-31s |"
      val stateLine = f"| $playerState%-31s |"

      // Build the box for the player based on game state
      state match {
        case GameState.Initialized =>
          Seq(playerBoxTop, nameLine, bankLine, stateLine, boxBottom)
        case GameState.Betting =>
          Seq(playerBoxTop, nameLine, bankLine, betLine, stateLine, boxBottom)
        case GameState.Evaluated =>
          Seq(playerBoxTop, nameLine, bankLine, handLine, valueLine, stateLine, boxBottom)
        case _ =>
          Seq(playerBoxTop, nameLine, bankLine, handLine, valueLine, betLine, stateLine, boxBottom)
      }
    }

    // Combine player boxes side by side
    if (playerBoxes.nonEmpty) {
      val combinedBoxLines = playerBoxes.map(_.toArray).transpose.map(_.mkString(" "))
      combinedBoxLines.foreach(line => stringBuilder.append(line + "\n"))
    }


    stringBuilder.append(s"${reset}------------------------------------------------------\n")

    if (players.nonEmpty) {
      stringBuilder.append(s"Current Player: ${players(current_idx).name}, State: ${players(current_idx).state}\n")
    }

    stringBuilder.append("Options: ")
    getPlayerOptions.foreach(option => stringBuilder.append(s"\t$option"))
    stringBuilder.append("\n")

    stringBuilder.append("------------------------------------------------------\n")
    stringBuilder.toString()
  }


}