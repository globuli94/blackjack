package controller

import model.Game
import util.{Observer, Observable, Event}
import scala.annotation.constructorOnly
import model.GameState

case class Controller(var game: Game) extends Observable {
    
    def initializeGame = {
        game = Game()
        notifyObservers(Event.Start)
    }

    def startGame = {
        if((game.state == GameState.Initialized || game.state == GameState.Evaluated) && !game.queue.isEmpty) {
            game = game.startGame
            notifyObservers(Event.Start)
        } else {
            notifyObservers(Event.invalidCommand)
        }
    }

    def addPlayer(name:String): Unit = {
        if(game.state == GameState.Initialized || game.state == GameState.Evaluated) {
            game = game.createPlayer(name)
            notifyObservers(Event.AddPlayer)
        } else {
            notifyObservers(Event.invalidCommand)
        }
    }

    def leavePlayer = {
        if((game.state == GameState.Betting || game.state == GameState.Initialized)) {
            if(game.queue.length == 1) {
                exit();
            } else {
                game = game.leavePlayer
                notifyObservers(Event.leavePlayer)
            }
        } else {
            notifyObservers(Event.invalidCommand)
        }
    }

    def hitNextPlayer = {
        if(game.queue.head.hand.canHit && game.state == GameState.Started) {
            game = game.hit
            notifyObservers(Event.hitNextPlayer)
        } else {
            notifyObservers(Event.invalidCommand)
        }
    }

    def standNextPlayer = {
        if(game.state == GameState.Started) {
            game = game.stand
            notifyObservers(Event.standNextPlayer)
        } else {
            notifyObservers(Event.invalidCommand)
        }

    }

    def doubleDown = {
        val player = game.queue.head
        
        if(game.state == GameState.Started && player.hand.canDoubleDown && player.bet <= player.money ) {
            game = game.doubleDown
            notifyObservers(Event.doubleDown)
        } else {
            notifyObservers(Event.invalidBet)
        }
    }

    def bet(amount: Array[String]) = {
        if(game.state == GameState.Betting && amount.length == 2) {
            try {
                if(game.isValidBet(amount(1).toInt) && amount(1).toInt > 0) {
                    game = game.bet(amount(1).toInt)
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