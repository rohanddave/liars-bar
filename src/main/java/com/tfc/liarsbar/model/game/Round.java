package com.tfc.liarsbar.model.game;

import com.tfc.liarsbar.model.exceptions.NoSuchCardException;

import java.util.List;

/**
 * Class represents a single round of Liars Bar.
 * 
 * A round focuses on a specific rank (Aces, Kings, Queens, or Jacks).
 * Players take turns playing cards and making claims about the current rank.
 * The round ends when all cards of the current rank have been played or claimed.
 */
public interface Round {
  
  /**
   * Gets the rank for this round (what players must claim to play)
   * @return The rank for this round
   */
  Rank getRank();
  
  /**
   * Starts a new round with the given players
   * @param players The players participating in this round
   */
  void startRound(List<Player> players);
  
  /**
   * Player makes a claim about cards they are playing
   * @param player The player making the claim
   * @param count Number of cards claimed
   * @param cards The actual cards being played
   * @param claimedRank The rank being claimed (should match round rank)
   * @throws NoSuchCardException if player doesn't have the cards
   */
  void claim(Player player, int count, List<Card> cards, Rank claimedRank) throws NoSuchCardException;
  
  /**
   * Challenges the previous player's claim
   * @param player The player making the challenge
   * @return The player who must spin the revolver (challenger if wrong, claimer if lying)
   */
  Player challengeClaim(Player player);
  
  /**
   * Gets the last claim made in this round
   * @return The most recent unsettled claim, or null if no unsettled claims exist
   */
  Claim getLastClaim();

  /**
   * Settles the last claim made in this round
   * Used after a challenge is resolved to prevent multiple challenges on the same claim
   */
  void settleLastClaim();
  
  /**
   * Gets the player whose turn it is
   * @return The current active player
   */
  Player getCurrentPlayer();
  
  /**
   * Gets all players still active in this round
   * @return List of active players
   */
  List<Player> getActivePlayers();
  
  /**
   * Checks if this round is complete
   * A round is complete when all cards of the current rank have been played/claimed
   * or when only one player remains active
   * @return true if round is finished, false otherwise
   */
  boolean isRoundComplete();
  
  /**
   * Gets all claims made during this round
   * @return List of all claims in chronological order
   */
  List<Claim> getAllClaims();
  
  /**
   * Moves to the next player's turn
   */
  void moveToNextPlayer();
  
  /**
   * Gets the total number of cards of this rank that have been claimed
   * Used to determine when round should end (max 4 cards per rank in deck)
   * @return Total count of claimed cards for this rank
   */
  int getTotalClaimedCards();
  
  /**
   * Resets the round state for a new round
   */
  void resetRound();
}