package model.game;

import java.util.List;

public class ClaimImpl implements Claim {
  private final int count;
  private final Player player;
  private final List<Card> cards;
  private final Rank rank;

  public ClaimImpl(int count, Player player, List<Card> cards, Rank rank) {
    this.cards = cards;
    this.count = count;
    this.player = player;
    this.rank = rank;
  }

  @Override
  public int getCount() {
    return this.count;
  }

  @Override
  public Player getPlayer() {
    return this.player;
  }

  @Override
  public List<Card> getCards() {
    return this.cards;
  }

  @Override
  public boolean isValidClaim() {
    int realCount = 0;
    for (Card card : this.cards) {
      if (card.getRank() == this.rank) {
        realCount++;
      }
    }

    return realCount == this.count;
  }

  @Override
  public Rank getRank() {
    return this.rank;
  }
}
