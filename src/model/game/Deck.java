package model.game;

import java.util.List;

/**
 * This class represents a deck of n cards.
 */
public interface Deck {
  Card drawRandomCard();

  List<Card> drawNRandomCards(int n);
}
