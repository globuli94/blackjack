package controller

import model.{Game_v2, GameState}
import util.{Event, Observable, Observer}

case class Controller_v2(var game: Game_v2) extends Observable {

  def initializeGame(): Unit = {
    game = Game_v2()
    notifyObservers(Event.Start)
  }

  def startGame(): Unit = {
    if ((game.state == GameState.Initialized || game.state == GameState.Evaluated) && game.players.nonEmpty) {
      game = game.startGame
      notifyObservers(Event.Start)
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  def addPlayer(name: String): Unit = {
    if (game.state == GameState.Initialized || game.state == GameState.Evaluated) {
      game = game.createPlayer(name)
      notifyObservers(Event.AddPlayer)
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  def leavePlayer(): Unit = {
    if ((game.state == GameState.Betting || game.state == GameState.Initialized)) {
      if (game.players.length == 1) {
        exit();
      } else {
        game = game.leavePlayer()
        notifyObservers(Event.leavePlayer)
      }
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  def hitNextPlayer(): Unit = {
    val player = game.players(game.current_idx)
    if (player.hand.canHit && game.state == GameState.Started) {
      game = game.hitPlayer
      notifyObservers(Event.hitNextPlayer)
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  def standNextPlayer(): Unit = {
    if (game.state == GameState.Started) {
      game = game.standPlayer
      notifyObservers(Event.standNextPlayer)
    } else {
      notifyObservers(Event.invalidCommand)
    }

  }

  def doubleDown(): Unit = {
    val player = game.players(game.current_idx)

    if (game.state == GameState.Started && player.hand.canDoubleDown && player.bet <= player.money) {
      game = game.doubleDownPlayer
      notifyObservers(Event.doubleDown)
    } else {
      notifyObservers(Event.invalidBet)
    }
  }

  /*
  def split(): Unit = {
    val player = game.players(game.current_idx)
    if (game.state == GameState.Started && player.hand.canSplit && player.bet <= player.money) {
      game = game.split
      notifyObservers(Event.Split)
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }
   */

  def bet(amount: Array[String]): Unit = {
    if (game.state == GameState.Betting && amount.length == 2) {
      try {
        if (game.isValidBet(amount(1).toInt) && amount(1).toInt > 0) {
          game = game.betPlayer(amount(1).toInt)
          notifyObservers(Event.bet)
        } else {
          notifyObservers(Event.invalidBet)
        }
      } catch {
        case _: NumberFormatException => notifyObservers(Event.invalidCommand)
      }
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  def exit(): Unit = {
    sys.exit(0)
  }

  override def toString: String = {
    game.toString
  }
}
