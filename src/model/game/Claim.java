package model.game;

/**
 * Represents the Claim by a Player.
 */
public interface Claim {
  /**
   * Getter for the claimed number of suit.
   * @return an integer representing the claimed number of suit.
   */
  int getNumber();

  /**
   * Getter for Suit.
   * @return Suit.
   */
  Suit getSuit();

  /**
   * Getter for the referencing Player.
   * @return Player that made the bid.
   */
  Player getPlayer();

  /**
   * Setter for the referencing Player
   * @param player Player that this bid is made by.
   */
  void setPlayer(Player player);
}
