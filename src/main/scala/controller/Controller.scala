package controller

import model.Game
import util.{Observer, Observable, Event}
import scala.annotation.constructorOnly

case class Controller(var game: Game) extends Observable {
    
    def initializeGame = {
        game = game.deal
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

    def exit(): Unit = {
        sys.exit(0)
    }

    override def toString: String = {
        game.toString
    }
}