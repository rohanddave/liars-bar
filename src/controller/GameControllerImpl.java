package controller;

import model.game.*;
import model.exceptions.*;
import view.RevolverView;
import util.InputHandler;
import java.util.List;
import java.util.ArrayList;

/**
 * Concrete implementation of GameController for multiplayer terminal support.
 * Manages game initialization, player turns, and game flow control.
 */
public class GameControllerImpl implements GameController {
    
    private Game game;
    private List<Player> allPlayers;
    private int currentPlayerIndex;
    private boolean gameInitialized;
    private boolean gameStarted;
    private Rank currentRank;
    private RevolverView revolverView;
    
    public GameControllerImpl() {
        this.allPlayers = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.gameInitialized = false;
        this.gameStarted = false;
        this.currentRank = Rank.KING; // Start with Kings
        this.revolverView = new RevolverView(new InputHandler());
    }
    
    @Override
    public void initializeGame(int playerCount, List<String> playerNames) {
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }
        if (playerNames.size() != playerCount) {
            throw new IllegalArgumentException("Player names count must match player count");
        }
        
        // Clear any existing game state
        this.allPlayers.clear();
        this.currentPlayerIndex = 0;
        
        // Create players
        for (String name : playerNames) {
            Player player = new TerminalPlayer(name);
            this.allPlayers.add(player);
        }
        
