package model.game;

/**
 * This class represents a deck of n cards.
 */
public interface Deck {
  // TODO: two constructors, 1. with n parameter and randomly generates and 2. a list of cards

  Card drawRandomCard();

  Card[] drawNRandomCards(int n);
}
