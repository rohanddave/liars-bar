package model.game;

import java.util.List;

import model.exceptions.HandEmptyException;
import model.exceptions.HandFullException;

public class HandImpl implements Hand {
  private final List<Card> cards;
  private final int size;

  public HandImpl(List<Card> cards) {
    this.cards = cards;
    this.size = this.cards.size();
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
    if (this.cards.size() == this.size) {
      throw new HandFullException("Hand is full!");
    }

    this.cards.add(card);
  }

  @Override
  public void discard(Card card) {
    if (this.cards.size() == 0) {
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
