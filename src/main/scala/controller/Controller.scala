package controller

import model.Game
import util.{Observer, Observable, Event}
import scala.annotation.constructorOnly

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
        game = game.createPlayer(name)
        notifyObservers(Event.AddPlayer)
    }

    def hitNextPlayer = {
        if(game.queue.head.hand.canHit) {
            game = game.hit


            notifyObservers(Event.hitNextPlayer)
        } else {
            notifyObservers(Event.invalidCommand)
        }
    }

    def standNextPlayer = {
        game = game.stand
        notifyObservers(Event.standNextPlayer)
    }

    def bet(amount:Int) = {
        game = game.bet(amount)
        notifyObservers(Event.bet)
    }

    def exit(): Unit = {
        sys.exit(0)
    }

    override def toString: String = {
        game.toString
    }
}