package model;

import scala.util.Random;
import scala.collection.mutable.Queue;

// handles logic for shuffling and drawing cards //

case class Deck(deck: Queue[Card] = Queue.empty) {
    private val suits = List("Hearts", "Diamonds", "Clubs", "Spades");
    private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace");

    // returns queue of freshly shuffled cards
    def shuffle: Deck = {
        val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)));
        val shuffledList = Random.shuffle(cardList);
        val queue = Queue(shuffledList*)
        return Deck(queue);
    }
    
    // returns card from deck, if no cards left
    def draw(): (Card, Deck) = {
        val card = this.deck.dequeue()
        (card, Deck(deck))
    }
}