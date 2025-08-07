package model.game;

import java.util.List;

/**
 * Represents the Claim by a Player.
 */
public interface Claim {
  /**
   * Getter for the claimed count of suit.
   * @return an integer representing the claimed count of suit.
   */
  int getCount();

  /**
   * Getter for the referencing Player.
   * @return Player that made the bid.
   */
  Player getPlayer();

  List<Card> getCards();

  boolean isValidClaim();

  boolean isSettled();

  void settle();

  Rank getRank();
}
