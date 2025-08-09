package model.game;

public class ClaimImpl implements Claim {
  private final int count;
  private final Player player;

  public ClaimImpl(int count, Player player) {
    this.count = count;
    this.player = player;
  }

  @Override
  public int getCount() {
    return this.count;
  }

  @Override
  public Player getPlayer() {
    return this.player;
  }
}
