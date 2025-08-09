package com.tfc.liarsbar.model.game;

import com.tfc.liarsbar.model.exceptions.HandEmptyException;

import java.util.List;

public class HandImpl implements Hand {
  private final List<Card> cards;

  public HandImpl(List<Card> cards) {
    this.cards = cards;
  }

  @Override
  public int getSize() {
    return this.cards.size();
  }

  @Override
  public Card getAt(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= this.cards.size()) throw new IndexOutOfBoundsException();

    return this.cards.get(index);
  }

  @Override
  public void add(Card card) {
    // Note: Removed arbitrary size limit since hand size should be dynamic in the game
    this.cards.add(card);
  }

  @Override
  public void discard(Card card) {
    if (this.cards.isEmpty()) {
      throw new HandEmptyException("Hand is empty!");
    }

    this.cards.remove(card);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Card card: this.cards) {
      sb.append(card.toString()).append("\t");
    }
    return sb.toString();
  }
}
