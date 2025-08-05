package model.game;

import java.util.List;

import model.exceptions.HandEmptyException;
import model.exceptions.HandFullException;

public class HandImpl implements Hand {
  private List<Card> cards;
  private int size;

  public HandImpl(List<Card> cards) {
    this.cards = cards;
    this.size = this.cards.size();
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
}
