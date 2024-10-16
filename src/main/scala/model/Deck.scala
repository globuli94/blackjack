package model;

import scala.util.Random;
import scala.collection.mutable.Queue;

// handles logic for shuffling and drawing cards //

case class Deck(deck: Queue[Card] = Queue.empty) {
    private val suits = List("Hearts", "Diamonds", "Clubs", "Spades");
    private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace");

    // returns queue of freshly shuffled cards
    def shuffle: Queue[Card] = {
        val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)));
        val shuffledList = Random.shuffle(cardList);
        val queue = Queue(shuffledList*)
        return queue;
    }
    
    // returns card from deck, if no cards left
    def draw(): (Card, Deck) = {
        
        if(deck.length == 0) {
            val new_deck = shuffle
            val card = new_deck.dequeue()
            return (card, Deck(new_deck));
        }
        
        val card = deck.dequeue();
        return (card , Deck(deck));
    }
}