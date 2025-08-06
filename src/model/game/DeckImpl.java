package model.game;

import java.util.ArrayList;
import java.util.Random;

public class DeckImpl implements Deck {
  private final ArrayList<Card> cards;
  private final int SIZE;
  private final Random random = new Random();
  // TODO: change the wild card here
  private final Rank wild = Rank.ACE;

  public DeckImpl(ArrayList<Card> cards) {
    this.cards = new ArrayList<>(cards);
    this.SIZE = this.cards.size();
  }

  // TODO: how is the deck built?
  public DeckImpl() {
    int normalNumberOfCardsPerRank = 6;
    int wildNumberOfCards = 2; 

    int numberOfRanks = Rank.values().length;

    this.SIZE = (numberOfRanks - 1) * normalNumberOfCardsPerRank + wildNumberOfCards;

    this.cards = new ArrayList<>(this.SIZE);

    for (Rank rank : Rank.values()) {
        System.out.println(rank);
        int limit = rank == this.wild ? wildNumberOfCards : normalNumberOfCardsPerRank;
        for (int i = 0; i < limit; i++) { 
          this.cards.add(new CardImpl(rank));
        }
    }
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
