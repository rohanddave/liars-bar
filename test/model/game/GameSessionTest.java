package model.game;

import model.exceptions.NoSuchCardException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Comprehensive unit tests for GameSession class.
 * Tests session management, player management, turn advancement, 
 * round progression, and win condition checking.
 */
public class GameSessionTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running GameSession Tests...");
        System.out.println("============================");
        
        testGameSessionFunctionality();
        
        System.out.println("\n============================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All GameSession tests passed!");
        } else {
            System.out.println("❌ Some GameSession tests failed!");
        }
    }
    
    private static void testGameSessionFunctionality() {
        System.out.println("\nTesting GameSession functionality...");
        
        testConstructorAndInitialization();
        testSessionStartAndManagement();
        testPlayerManagement();
        testTurnAdvancement();
        testGameCompletion();
        testSessionReset();
        testGettersAndState();
    }
    
    private static void testConstructorAndInitialization() {
        // Test valid constructor
        runTest("Constructor with valid player count", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            
            assert gameSession.getPlayerCount() == 3 : "Should have 3 players";
            assert gameSession.getActivePlayerCount() == 3 : "Should have 3 active players";
            assert gameSession.getEliminatedPlayerCount() == 0 : "Should have 0 eliminated players";
            assert !gameSession.isSessionActive() : "Session should not be active initially";
            assert !gameSession.isGameComplete() : "Game should not be complete initially";
            assert gameSession.getRoundNumber() == 0 : "Should start at round 0";
            assert gameSession.getCurrentRank() == Rank.KING : "Should start with KING rank";
        });
        
        // Test invalid constructor parameters
        runTest("Constructor rejects null players", () -> {
            boolean exceptionThrown = false;
            try {
                new GameSession(null);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("2-4 players") : "Should mention valid player range";
            }
            assert exceptionThrown : "Should throw exception for null players";
        });
        
        runTest("Constructor rejects too few players", () -> {
            List<Player> onePlayer = Arrays.asList(new MockPlayer("Player1"));
            boolean exceptionThrown = false;
            try {
                new GameSession(onePlayer);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("2-4 players") : "Should mention valid player range";
            }
            assert exceptionThrown : "Should throw exception for too few players";
        });
        
        runTest("Constructor rejects too many players", () -> {
            List<Player> fivePlayers = createTestPlayers(5);
            boolean exceptionThrown = false;
            try {
                new GameSession(fivePlayers);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("2-4 players") : "Should mention valid player range";
            }
            assert exceptionThrown : "Should throw exception for too many players";
        });
    }
    
    private static void testSessionStartAndManagement() {
        runTest("startSession initializes game and activates session", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            
            gameSession.startSession();
            
            assert gameSession.isSessionActive() : "Session should be active after start";
            assert !gameSession.isGameComplete() : "Game should not be complete after start";
            assert gameSession.getGame() != null : "Game should be initialized";
            assert gameSession.getCurrentPlayer() != null : "Should have current player";
        });
        
        runTest("startSession rejects starting already active session", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            boolean exceptionThrown = false;
            try {
                gameSession.startSession();
            } catch (IllegalStateException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("already active") : "Should mention session is already active";
            }
            assert exceptionThrown : "Should throw exception when starting already active session";
        });
    }
    
    private static void testTurnAdvancement() {
        runTest("advanceToNextPlayer cycles through active players", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            Player firstPlayer = gameSession.getCurrentPlayer();
            assert firstPlayer != null : "Should have first player";
            
            Player secondPlayer = gameSession.advanceToNextPlayer();
            assert secondPlayer != null : "Should have second player";
            assert !firstPlayer.equals(secondPlayer) : "Second player should be different";
            
            Player thirdPlayer = gameSession.advanceToNextPlayer();
            assert thirdPlayer != null : "Should have third player";
            assert !secondPlayer.equals(thirdPlayer) : "Third player should be different";
            
            // Should cycle back to first player
            Player cycledPlayer = gameSession.advanceToNextPlayer();
            assert firstPlayer.equals(cycledPlayer) : "Should cycle back to first player";
        });
        
        runTest("advanceToNextPlayer skips eliminated players", () -> {
            List<MockPlayer> mockPlayers = Arrays.asList(
                new MockPlayer("Player1"),
                new MockPlayer("Player2"), 
                new MockPlayer("Player3")
            );
            List<Player> testPlayers = new ArrayList<>(mockPlayers);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            Player firstPlayer = gameSession.getCurrentPlayer();
            
            // Eliminate the second player
            mockPlayers.get(1).eliminate();
            gameSession.eliminatePlayer(mockPlayers.get(1));
            
            Player nextPlayer = gameSession.advanceToNextPlayer();
            
            // Should skip eliminated player2
            assert !mockPlayers.get(1).equals(nextPlayer) : "Should skip eliminated player";
            assert gameSession.getActivePlayerCount() == 2 : "Should have 2 active players";
            assert gameSession.getEliminatedPlayerCount() == 1 : "Should have 1 eliminated player";
        });
        
        runTest("advanceToNextPlayer throws exception when session not active", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            
            boolean exceptionThrown = false;
            try {
                gameSession.advanceToNextPlayer();
            } catch (IllegalStateException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("not active") : "Should mention session not active";
            }
            assert exceptionThrown : "Should throw exception when session not active";
        });
    }
    
    private static void testPlayerManagement() {
        runTest("eliminatePlayer moves player from active to eliminated", () -> {
            List<MockPlayer> mockPlayers = Arrays.asList(
                new MockPlayer("Player1"),
                new MockPlayer("Player2"), 
                new MockPlayer("Player3")
            );
            List<Player> testPlayers = new ArrayList<>(mockPlayers);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            assert gameSession.getActivePlayerCount() == 3 : "Should start with 3 active players";
            assert gameSession.getEliminatedPlayerCount() == 0 : "Should start with 0 eliminated players";
            
            gameSession.eliminatePlayer(mockPlayers.get(1));
            
            assert gameSession.getActivePlayerCount() == 2 : "Should have 2 active players after elimination";
            assert gameSession.getEliminatedPlayerCount() == 1 : "Should have 1 eliminated player";
            assert gameSession.isPlayerEliminated(mockPlayers.get(1)) : "Player should be eliminated";
            assert !gameSession.isPlayerActive(mockPlayers.get(1)) : "Player should not be active";
        });
        
        runTest("eliminatePlayer rejects invalid player", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            MockPlayer outsidePlayer = new MockPlayer("Outside");
            
            boolean exceptionThrown = false;
            try {
                gameSession.eliminatePlayer(outsidePlayer);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("not in this game") : "Should mention player not in game";
            }
            assert exceptionThrown : "Should throw exception for invalid player";
        });
    }
    
    private static void testGameCompletion() {
        runTest("checkGameCompletion detects single player remaining", () -> {
            List<MockPlayer> mockPlayers = Arrays.asList(
                new MockPlayer("Player1"),
                new MockPlayer("Player2"), 
                new MockPlayer("Player3")
            );
            List<Player> testPlayers = new ArrayList<>(mockPlayers);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            assert !gameSession.checkGameCompletion() : "Game should not be complete initially";
            
            // Eliminate two players
            mockPlayers.get(1).eliminate();
            mockPlayers.get(2).eliminate();
            gameSession.eliminatePlayer(mockPlayers.get(1));
            gameSession.eliminatePlayer(mockPlayers.get(2));
            
            assert gameSession.checkGameCompletion() : "Game should be complete with one player";
            assert gameSession.isGameComplete() : "Game should be marked complete";
            assert !gameSession.isSessionActive() : "Session should not be active";
            assert gameSession.getWinner().equals(mockPlayers.get(0)) : "First player should be winner";
        });
    }
    
    private static void testSessionReset() {
        runTest("resetSession restores initial state", () -> {
            List<MockPlayer> mockPlayers = Arrays.asList(
                new MockPlayer("Player1"),
                new MockPlayer("Player2"), 
                new MockPlayer("Player3")
            );
            List<Player> testPlayers = new ArrayList<>(mockPlayers);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            // Eliminate a player
            gameSession.eliminatePlayer(mockPlayers.get(1));
            
            // Reset session
            gameSession.resetSession();
            
            assert gameSession.getActivePlayerCount() == 3 : "Should have 3 active players after reset";
            assert gameSession.getEliminatedPlayerCount() == 0 : "Should have 0 eliminated players after reset";
            assert !gameSession.isSessionActive() : "Session should not be active after reset";
            assert !gameSession.isGameComplete() : "Game should not be complete after reset";
            assert gameSession.getRoundNumber() == 0 : "Should be at round 0 after reset";
            assert gameSession.getCurrentRank() == Rank.KING : "Should be at KING rank after reset";
            assert gameSession.getWinner() == null : "Should have no winner after reset";
        });
    }
    
    private static void testGettersAndState() {
        runTest("getters return correct values", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            
            assert gameSession.getAllPlayers().size() == 3 : "Should have 3 total players";
            assert gameSession.getActivePlayers().size() == 3 : "Should have 3 active players";
            assert gameSession.getEliminatedPlayers().size() == 0 : "Should have 0 eliminated players";
            
            assert gameSession.isPlayerActive(testPlayers.get(0)) : "First player should be active";
            assert !gameSession.isPlayerEliminated(testPlayers.get(0)) : "First player should not be eliminated";
            
            // Test immutability of returned lists
            List<Player> allPlayers = gameSession.getAllPlayers();
            boolean exceptionThrown = false;
            try {
                allPlayers.add(new MockPlayer("Test"));
            } catch (UnsupportedOperationException e) {
                exceptionThrown = true;
            }
            assert exceptionThrown : "Returned list should be immutable";
        });
        
        runTest("round advancement logic", () -> {
            List<Player> testPlayers = createTestPlayers(3);
            GameSession gameSession = new GameSession(testPlayers);
            gameSession.startSession();
            
            assert gameSession.getRoundNumber() == 0 : "Should start at round 0";
            assert gameSession.getCurrentRank() == Rank.KING : "Should start with KING rank";
            
            // Test the method (will return false since Game.isRoundComplete() returns false by default)
            assert !gameSession.advanceRoundIfComplete() : "Should not advance round when not complete";
        });
    }
    
    private static List<Player> createTestPlayers(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            players.add(new MockPlayer("Player" + i));
        }
        return players;
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
     * Mock Player implementation for testing purposes.
     */
    private static class MockPlayer implements Player {
        private final String id;
        private Hand hand;
        private boolean alive;
        
        public MockPlayer(String id) {
            this.id = id;
            this.alive = true;
            this.hand = new MockHand();
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
            return new MockClaim(this, count);
        }
        
        @Override
        public void challengeClaim(Claim claim) {
            // Mock implementation
        }
        
        @Override
        public boolean shoot() {
            return false; // Mock - never eliminates
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
            MockPlayer that = (MockPlayer) obj;
            return id.equals(that.id);
        }
        
        @Override
        public int hashCode() {
            return id.hashCode();
        }
        
        @Override
        public String toString() {
            return "MockPlayer{id='" + id + "'}";
        }
    }
    
    /**
     * Mock Hand implementation for testing.
     */
    private static class MockHand implements Hand {
        private List<Card> cards = new ArrayList<>();
        
        @Override
        public Card getAt(int index) throws IndexOutOfBoundsException {
            if (index < 0 || index >= cards.size()) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + cards.size());
            }
            return cards.get(index);
        }
        
        @Override
        public void add(Card card) {
            cards.add(card);
        }
        
        @Override
        public void discard(Card card) {
            cards.remove(card);
        }
    }
    
    /**
     * Mock Claim implementation for testing.
     */
    private static class MockClaim implements Claim {
        private final Player player;
        private final int count;
        
        public MockClaim(Player player, int count) {
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