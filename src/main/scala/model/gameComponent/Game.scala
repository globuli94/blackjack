package model.gameComponent

import com.google.inject.Inject

import model.cardComponent.CardInterface
import model.dealerComponent.*
import model.deckComponent.*
import model.handComponent.*
import model.playerComponent.*
import model.playerComponent.PlayerState.{LOST, WON}

enum GameState {
  case Initialized, Betting, Started, Evaluated
}

case class Game @Inject() (
                 current_idx: Int = 0,
                 players: List[PlayerInterface] = List.empty,
                 deck: DeckInterface = Deck(),
                 dealer: DealerInterface = Dealer(),
                 state: GameState = GameState.Initialized) extends GameInterface {

  override def getIndex: Int = current_idx
  override def getPlayers: List[PlayerInterface] = players
  override def getDeck: DeckInterface = deck
  override def getState: GameState = state
  override def getDealer: DealerInterface = dealer
  
  override def createPlayer(name: String): GameInterface = {
    if (players.length < 4) {
      this.copy(players = Player(name) :: players).evaluate
    } else {
      this
    }
  }

  override def initialize: GameInterface = Game()

  override def leavePlayer(name: String = ""): GameInterface = {
    val idx = if (current_idx == players.length - 1) 0 else current_idx

    if(players.length == 1) {
      Game()
    } else if(name.trim.isEmpty) {
      copy(players = players.patch(current_idx, Nil, 1), current_idx = idx).evaluate
    } else {
      copy(players = players.filterNot(_.getName == name), current_idx = idx).evaluate
    }
  }

  // performs the initial deal -> dealer 1 card, all players 2 cards, player state set to playing
  override def deal: GameInterface = {
    val shuffled_deck = deck.shuffle

    val (card: CardInterface, deck_after_dealer: DeckInterface) = shuffled_deck.draw
    val dealer_hand = Hand().addCard(card)

    val (updated_players, final_deck) = players.foldLeft((List.empty[PlayerInterface], deck_after_dealer)) {
      case ((playersAcc, currentDeck), player) =>
        // Draw two cards for each player
        val (first_card, deck_after_first_draw) = currentDeck.draw
        val (second_card, finalDeck) = deck_after_first_draw.draw

        // Add cards to player's hand
        val updatedHand = player.getHand.addCard(first_card).addCard(second_card)

        // Update player's state to playing
        val updatedPlayer =
          Player(player.getName, updatedHand, player.getSplitHand, player.getMoney, player.getBet, PlayerState.Playing)

        // Accumulate the updated player and deck for the next iteration
        (playersAcc :+ updatedPlayer, finalDeck)
    }

    copy(current_idx = 0, players = updated_players, deck = final_deck, dealer = Dealer(dealer_hand), state = GameState.Started)
  }

  // hits dealer if possible, else changes dealers state to bust or standing
  override def hitDealer: GameInterface = {
      val (card: CardInterface, new_deck: DeckInterface) = deck.draw
      val new_dealer: DealerInterface = Dealer(dealer.getHand.addCard(card), dealer.getState)
      copy(dealer = new_dealer, deck = new_deck).evaluate
  }

  // hits current player and sets player state to blackjack playing or busted, updates deck
  override def hitPlayer: GameInterface = {
    players.lift(current_idx) match {
    case Some(player) =>
      val (card, new_deck) = deck.draw
      val new_player_hand = player.getHand.addCard(card)

      val updated_players: List[PlayerInterface] = players.map({
        p => {
          if(p == player)
            Player(p.getName, new_player_hand, p.getSplitHand, p.getMoney, p.getBet, p.getState)
          else p
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
  override def standPlayer: GameInterface = {
    players.lift(current_idx) match {
      case Some(player) =>
        val updated_players: List[PlayerInterface] = players.map({
          p => {
            if(p == player)
              Player(p.getName, p.getHand, p.getSplitHand, p.getMoney, p.getBet, PlayerState.Standing)
            else p
          }
        })

        val next_idx = (current_idx + 1) % players.length
        copy(players = updated_players, current_idx = next_idx).evaluate

      case None => this
    }
  }

  // subtracts amount of money from player, updates money, bet and player state to playing
  override def betPlayer(amount: Int): GameInterface = {
    players.lift(current_idx) match {
      case Some(player) =>
        val new_balance = player.getMoney - amount

        val updated_players: List[PlayerInterface] = players.map({
          p => {
            if (p == player)
              Player(p.getName, p.getHand, p.getSplitHand, new_balance, amount, PlayerState.Playing)
            else p
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
  override def isValidBet(amount: Int): Boolean = {
    players.lift(current_idx) match {
      case Some(player) =>
        amount <= player.getMoney
      case None => false
    }
  }

  override def doubleDownPlayer: GameInterface = {
    players.lift(current_idx) match {
      case Some(player) =>
        val new_bet = player.getBet * 2
        val new_balance = player.getMoney - player.getBet

        val (card, new_deck) = deck.draw
        val new_player_hand = player.getHand.addCard(card)

        val updated_players: List[PlayerInterface] = players.map({
          p => {
            if (p == player)
              Player(p.getName, new_player_hand, p.getSplitHand, new_balance, new_bet, PlayerState.DoubledDown)
            else p
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

  override def startGame: GameInterface = {
    val updated_players = players.map(
      p =>
        Player(p.getName, Hand(), p.getSplitHand, p.getMoney, p.getBet, PlayerState.Betting)
    )

    copy(
      players = updated_players,
      state = GameState.Betting,
      current_idx = 0,
      dealer = Dealer()
    ).evaluate
  }

  override def evaluate: GameInterface = {
    // Evaluate player states
    val evaluated_players: List[PlayerInterface] = players.map {
      case player if player.getState == PlayerState.Standing => player
      case player if player.getHand.isBust =>
        Player(player.getName, player.getHand, player.getSplitHand, player.getMoney, player.getBet, PlayerState.Busted)
      case player if player.getHand.hasBlackjack =>
        Player(player.getName, player.getHand, player.getSplitHand, player.getMoney, player.getBet, PlayerState.Blackjack)
      case player => player
    }

    val evaluated_dealer: DealerInterface = dealer match {
      case d if d.getHand.isBust =>
        Dealer(d.getHand, DealerState.Bust)
      case d if d.getHand.getHandValue >= 17 =>
        Dealer(d.getHand, DealerState.Standing)
      case _ => dealer
    }

    val any_playing = evaluated_players.exists(_.getState == PlayerState.Playing)
    val any_betting = evaluated_players.exists(_.getState == PlayerState.Betting)

    state match {
      case GameState.Betting if !any_betting => deal.evaluate

      case GameState.Started if any_playing => {
        val current_player = evaluated_players(current_idx)
        val new_index =
          if(current_player.getState == PlayerState.Blackjack || current_player.getState == PlayerState.Busted) {
            current_idx + 1
          } else {
            current_idx
          }

        copy(current_idx = new_index, players = evaluated_players)
      }

      case GameState.Started if !any_playing && evaluated_dealer.getState != DealerState.Standing && evaluated_dealer.getState != DealerState.Bust =>
        copy(dealer = evaluated_dealer).hitDealer.evaluate // Ensure evaluation continues after hitting dealer

      case GameState.Started
        if !any_playing && (evaluated_dealer.getState == DealerState.Bust || evaluated_dealer.getState == DealerState.Standing) =>
          val evaluated_players_bets = evaluated_players.map { p =>
            p.getState match {
              case PlayerState.Blackjack if dealer.getHand.hasBlackjack =>
                Player(p.getName, p.getHand, p.getSplitHand, p.getMoney, 0, LOST)
              case PlayerState.Blackjack =>
                val money_after_winning = p.getMoney + p.getBet * 2
                Player(p.getName, p.getHand, p.getSplitHand, money_after_winning, 0, WON)
              case PlayerState.Standing | PlayerState.DoubledDown
                if dealer.getHand.getHandValue >= p.getHand.getHandValue && !dealer.getHand.isBust =>
                  Player(p.getName, p.getHand, p.getSplitHand, p.getMoney, 0, LOST)
              case PlayerState.Standing | PlayerState.DoubledDown =>
                val money_after_winning = p.getMoney + p.getBet * 2
                Player(p.getName, p.getHand, p.getSplitHand, money_after_winning, 0, WON)
              case PlayerState.Busted =>
                Player(p.getName, p.getHand, p.getSplitHand, p.getMoney, 0, LOST)
              case _ => p
            }
          }
          copy(players = evaluated_players_bets, state = GameState.Evaluated, dealer = evaluated_dealer)

      case GameState.Evaluated => copy(state = GameState.Initialized)

      case _ => this
    }
  }


  override def getPlayerOptions: List[String] = {
    val baseOptions = List("exit")

    val playerOpt: Option[PlayerInterface] = players.lift(current_idx)

    val options = state match {
      case GameState.Initialized =>
        baseOptions ++ (if (players.nonEmpty) List("add <player>", "start") else List("add <player>"))

      case GameState.Betting =>
        baseOptions ++ List("bet <amount>", "leave")

      case GameState.Started =>
        playerOpt match {
          case Some(player) =>
            baseOptions ++ List("stand") ++
              (if (player.getHand.canHit) List("hit") else Nil) ++
              (if (player.getHand.canDoubleDown && player.getMoney >= player.getBet) List("double (down)") else Nil)
          // ++ (if (player.hand.canSplit) List("split") else Nil)  // Uncomment if needed
          case None => baseOptions
        }

      case GameState.Evaluated =>
        baseOptions ++ List("add <player>", "continue")
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
    val dealerHand = if (dealer.getHand.length == 1) f"[* *] ${dealer.getHand.toString}" else f" ${dealer.getHand.toString} "
    val dealerValue = f"${
      if (dealer.getHand.isBust) "Busted"
      else if (dealer.getHand.hasBlackjack) "Blackjack"
      else f"Value: ${dealer.getHand.getHandValue}"
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

      val playerName  = s"Player: ${player.getName.take(25)}"  // Truncate to 35 chars
      val playerBank  = s"Bank: $$${player.getMoney.toString.take(25)}"  // Truncate to 35 chars
      val playerHand  = s"Hand: ${player.getHand.toString.take(25)}"  // Truncate to 35 chars
      val playerBet   = s"Bet: $$${player.getBet.toString.take(25)}"  // Truncate to 35 chars
      val playerValue = s"Value: ${player.getHand.getHandValue.toString.take(25)}"  // Truncate to 35 chars
      val playerState = s"State: ${player.getState.toString.take(25)}"  // Truncate to 35 chars

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
      stringBuilder.append(s"Current Player: ${players(current_idx).getName}, State: ${players(current_idx).getState}\n")
    }

    stringBuilder.append("Options: ")
    getPlayerOptions.foreach(option => stringBuilder.append(s"\t$option"))
    stringBuilder.append("\n")

    stringBuilder.append("------------------------------------------------------\n")
    stringBuilder.toString()
  }
}