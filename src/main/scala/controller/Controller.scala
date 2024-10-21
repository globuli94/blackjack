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
        game = game.startGame
        notifyObservers(Event.Start)
    }

    def addPlayer(name:String): Unit = {
        if(game.state == GameState.Initialized) {
            game = game.createPlayer(name)
            notifyObservers(Event.AddPlayer)
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

    def bet(amount:String) = {
        try {
            amount.toInt
            if(game.isValidBet(amount.toInt)) {
                game = game.bet(amount.toInt)
                notifyObservers(Event.bet)
            } else {
                notifyObservers(Event.invalidBet)
            }
        } catch {
            case _: NumberFormatException => notifyObservers(Event.invalidBet)
        }   
    }

    def exit(): Unit = {
        sys.exit(0)
    }

    override def toString: String = {
        game.toString
    }
}