package com.tfc.liarsbar.model.game;

import java.util.List;

public class ClaimImpl implements Claim {
  private final int count;
  private final Player player;
  private final List<Card> cards;
  private final Rank rank;
  private boolean isSettled;

  public ClaimImpl(int count, Player player, List<Card> cards, Rank rank) {
    this.cards = cards;
    this.count = count;
    this.player = player;
    this.rank = rank;
    this.isSettled = false;
  }

  public ClaimImpl(int count, Player player, List<Card> cards, Rank rank, boolean isSettled) {
    this.cards = cards;
    this.count = count;
    this.player = player;
    this.rank = rank;
    this.isSettled = isSettled;
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
  public boolean isSettled() { return this.isSettled; }

  @Override
  public void settle() { this.isSettled = true; }

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
