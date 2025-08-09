package com.tfc.liarsbar.model.game;


import com.tfc.liarsbar.model.exceptions.NoSuchCardException;

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
}
