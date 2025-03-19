package controller

import model.{Game, GameState}
import util.Event.invalidCommand
import util.{Event, Observable, Observer}

case class Controller(var game: Game) extends Observable {

  def initializeGame(): Unit = {
    game = Game()
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
      if(game.players.exists(_.name == name)) {
        notifyObservers(Event.errPlayerNameExists)
      } else {
        game = game.createPlayer(name)
        notifyObservers(Event.AddPlayer)
      }
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  def leavePlayer(): Unit = {
    if(game.players.nonEmpty) {
        game = game.leavePlayer()
        notifyObservers(Event.leavePlayer)
    } else {
      notifyObservers(invalidCommand)
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

  def bet(amount: String): Unit = {
    if (game.state == GameState.Betting) {
      try {
        if (game.isValidBet(amount.toInt) && amount.toInt > 0) {
          game = game.betPlayer(amount.toInt)
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
