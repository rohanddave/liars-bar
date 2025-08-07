package model.game;

import java.util.Random;

public class RevolverImpl implements Revolver {
  private final int BARREL_SIZE = 6;
  private int BULLET_INDEX;
  private int currentIndex;

  public RevolverImpl() {
    this.currentIndex = 0;
    this.BULLET_INDEX = new Random().nextInt(this.BARREL_SIZE);
  }

  @Override
  public boolean shoot() {
    this.currentIndex = this.currentIndex + 1;
    return this.currentIndex == this.BULLET_INDEX;
  }

  @Override
  public void reset() {
    this.currentIndex = 0;
    this.BULLET_INDEX = new Random().nextInt(this.BARREL_SIZE);
  }

  @Override
  public int getCurrentIndex() {
    return this.currentIndex;
  }

  @Override
  public void forceElimination() {
    // Force a bullet shot to eliminate the player
    this.currentIndex = this.BULLET_INDEX;
  }
}
