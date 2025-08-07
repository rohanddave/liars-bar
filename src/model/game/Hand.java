package model.game;

import model.exceptions.NoSuchCardException;
import java.util.List;

/**
 * This class represents a hand of n cards.
 */
public interface Hand {
  int getSize();

  Card getAt(int index) throws IndexOutOfBoundsException;

  /**
   * Adds a card to the hand.
   * @param card card to be added to the hand.
   */
  void add(Card card);

  /**
   * Discards a card from the hand.
   * @param card the card to be discarded.
   */
  void discard(Card card) throws NoSuchCardException;

  /**
   * Gets all cards in the hand.
   * @return list of cards in the hand
   */
  List<Card> getCards();
}
