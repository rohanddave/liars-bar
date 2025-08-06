package model.game;

/**
 * This interface represents a Player of the game Liars Bar.
 */
public interface Player {
  String getId();

  Claim claim();

  void challengeClaim(Claim claim);

  boolean shoot();

  boolean isAlive();

  Hand getHand();

  void setHand(Hand hand);
}
