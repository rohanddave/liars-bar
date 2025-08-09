package controller;

import model.game.Game;
import model.game.GameState;
import model.game.Player;
import model.game.Rank;
import model.game.Card;
import java.util.List;

/**
 * Enhanced GameController interface for multiplayer terminal support.
 * Provides methods for initializing multiplayer games, managing player turns,
 * and handling terminal-based user interactions.
 */
public interface GameController {
    
    /**
     * Initializes a new multiplayer game with the specified number of players and names.
     * @param playerCount Number of players (2-4)
     * @param playerNames List of player names
     * @throws IllegalArgumentException if playerCount is not between 2-4 or names list size doesn't match
     */
    void initializeGame(int playerCount, List<String> playerNames);
    
    /**
     * Starts the game session and begins the first round.
     * Must be called after initializeGame.
     * @throws IllegalStateException if game has not been initialized
     */
    void startGameSession();
    
    /**
     * Processes a player's turn, handling their input and updating game state.
     * @param player The player whose turn it is
     * @throws IllegalArgumentException if player is not the current player
     */
    void processPlayerTurn(Player player);
    
    /**
     * Handles a player making a claim about cards they're playing.
     * @param player The player making the claim
     * @param count Number of cards being claimed
     * @param cardIndices List of card indices from player's hand
     * @throws IllegalArgumentException if indices are invalid or count doesn't match indices size
     */
    void handleClaim(Player player, int count, List<Integer> cardIndices);
    
    /**
     * Handles a player challenging the current claim.
     * @param player The player making the challenge
     * @throws IllegalStateException if no claim exists to challenge
     */
    void handleChallenge(Player player);
    
    /**
     * Handles a player spinning the revolver after losing a challenge.
     * @param player The player who must spin the revolver
     * @return true if player is eliminated, false if they survive
     */
    boolean handleRevolverSpin(Player player);
    
    /**
     * Advances to the next player in turn order.
     * Skips eliminated players automatically.
     */
    void advanceToNextPlayer();
    
    /**
     * Checks if the game has ended and handles end-game logic.
     * @return true if game is over, false if it continues
     */
    boolean checkGameEnd();
    
    /**
     * Gets the current game state for display purposes.
     * @return Current GameState object
     */
    GameState getCurrentGameState();
    
    /**
     * Checks if the game is currently active.
     * @return true if game is in progress, false otherwise
     */
    boolean isGameActive();
    
    /**
     * Gets the underlying Game model object.
     * @return The Game instance being controlled
     */
    Game getGame();
    
    /**
     * Gets the current player whose turn it is.
     * @return Current active player
     */
    Player getCurrentPlayer();
    
    /**
     * Gets all players in the game (active and eliminated).
     * @return List of all players
     */
    List<Player> getAllPlayers();
    
    /**
     * Gets only the active (non-eliminated) players.
     * @return List of active players
     */
    List<Player> getActivePlayers();
    
    /**
     * Gets the current round's target rank (card type).
     * @return Current Rank being played
     */
    Rank getCurrentRank();
    
    /**
     * Resets the game to initial state for a new game.
     */
    void resetGame();
}
