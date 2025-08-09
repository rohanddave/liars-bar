package model.game;

import controller.GameController;
import controller.GameControllerImpl;
import java.util.Arrays;
import java.util.List;

/**
 * Integration tests to verify GameSession works with the existing game infrastructure.
 */
public class GameSessionIntegrationTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running GameSession Integration Tests...");
        System.out.println("=======================================");
        
        testGameSessionWithController();
        
        System.out.println("\n=======================================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All GameSession integration tests passed!");
        } else {
            System.out.println("❌ Some GameSession integration tests failed!");
        }
    }
    
    private static void testGameSessionWithController() {
        System.out.println("\nTesting GameSession integration with GameController...");
        
        // Test that GameSession can be used alongside GameController
        runTest("GameSession and GameController compatibility", () -> {
            // Create players for GameSession
            List<Player> sessionPlayers = Arrays.asList(
                new TestPlayer("Alice"),
                new TestPlayer("Bob"),
                new TestPlayer("Charlie")
            );
            
            // Create GameSession
            GameSession gameSession = new GameSession(sessionPlayers);
            
            // Verify initial state
            assert gameSession.getPlayerCount() == 3 : "Should have 3 players";
            assert !gameSession.isSessionActive() : "Session should not be active initially";
            
            // Start session
            gameSession.startSession();
            assert gameSession.isSessionActive() : "Session should be active after start";
            
            // Test player advancement
            Player firstPlayer = gameSession.getCurrentPlayer();
            assert firstPlayer != null : "Should have current player";
            
            Player secondPlayer = gameSession.advanceToNextPlayer();
            assert secondPlayer != null : "Should have second player";
            assert !firstPlayer.equals(secondPlayer) : "Players should be different";
            
            // Test that we can also create a GameController independently
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Dave", "Eve");
            
            controller.initializeGame(2, playerNames);
            controller.startGameSession();
            
            assert controller.isGameActive() : "Controller game should be active";
            assert controller.getAllPlayers().size() == 2 : "Controller should have 2 players";
            
            // Both should work independently
            assert gameSession.isSessionActive() : "GameSession should still be active";
            assert gameSession.getPlayerCount() == 3 : "GameSession should still have 3 players";
        });
        
        // Test GameSession state management
        runTest("GameSession state transitions", () -> {
            List<Player> players = Arrays.asList(
                new TestPlayer("Player1"),
                new TestPlayer("Player2")
            );
            
            GameSession session = new GameSession(players);
            
            // Test initial state
            assert !session.isSessionActive() : "Should not be active initially";
            assert !session.isGameComplete() : "Should not be complete initially";
            assert session.getWinner() == null : "Should have no winner initially";
            
            // Start session
            session.startSession();
            assert session.isSessionActive() : "Should be active after start";
            assert !session.isGameComplete() : "Should not be complete after start";
            
            // Eliminate one player to end game
            TestPlayer player2 = (TestPlayer) players.get(1);
            player2.eliminate();
            session.eliminatePlayer(player2);
            
            // Check game completion
            assert session.checkGameCompletion() : "Game should be complete";
            assert !session.isSessionActive() : "Session should not be active after completion";
            assert session.isGameComplete() : "Game should be marked complete";
            assert session.getWinner() != null : "Should have a winner";
            assert session.getWinner().equals(players.get(0)) : "First player should be winner";
        });
        
        // Test GameSession reset functionality
        runTest("GameSession reset functionality", () -> {
            List<TestPlayer> testPlayers = Arrays.asList(
                new TestPlayer("Alice"),
                new TestPlayer("Bob"),
                new TestPlayer("Charlie")
            );
            List<Player> players = Arrays.asList(testPlayers.toArray(new Player[0]));
            
            GameSession session = new GameSession(players);
            session.startSession();
            
            // Eliminate a player
            testPlayers.get(1).eliminate();
            session.eliminatePlayer(testPlayers.get(1));
            
            assert session.getActivePlayerCount() == 2 : "Should have 2 active players";
            assert session.getEliminatedPlayerCount() == 1 : "Should have 1 eliminated player";
            
            // Reset session
            session.resetSession();
            
            assert session.getActivePlayerCount() == 3 : "Should have 3 active players after reset";
            assert session.getEliminatedPlayerCount() == 0 : "Should have 0 eliminated players after reset";
            assert !session.isSessionActive() : "Should not be active after reset";
            assert !session.isGameComplete() : "Should not be complete after reset";
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
            e.printStackTrace();
        }
    }
    
    /**
     * Simple test Player implementation for integration testing.
     */
    private static class TestPlayer implements Player {
        private final String id;
        private Hand hand;
        private boolean alive;
        
        public TestPlayer(String id) {
            this.id = id;
            this.alive = true;
            this.hand = new TestHand();
        }
        
        public void eliminate() {
            this.alive = false;
        }
        
        @Override
        public String getId() {
            return id;
        }
        
        @Override
        public Claim claim(Rank rank, int count, List<Card> droppedCards) {
            return new TestClaim(this, count);
        }
        
        @Override
        public void challengeClaim(Claim claim) {
            // Test implementation
        }
        
        @Override
        public boolean shoot() {
            return false;
        }
        
        @Override
        public boolean isAlive() {
            return alive;
        }
        
        @Override
        public Hand getHand() {
            return hand;
        }
        
        @Override
        public void setHand(Hand hand) {
            this.hand = hand;
        }
        
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
        
        @Override
        public String toString() {
            return "TestPlayer{id='" + id + "'}";
        }
    }
    
    /**
     * Simple test Hand implementation.
     */
    private static class TestHand implements Hand {
        @Override
        public Card getAt(int index) throws IndexOutOfBoundsException {
            throw new IndexOutOfBoundsException("Test hand is empty");
        }
        
        @Override
        public void add(Card card) {
            // Test implementation
        }
        
        @Override
        public void discard(Card card) {
            // Test implementation
        }
    }
    
    /**
     * Simple test Claim implementation.
     */
    private static class TestClaim implements Claim {
        private final Player player;
        private final int count;
        
        public TestClaim(Player player, int count) {
            this.player = player;
            this.count = count;
        }
        
        @Override
        public int getCount() {
            return count;
        }
        
        @Override
        public Player getPlayer() {
            return player;
        }
    }
}