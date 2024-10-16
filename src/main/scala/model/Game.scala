package model;
import scala.collection.mutable.Queue;


case class Game(queue: Queue[Player] = Queue.empty, deck: Deck = new Deck()) {

    def createPlayer(name:String): Game = {
        return Game(queue.enqueue(Player(name)))
    }

}