        // Create game using builder pattern
        try {
            GameImpl.Builder gameBuilder = new GameImpl.Builder()
                .setRank(this.currentRank);
            
            for (Player player : this.allPlayers) {
                gameBuilder.addPlayer(player);
            }
            
            this.game = gameBuilder.build();
            this.gameInitialized = true;
            
        } catch (GameFullException e) {
            throw new IllegalArgumentException("Failed to initialize game: " + e.getMessage());
        }
    }
    
    @Override
    public void startGameSession() {
        if (!gameInitialized) {
            throw new IllegalStateException("Game must be initialized first");
        }
        
        // Start the game
        this.game.startGame();
        this.gameStarted = true;
        
        // Distribute cards to players (this would be handled by the Game implementation)
        // For now, we'll assume the Game handles card distribution
    }
    
    @Override
    public void processPlayerTurn(Player player) {
        if (!gameStarted) {
            throw new IllegalStateException("Game must be started first");
        }
        
        Player currentPlayer = getCurrentPlayer();
        if (!player.equals(currentPlayer)) {
            throw new IllegalArgumentException("It's not this player's turn");
        }
        
        // Process the player's turn - this would involve getting their action
        // and updating the game state accordingly
        // The actual turn processing would be handled by the terminal UI
    }
    
    @Override
    public void handleClaim(Player player, int count, List<Integer> cardIndices) {
        if (count != cardIndices.size()) {
            throw new IllegalArgumentException("Count must match card indices size");
        }
        
        if (count < 1 || count > 5) {
            throw new IllegalArgumentException("Claim count must be between 1 and 5");
        }
        
        // Validate card indices
        Hand playerHand = player.getHand();
        for (Integer index : cardIndices) {
            if (index < 0) {
                throw new IllegalArgumentException("Card index cannot be negative");
            }
            // Additional validation would check against actual hand size
        }
        
        try {
            // Convert indices to actual cards
            List<Card> cards = new ArrayList<>();
            for (Integer index : cardIndices) {
                Card card = playerHand.getAt(index);
                cards.add(card);
            }
            
            // Make the claim through the game
            this.game.claim(player, count, cards, this.currentRank);
            
        } catch (NoSuchCardException e) {
            throw new IllegalArgumentException("Invalid card selection: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Card index out of bounds: " + e.getMessage());
        }
    }
    
    /**
     * Handles claim input using the ClaimInputView system.
     * This method integrates the terminal-based claim input with the game controller.
     * @param player The player making the claim
     * @param claimInputView The claim input view to use for getting user input
     * @return true if claim was successfully processed, false if cancelled
     */
    public boolean handleClaimWithInput(Player player, view.ClaimInputView claimInputView) {
        if (!gameStarted) {
            throw new IllegalStateException("Game must be started first");
        }
        
        Player currentPlayer = getCurrentPlayer();
        if (!player.equals(currentPlayer)) {
            throw new IllegalArgumentException("It's not this player's turn");
        }
        
        try {
            // Get claim input from the player
            view.ClaimInputView.ClaimInput claimInput = claimInputView.getClaimInput(player, this.currentRank);
            
            if (claimInput == null) {
                return false; // Player cancelled the claim
            }
            
            // Process the claim using the existing handleClaim method
            handleClaim(player, claimInput.getClaimCount(), claimInput.getCardIndices());
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error processing claim: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void handleChallenge(Player player) {
        Claim lastClaim = this.game.getLastClaim();
        if (lastClaim == null) {
            throw new IllegalStateException("No claim exists to challenge");
        }
        
        try {
            this.game.challengeClaim(player);
        } catch (NoActiveClaimException e) {
            throw new IllegalStateException("No active claim to challenge: " + e.getMessage());
        } catch (NotPlayerTurnException e) {
            throw new IllegalArgumentException("It's not this player's turn to challenge: " + e.getMessage());
        }
    }
    
    /**
     * Handles challenge input using the ChallengeView system.
     * This method integrates the terminal-based challenge system with the game controller.
     * @param challenger The player considering the challenge
     * @param challengeView The challenge view to use for user interaction
     * @return ChallengeResult containing the outcome, or null if no challenge was made
     */
    public view.ChallengeView.ChallengeResult handleChallengeWithInput(Player challenger, view.ChallengeView challengeView) {
        if (!gameStarted) {
            throw new IllegalStateException("Game must be started first");
        }
        
        Claim lastClaim = this.game.getLastClaim();
        if (lastClaim == null) {
            throw new IllegalStateException("No claim exists to challenge");
        }
        
        try {
            // Ask the player if they want to challenge
            boolean wantsToChallenge = challengeView.promptForChallenge(challenger, lastClaim, this.currentRank);
            
            if (!wantsToChallenge) {
                return null; // Player chose not to challenge
            }
            
            // Display challenge action
            challengeView.displayChallengeAction(challenger, lastClaim);
            
            // Process the challenge through the game model
            challengeView.displayChallengeProcessing("Revealing cards and determining outcome...");
            
            // Get the actual cards that were played (this would need to be implemented in the Game interface)
            // For now, we'll simulate this by getting cards from the claimed player's recent play
            List<Card> actualCards = getLastPlayedCards(lastClaim.getPlayer());
            
            if (actualCards == null || actualCards.isEmpty()) {
                challengeView.displayChallengeError("Could not retrieve played cards");
                return null;
            }
            
            // Display the challenge reveal and get the result
            view.ChallengeView.ChallengeResult result = challengeView.displayChallengeReveal(
                lastClaim, actualCards, this.currentRank);
            
            // Process the challenge through the game model
            this.game.challengeClaim(challenger);
            
            return result;
            
        } catch (NoActiveClaimException e) {
            challengeView.displayChallengeError("No active claim to challenge");
            return null;
        } catch (NotPlayerTurnException e) {
            challengeView.displayChallengeError("It's not your turn to challenge");
            return null;
        } catch (Exception e) {
            challengeView.displayChallengeError("Error processing challenge: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gets the cards that were last played by a player.
     * This is a placeholder method - in a real implementation, this would be tracked by the Game model.
     * @param player The player whose last played cards to retrieve
     * @return List of cards that were last played, or null if not available
     */
    private List<Card> getLastPlayedCards(Player player) {
        // This is a placeholder implementation
        // In a real game, this information would be stored when the claim was made
        // For now, we'll return some sample cards for testing
        List<Card> sampleCards = new ArrayList<>();
        
        // Try to get cards from the player's hand as a simulation
        Hand playerHand = player.getHand();
        if (playerHand != null) {
            try {
                // Get first few cards as a simulation of what was played
                for (int i = 0; i < Math.min(2, getHandSize(playerHand)); i++) {
                    sampleCards.add(playerHand.getAt(i));
                }
            } catch (IndexOutOfBoundsException e) {
                // Handle empty hand
            }
        }
        
        return sampleCards.isEmpty() ? null : sampleCards;
    }
    
    /**
     * Helper method to get hand size.
     * @param hand The hand to measure
     * @return Number of cards in hand
     */
    private int getHandSize(Hand hand) {
        if (hand == null) return 0;
        
        int count = 0;
        try {
            while (true) {
                hand.getAt(count);
                count++;
            }
        } catch (IndexOutOfBoundsException e) {
            // Expected when we reach the end
        }
        return count;
    }
    
    @Override
    public boolean handleRevolverSpin(Player player) {
        if (!player.isAlive()) {
            throw new IllegalArgumentException("Player is already eliminated");
        }
        
        // Get the player's revolver for display purposes
        Revolver revolver = null;
        if (game instanceof GameImpl) {
            revolver = ((GameImpl) game).getPlayerRevolver(player);
        }
        
        // Display revolver spinning interface
        revolverView.displayRevolverSpin(player);
        
        // Show spinning animation if we have revolver info
        if (revolver instanceof RevolverImpl) {
            RevolverImpl revolverImpl = (RevolverImpl) revolver;
            revolverView.displaySpinningAnimation(revolverImpl.getBulletChamber());
        }
        
        // Display trigger pull interface
        revolverView.displayTriggerPull(player);
        
        // Actually spin the revolver
        boolean playerEliminated = this.game.spinRevolver(player);
        
        // Get chamber position for display
        int chamberPosition = this.game.getRevolverChamberPosition(player);
        if (chamberPosition == -1 && revolver instanceof RevolverImpl) {
            chamberPosition = ((RevolverImpl) revolver).getCurrentChamber();
        }
        
        // Display the outcome
        revolverView.displayRevolverOutcome(player, playerEliminated, chamberPosition);
        
        if (playerEliminated) {
            // Display elimination status
            int remainingPlayers = (int) allPlayers.stream().filter(Player::isAlive).count();
            revolverView.displayPlayerElimination(player, remainingPlayers);
            
            // Player was eliminated, check if game should end
            checkGameEnd();
        }
        
        return playerEliminated;
    }
    
    @Override
    public void advanceToNextPlayer() {
        if (!gameStarted) {
            throw new IllegalStateException("Game must be started first");
        }
        
        // Find next active player
        int startIndex = this.currentPlayerIndex;
        do {
            this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.allPlayers.size();
        } while (!this.allPlayers.get(this.currentPlayerIndex).isAlive() && 
                 this.currentPlayerIndex != startIndex);
        
        // If we've cycled through all players and none are alive, game should end
        if (!this.allPlayers.get(this.currentPlayerIndex).isAlive()) {
            // This shouldn't happen if game end checking is working properly
            throw new IllegalStateException("No active players remaining");
        }
    }
    
    @Override
    public boolean checkGameEnd() {
        if (!gameStarted) {
            return false;
        }
        
        // Check if game is over according to the Game model
        boolean gameOver = this.game.isGameOver();
        
        if (gameOver) {
            this.gameStarted = false;
        }
        
        return gameOver;
    }
    
    @Override
    public GameState getCurrentGameState() {
        if (this.game == null) {
            return null;
        }
        return this.game.getGameState();
    }
    
    @Override
    public boolean isGameActive() {
        return this.gameStarted;
    }
    
    @Override
    public Game getGame() {
        return this.game;
    }
    
    @Override
    public Player getCurrentPlayer() {
        if (!gameStarted || this.allPlayers.isEmpty()) {
            return null;
        }
        return this.allPlayers.get(this.currentPlayerIndex);
    }
    
    @Override
    public List<Player> getAllPlayers() {
        return new ArrayList<>(this.allPlayers);
    }
    
    @Override
    public List<Player> getActivePlayers() {
        if (this.game != null) {
            return this.game.getActivePlayers();
        }
        
        // Fallback: filter alive players from our list
        List<Player> activePlayers = new ArrayList<>();
        for (Player player : this.allPlayers) {
            if (player.isAlive()) {
                activePlayers.add(player);
            }
        }
        return activePlayers;
    }
    
    @Override
    public Rank getCurrentRank() {
        if (this.game != null) {
            return this.game.getRank();
        }
        return this.currentRank;
    }
    
    @Override
    public void resetGame() {
        this.game = null;
        this.allPlayers.clear();
        this.currentPlayerIndex = 0;
        this.gameInitialized = false;
        this.gameStarted = false;
        this.currentRank = Rank.KING;
    }
    
    /**
     * Simple terminal-specific Player implementation.
     * This is a basic implementation for the controller to use.
     */
    private static class TerminalPlayer implements Player {
        private final String id;
        private Hand hand;
        private boolean alive;
        
        public TerminalPlayer(String id) {
            this.id = id;
            this.alive = true;
            // Hand will be set when game starts
        }
        
        @Override
        public String getId() {
            return this.id;
        }
        
        @Override
        public Claim claim(Rank rank, int count, List<Card> droppedCards) throws NoSuchCardException {
            // Remove cards from hand
            for (Card card : droppedCards) {
                this.hand.discard(card);
            }
            
            // Return a claim (would need ClaimImpl)
            return new TerminalClaim(this, count);
        }
        
        @Override
        public void challengeClaim(Claim claim) {
            // Challenge logic would be handled by the Game
        }
        
        @Override
        public boolean shoot() {
            // Revolver logic - for now return random
            boolean eliminated = Math.random() < 0.16667; // 1/6 chance
            if (eliminated) {
                this.alive = false;
            }
            return eliminated;
        }
        
        @Override
        public boolean isAlive() {
            return this.alive;
        }
        
        @Override
        public Hand getHand() {
            return this.hand;
        }
        
        @Override
        public void setHand(Hand hand) {
            this.hand = hand;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TerminalPlayer that = (TerminalPlayer) obj;
            return id.equals(that.id);
        }
        
        @Override
        public int hashCode() {
            return id.hashCode();
        }
        
        @Override
        public String toString() {
            return "TerminalPlayer{id='" + id + "', alive=" + alive + "}";
        }
    }
    
    /**
     * Simple Claim implementation for terminal use.
     */
    private static class TerminalClaim implements Claim {
        private final Player player;
        private final int count;
        
        public TerminalClaim(Player player, int count) {
            this.player = player;
            this.count = count;
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
        public String toString() {
            return "TerminalClaim{player=" + player.getId() + ", count=" + count + "}";
        }
    }
}
