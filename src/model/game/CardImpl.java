package model.game;

public class CardImpl implements Card {
  private final Rank rank;

  public CardImpl(Rank rank) {
    this.rank = rank;
  }

  @Override
  public Rank getRank() {
    return this.rank;
  }

  @Override
  public String toString() {
    return rank.toString();
  }
}
