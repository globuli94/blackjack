package controller.controllerComponent

import com.google.inject.Inject
import controller.ControllerInterface
import model.ModelInterface
import model.modelComponent.GameState
import util.Event.*
import util.{Event, FileIOInterface, Observable}

case class Controller @Inject (var game: ModelInterface, fileIO: FileIOInterface) extends ControllerInterface with Observable {

  override def getGame: ModelInterface = game

  override def saveGame(): Unit = {
    fileIO.save(game)
    notifyObservers(Event.save)
  }

  override def loadGame(): Unit = {
    game = fileIO.load()
    notifyObservers(Event.load)
  }

  override def initializeGame(): Unit = {
    game = game.initialize
    notifyObservers(Event.start)
    saveGame()
  }

  override def startGame(): Unit = {
    if ((game.getState == GameState.Initialized || game.getState == GameState.Evaluated) && game.getPlayers.nonEmpty) {
      game = game.startGame
      notifyObservers(Event.start)
      saveGame()
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def addPlayer(name: String): Unit = {
    if (game.getState == GameState.Initialized || game.getState == GameState.Evaluated) {
      if(game.getPlayers.exists(_.name == name)) {
        notifyObservers(Event.errPlayerNameExists)
      } else {
        game = game.createPlayer(name)
        notifyObservers(Event.addPlayer)
        saveGame()
      }
    } else {
      notifyObservers(Event.invalidCommand)
    }

  }

  override def leavePlayer(): Unit = {
    if(game.getPlayers.nonEmpty) {
        game = game.leavePlayer()
        notifyObservers(Event.leavePlayer)
        saveGame()
    } else {
      notifyObservers(invalidCommand)
    }
  }

  override def hitPlayer(): Unit = {
    val player = game.getPlayers(game.getIndex)
    if (player.hand.canHit && game.getState == GameState.Started) {
      game = game.hitPlayer
      notifyObservers(Event.hitNextPlayer)
      saveGame()
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def standPlayer(): Unit = {
    if (game.getState == GameState.Started) {
      game = game.standPlayer
      notifyObservers(Event.standNextPlayer)
      saveGame()
    } else {
      notifyObservers(Event.invalidCommand)
    }

  }

  override def doubleDown(): Unit = {
    val player = game.getPlayers(game.getIndex)

    if (game.getState == GameState.Started && player.hand.canDoubleDown && player.bet <= player.money) {
      game = game.doubleDownPlayer
      notifyObservers(Event.doubleDown)
      saveGame()
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
          saveGame()
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
    saveGame()
    sys.exit(0)
  }

  override def toString: String = {
    game.toString
  }
}
