package controller

import model.Game
import util.{Observer, Observable, Event}

case class Controller(var game: Game) extends Observable {
    
    def start = {
        game = game.startGame
        notifyObservers(Event.Start)
    }

    def addPlayer(name:String): Unit = {
        game = game.createPlayer(name)
        notifyObservers(Event.AddPlayer)
    }

    def exit(): Unit = {
        sys.exit(0)
    }

    override def toString: String = {
        game.toString
    }
}