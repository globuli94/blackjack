package controller.controllerComponent

import model.gameComponent.{GameInterface, GameState}
import util.Event.invalidCommand
import util.{Event, Observable, Observer}

case class Controller(var game: GameInterface) extends ControllerInterface with Observable {

  override def getGame: GameInterface = game

  override def initializeGame(): Unit = {
    game = game.initialize
    notifyObservers(Event.Start)
  }

  override def startGame(): Unit = {
    if ((game.getState == GameState.Initialized || game.getState == GameState.Evaluated) && game.getPlayers.nonEmpty) {
      game = game.startGame
      notifyObservers(Event.Start)
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def addPlayer(name: String): Unit = {
    if (game.getState == GameState.Initialized || game.getState == GameState.Evaluated) {
      if(game.getPlayers.exists(_.getName == name)) {
        notifyObservers(Event.errPlayerNameExists)
      } else {
        game = game.createPlayer(name)
        notifyObservers(Event.AddPlayer)
      }
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def leavePlayer(): Unit = {
    if(game.getPlayers.nonEmpty) {
        game = game.leavePlayer()
        notifyObservers(Event.leavePlayer)
    } else {
      notifyObservers(invalidCommand)
    }
  }

  override def hitNextPlayer(): Unit = {
    val player = game.getPlayers(game.getIndex)
    if (player.getHand.canHit && game.getState == GameState.Started) {
      game = game.hitPlayer
      notifyObservers(Event.hitNextPlayer)
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def standNextPlayer(): Unit = {
    if (game.getState == GameState.Started) {
      game = game.standPlayer
      notifyObservers(Event.standNextPlayer)
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def doubleDown(): Unit = {
    val player = game.getPlayers(game.getIndex)

    if (game.getState == GameState.Started && player.getHand.canDoubleDown && player.getBet <= player.getMoney) {
      game = game.doubleDownPlayer
      notifyObservers(Event.doubleDown)
    } else {
      notifyObservers(Event.invalidBet)
    }
  }

  override def bet(amount: String): Unit = {
    if (game.getState == GameState.Betting) {
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

  override def exit(): Unit = {
    sys.exit(0)
  }

  override def toString: String = {
    game.toString
  }
}
