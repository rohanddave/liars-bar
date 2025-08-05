package model.game;

import java.util.ArrayList;
import java.util.Random;

public class DeckImpl implements Deck {
  private ArrayList<Card> cards;
  private int SIZE;
  private Random random;

  public DeckImpl(ArrayList<Card> cards) {
    this.cards = new ArrayList<>(cards);
    this.SIZE = this.cards.size();
    this.random = new Random();
  }

  @Override
  public Card drawRandomCard() {
    if (cards.isEmpty()) {
      throw new IllegalStateException("Cannot draw from empty deck");
    }

    int randomIndex = random.nextInt(cards.size());
    return cards.remove(randomIndex);
  }

  @Override
  public Card[] drawNRandomCards(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("Number of cards to draw cannot be negative");
    }

    if (n > cards.size()) {
      throw new IllegalStateException("Cannot draw " + n + " cards from deck with only " + cards.size() + " cards");
    }

    Card[] drawnCards = new Card[n];

    for (int i = 0; i < n; i++) {
      int randomIndex = random.nextInt(cards.size());
      drawnCards[i] = cards.remove(randomIndex);
    }

    return drawnCards;
  }

  /**
   * Gets the current number of cards remaining in the deck
   * @return Number of cards left
   */
  public int size() {
    return cards.size();
  }

  /**
   * Checks if the deck is empty
   * @return true if no cards remain
   */
  public boolean isEmpty() {
    return cards.isEmpty();
  }
}
