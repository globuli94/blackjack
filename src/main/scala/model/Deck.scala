package model;

import scala.collection.mutable
import scala.util.Random
import scala.collection.mutable.Queue;

// handles logic for shuffling and drawing cards //

case class Deck(deck: mutable.Queue[Card] = Queue.empty) {
    private val suits = List("Hearts", "Diamonds", "Clubs", "Spades");
    private val ranks = List("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");

    // returns queue of freshly shuffled cards
    def shuffle: Deck = {
        val cardList = ranks.flatMap(rank => suits.map(suit => Card(rank, suit)));
        val shuffledList = Random.shuffle(cardList);
        val queue = mutable.Queue(shuffledList*)
        return Deck(queue);
    }

    // returns card from deck, if no cards left
    def draw(): Card = {
        this.deck.dequeue()
    }
}