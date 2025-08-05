package model.game;

public class CardImpl implements Card {
  private Suit suit;
  private Rank rank;

  public CardImpl(Suit suit, Rank rank) {
    this.suit = suit;
    this.rank = rank;
  }

  @Override
  public Suit getSuit() {
    return this.suit;
  }

  @Override
  public Rank getRank() {
    return this.rank;
  }
}
