package com.tfc.liarsbar.model.game;

import com.tfc.liarsbar.model.exceptions.NoSuchCardException;

import java.util.List;


/**
 * Class represents a game of Liars Bar.
 *
 * RULES OF LIARS BAR (Cards & Revolver Version):
 * - Each player starts with 5 cards and 1 revolver with 1 bullet in random chamber
 * - Players take turns playing cards face-down and claiming what they played
 * - Claims must match the current round's suit (Aces, Kings, Queens, or Jacks)
 * - Players can either play a card (truthfully or lie) or challenge the previous player's claim
 * - When challenged, the claimed card is revealed:
 *   - If the claim was true, the challenger must spin the revolver
 *   - If the claim was false (lie detected), the liar must spin the revolver
 * - Spinning the revolver: player pulls trigger - if bullet fires, they're eliminated
 * - If revolver clicks (empty chamber), bullet moves to next chamber and player continues
 * - Players are also eliminated when they run out of cards
 * - Round ends when all cards of current suit are played, then moves to next suit
 * - Last player standing wins
 */
public interface Game {
  void addPlayer(Player player);

  Rank getRank();

  /**
   * Starts a new game with the given players
   * @throws IllegalArgumentException if less than 2 players or more than max allowed
   */
  void startGame();

  /**
   * Player plays a card and makes a claim about what they played
   * @param player The player playing the card
   * @param card The card being played (face-down to other players)
   * @param claimedCard The card the player claims to have played
   */
  void playCard(Player player, Card card, Card claimedCard);

  /**
   * Challenges the previous player's claim
   * @param player The player making the challenge
   */
  Player challengeClaim(Player player);

  /**
   * Forces a player to spin the revolver (after losing a challenge)
   * @param player The player who must spin the revolver
   */
  void spinRevolver(Player player);

  /**
   * Gets the current game state
   * @param player The player requesting the game state (to show their hand while hiding others')
   * @return Current state of the game
   */
  String getGameState(Player player);

  /**
   * Gets the last played card claim
   * @return The most recent unsettled claim, or null if no unsettled claims exist
   */
  Claim getLastClaim();

  /**
   * Settles the last claim made in the current round
   * Used after a challenge is resolved to prevent multiple challenges on the same claim
   */
  void settleLastClaim();

  /**
   * Gets the player whose turn it is
   * @return The current active player
   */
  Player getCurrentPlayer();

  /**
   * Gets all players still in the game
   * @return List of active players
   */
  List<Player> getActivePlayers();

  /**
   * Gets all players who have been eliminated
   * @return List of eliminated players
   */
  List<Player> getEliminatedPlayers();

  /**
   * Checks if the game is over
   * @return true if game is finished, false otherwise
   */
  boolean isGameOver();

  /**
   * Gets the winner of the game
   * @return The winning player, or null if game is not over
   */
  Player getWinner();

  /**
   * Gets the current revolver chamber position for a player
   * Used for UI display purposes
   * @param player The player whose revolver to check
   * @return Chamber position (1-6), or -1 if player eliminated
   */
  int getRevolverChamberPosition(Player player);

  /**
   * Gets the number of cards remaining in a player's hand
   * @param player The player to check
   * @return Number of cards in hand
   */
  int getPlayerCardCount(Player player);

  /**
   * Checks if current round is complete (all cards of suit played)
   * @return true if round should advance to next suit
   */
  boolean isRoundComplete();

  /**
   * Resets the game to initial state
   */
  void resetGame();

  void claim(Player player, int count, List<Card> cards, Rank claimedRank) throws NoSuchCardException;

  void moveToNextMove();
  
  /**
   * Checks if the game has been started
   * @return true if game has been started, false otherwise
   */
  boolean isGameStarted();

  /**
   * Removes a player completely from the game
   * @param player The player to remove
   */
  void removePlayer(Player player);
}