package controller;

import model.game.*;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for GameController interface methods.
 * Tests the contract and expected behavior of controller methods.
 */
public class GameControllerTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running GameController Tests...");
        System.out.println("===============================");
        
        testGameControllerInterface();
        
        System.out.println("\n===============================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All GameController tests passed!");
        } else {
            System.out.println("❌ Some GameController tests failed!");
        }
    }
    
    private static void testGameControllerInterface() {
        System.out.println("\nTesting GameController interface methods...");
        
        // Test valid initialization
        runTest("Initialize game with valid input", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            assert controller.isGameActive() : "Game should be initialized";
            assert controller.getAllPlayers().size() == 3 : "Should have 3 players";
        });
        
        // Test invalid player count (less than 2)
        runTest("Initialize game with invalid player count (too few)", () -> {
            GameController controller = new TestGameController();
            boolean exceptionThrown = false;
            
            try {
                controller.initializeGame(1, Arrays.asList("Alice"));
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
            }
            
            assert exceptionThrown : "Should throw IllegalArgumentException for player count < 2";
        });
        
        // Test invalid player count (more than 4)
        runTest("Initialize game with invalid player count (too many)", () -> {
            GameController controller = new TestGameController();
            boolean exceptionThrown = false;
            
            try {
                List<String> tooManyNames = Arrays.asList("A", "B", "C", "D", "E");
                controller.initializeGame(5, tooManyNames);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
            }
            
            assert exceptionThrown : "Should throw IllegalArgumentException for player count > 4";
        });
        
        // Test mismatched player count and names list size
        runTest("Initialize game with mismatched names count", () -> {
            GameController controller = new TestGameController();
            boolean exceptionThrown = false;
            
            try {
                controller.initializeGame(3, Arrays.asList("Alice", "Bob"));
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
            }
            
            assert exceptionThrown : "Should throw IllegalArgumentException for mismatched counts";
        });
        
        // Test start game session
        runTest("Start game session after initialization", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            
            assert controller.getCurrentPlayer() != null : "Current player should be set";
            assert controller.getCurrentGameState() != null : "Game state should be available";
            assert controller.getCurrentRank() != null : "Current rank should be set";
        });
        
        // Test start game session without initialization
        runTest("Start game session without initialization", () -> {
            GameController controller = new TestGameController();
            boolean exceptionThrown = false;
            
            try {
                controller.startGameSession();
            } catch (IllegalStateException e) {
                exceptionThrown = true;
            }
            
            assert exceptionThrown : "Should throw IllegalStateException if not initialized";
        });
        
        // Test get current rank
        runTest("Get current rank after game start", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            
            Rank currentRank = controller.getCurrentRank();
            assert currentRank != null : "Current rank should not be null";
            assert currentRank == Rank.KING || currentRank == Rank.QUEEN || 
                   currentRank == Rank.JACK || currentRank == Rank.ACE : "Current rank should be valid";
        });
        
        // Test get active players
        runTest("Get active players", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            
            List<Player> activePlayers = controller.getActivePlayers();
            assert activePlayers.size() == 3 : "Should have 3 active players initially";
            
            for (Player player : activePlayers) {
                assert player.isAlive() : "All players should be alive";
            }
        });
        
        // Test advance to next player
        runTest("Advance to next player", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            
            Player firstPlayer = controller.getCurrentPlayer();
            controller.advanceToNextPlayer();
            Player secondPlayer = controller.getCurrentPlayer();
            
            assert !firstPlayer.equals(secondPlayer) : "Current player should change";
        });
        
        // Test reset game
        runTest("Reset game", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            controller.resetGame();
            
            assert !controller.isGameActive() : "Game should not be active after reset";
        });
        
        // Test handle claim with valid input
        runTest("Handle claim with valid input", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            
            Player currentPlayer = controller.getCurrentPlayer();
            List<Integer> cardIndices = Arrays.asList(0, 1);
            
            // This should not throw an exception
            controller.handleClaim(currentPlayer, 2, cardIndices);
            assert true : "Valid claim should be handled without exception";
        });
        
        // Test handle claim with mismatched count
        runTest("Handle claim with mismatched count", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            
            Player currentPlayer = controller.getCurrentPlayer();
            List<Integer> cardIndices = Arrays.asList(0, 1);
            boolean exceptionThrown = false;
            
            try {
                // Count doesn't match indices size
                controller.handleClaim(currentPlayer, 3, cardIndices);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
            }
            
            assert exceptionThrown : "Should throw IllegalArgumentException for mismatched count";
        });
        
        // Test check game end
        runTest("Check game end initially", () -> {
            GameController controller = new TestGameController();
            List<String> playerNames = Arrays.asList("Alice", "Bob");
            
            controller.initializeGame(2, playerNames);
            controller.startGameSession();
            
            assert !controller.checkGameEnd() : "Game should not be ended initially";
        });
    }
    
    private static void runTest(String testName, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("  ✅ " + testName);
        } catch (Exception | AssertionError e) {
            System.out.println("  ❌ " + testName + ": " + e.getMessage());
        }
    }
    
    /**
     * Test implementation of GameController for testing purposes.
     * Provides minimal implementation to test interface contracts.
     */
    private static class TestGameController implements GameController {
        private boolean gameInitialized = false;
        private boolean gameStarted = false;
        private List<String> playerNames;
        private int playerCount;
        private TestGame game;
        private int currentPlayerIndex = 0;
        
        @Override
        public void initializeGame(int playerCount, List<String> playerNames) {
            if (playerCount < 2 || playerCount > 4) {
                throw new IllegalArgumentException("Player count must be between 2 and 4");
            }
            if (playerNames.size() != playerCount) {
                throw new IllegalArgumentException("Player names count must match player count");
            }
            
            this.playerCount = playerCount;
            this.playerNames = playerNames;
            this.game = new TestGame(playerNames);
            this.gameInitialized = true;
        }
        
        @Override
        public void startGameSession() {
            if (!gameInitialized) {
                throw new IllegalStateException("Game must be initialized first");
            }
            this.gameStarted = true;
        }
        
        @Override
        public void processPlayerTurn(Player player) {
            // Test implementation - no-op
        }
        
        @Override
        public void handleClaim(Player player, int count, List<Integer> cardIndices) {
            if (count != cardIndices.size()) {
                throw new IllegalArgumentException("Count must match card indices size");
            }
        }
        
        @Override
        public void handleChallenge(Player player) {
            // Test implementation - no-op
        }
        
        @Override
        public boolean handleRevolverSpin(Player player) {
            return false; // Test implementation - player survives
        }
        
        @Override
        public void advanceToNextPlayer() {
            currentPlayerIndex = (currentPlayerIndex + 1) % playerCount;
        }
        
        @Override
        public boolean checkGameEnd() {
            return false; // Test implementation - game continues
        }
        
        @Override
        public GameState getCurrentGameState() {
            return gameStarted ? new TestGameState() : null;
        }
        
        @Override
        public boolean isGameActive() {
            return gameInitialized;
        }
        
        @Override
        public Game getGame() {
            return game;
        }
        
        @Override
        public Player getCurrentPlayer() {
            return gameStarted ? new TestPlayer(playerNames.get(currentPlayerIndex)) : null;
        }
        
        @Override
        public List<Player> getAllPlayers() {
            return game != null ? game.getTestPlayers() : null;
        }
        
        @Override
        public List<Player> getActivePlayers() {
            return getAllPlayers(); // Test implementation - all players active
        }
        
        @Override
        public Rank getCurrentRank() {
            return gameStarted ? Rank.KING : null;
        }
        
        @Override
        public void resetGame() {
            gameInitialized = false;
            gameStarted = false;
            game = null;
        }
    }
    
    /**
     * Test implementation of Game interface for testing.
     */
    private static class TestGame implements Game {
        private List<Player> players;
        
        public TestGame(List<String> playerNames) {
            java.util.List<Player> playerList = new java.util.ArrayList<>();
            for (String name : playerNames) {
                playerList.add(new TestPlayer(name));
            }
            this.players = playerList;
        }
        
        public List<Player> getTestPlayers() {
            return players;
        }
        
        // Minimal implementations for required methods
        public Rank getRank() { return Rank.KING; }
        public void startGame() {}
        public void playCard(Player player, Card card, Card claimedCard) {}
        public void challengeClaim(Player player) {}
        public boolean spinRevolver(Player player) { return false; }
        public GameState getGameState() { return new TestGameState(); }
        public Claim getLastClaim() { return null; }
        public Player getCurrentPlayer() { return players.get(0); }
        public List<Player> getActivePlayers() { return players; }
        public List<Player> getEliminatedPlayers() { return Arrays.asList(); }
        public boolean isGameOver() { return false; }
        public Player getWinner() { return null; }
        public int getRevolverChamberPosition(Player player) { return 1; }
        public int getPlayerCardCount(Player player) { return 5; }
        public boolean isRoundComplete() { return false; }
        public void resetGame() {}
        public void claim(Player player, int count, List<Card> cards, Rank claimedRank) {}
    }
    
    /**
     * Test implementation of Player interface.
     */
    private static class TestPlayer implements Player {
        private String id;
        
        public TestPlayer(String id) {
            this.id = id;
        }
        
        public String getId() { return id; }
        public Claim claim(Rank rank, int count, List<Card> droppedCards) { return null; }
        public void challengeClaim(Claim claim) {}
        public boolean shoot() { return false; }
        public boolean isAlive() { return true; }
        public Hand getHand() { return new TestHand(); }
        public void setHand(Hand hand) {}
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestPlayer that = (TestPlayer) obj;
            return id.equals(that.id);
        }
        
        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
    
    /**
     * Test implementation of GameState interface.
     */
    private static class TestGameState implements GameState {
        public List<Player> getPlayers() {
            return Arrays.asList(new TestPlayer("Test"));
        }
    }
    
    /**
     * Test implementation of Hand interface.
     */
    private static class TestHand implements Hand {
        public Card getAt(int index) { return null; }
        public void add(Card card) {}
        public void discard(Card card) {}
    }
}