package model.game;

public class RoundImpl implements Round {
  private final Rank RANK;

  public RoundImpl(Rank rank) {
    this.RANK = rank;
  }

  @Override
  public Rank getRank() {
    return this.RANK;
  }
}
