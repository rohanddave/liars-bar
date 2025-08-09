package controller;

import model.game.*;
import model.exceptions.*;
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
    
    public GameControllerImpl() {
        this.allPlayers = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.gameInitialized = false;
        this.gameStarted = false;
        this.currentRank = Rank.KING; // Start with Kings
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
    
    @Override
    public boolean handleRevolverSpin(Player player) {
        if (!player.isAlive()) {
            throw new IllegalArgumentException("Player is already eliminated");
        }
        
        boolean playerEliminated = this.game.spinRevolver(player);
        
        if (playerEliminated) {
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
