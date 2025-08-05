package model.game;

/**
 * This class represents a playing card.
 */
public interface Card {
  /**
   * Getter for suit of the card.
   * @return Suit of the card.
   */
  Suit getSuit();

  /**
   * Getter for the rank of the card.
   * @return Rank of the card.
   */
  Rank getRank();
}
