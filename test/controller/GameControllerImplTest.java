package controller;

import model.game.*;
import java.util.Arrays;
import java.util.List;

/**
 * Integration tests for GameControllerImpl.
 * Tests the concrete implementation with model integration.
 */
public class GameControllerImplTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running GameControllerImpl Integration Tests...");
        System.out.println("==============================================");
        
        testGameControllerImplementation();
        
        System.out.println("\n==============================================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All GameControllerImpl integration tests passed!");
        } else {
            System.out.println("❌ Some GameControllerImpl integration tests failed!");
        }
    }
    
    private static void testGameControllerImplementation() {
        System.out.println("\nTesting GameControllerImpl integration...");
        
        // Test complete game initialization flow
        runTest("Complete game initialization and start", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            // Initialize game
            controller.initializeGame(3, playerNames);
            assert controller.isGameActive() : "Game should be active after initialization";
            assert controller.getAllPlayers().size() == 3 : "Should have 3 players";
            
            // Start game session
            controller.startGameSession();
            assert controller.getCurrentPlayer() != null : "Should have current player after start";
            assert controller.getCurrentGameState() != null : "Should have game state after start";
            
            // Verify players are properly initialized
            List<Player> players = controller.getAllPlayers();
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                assert player.getId().equals(playerNames.get(i)) : "Player ID should match name";
                assert player.isAlive() : "All players should be alive initially";
            }
        });
        
        // Test player turn advancement
        runTest("Player turn advancement", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob");
            
            controller.initializeGame(2, playerNames);
            controller.startGameSession();
            
            Player firstPlayer = controller.getCurrentPlayer();
            controller.advanceToNextPlayer();
            Player secondPlayer = controller.getCurrentPlayer();
            
            assert !firstPlayer.equals(secondPlayer) : "Current player should change";
            assert secondPlayer.getId().equals("Bob") : "Second player should be Bob";
            
            // Advance again to cycle back
            controller.advanceToNextPlayer();
            Player thirdPlayer = controller.getCurrentPlayer();
            assert thirdPlayer.equals(firstPlayer) : "Should cycle back to first player";
        });
        
        // Test game reset functionality
        runTest("Game reset functionality", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob");
            
            controller.initializeGame(2, playerNames);
            controller.startGameSession();
            
            assert controller.isGameActive() : "Game should be active";
            assert controller.getCurrentPlayer() != null : "Should have current player";
            
            controller.resetGame();
            
            assert !controller.isGameActive() : "Game should not be active after reset";
            assert controller.getAllPlayers().isEmpty() : "Should have no players after reset";
            assert controller.getCurrentPlayer() == null : "Should have no current player after reset";
        });
        
        // Test current rank functionality
        runTest("Current rank management", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob");
            
            controller.initializeGame(2, playerNames);
            controller.startGameSession();
            
            Rank currentRank = controller.getCurrentRank();
            assert currentRank != null : "Current rank should not be null";
            assert currentRank == Rank.KING : "Should start with KING rank";
        });
        
        // Test active players filtering
        runTest("Active players filtering", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
            
            controller.initializeGame(3, playerNames);
            controller.startGameSession();
            
            List<Player> activePlayers = controller.getActivePlayers();
            assert activePlayers.size() == 3 : "Should have 3 active players initially";
            
            for (Player player : activePlayers) {
                assert player.isAlive() : "All active players should be alive";
            }
        });
        
        // Test error handling for invalid initialization
        runTest("Error handling for invalid player count", () -> {
            GameController controller = new GameControllerImpl();
            boolean exceptionThrown = false;
            
            try {
                controller.initializeGame(1, Arrays.asList("Alice"));
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("between 2 and 4") : "Should mention valid range";
            }
            
            assert exceptionThrown : "Should throw exception for invalid player count";
        });
        
        // Test error handling for starting without initialization
        runTest("Error handling for starting without initialization", () -> {
            GameController controller = new GameControllerImpl();
            boolean exceptionThrown = false;
            
            try {
                controller.startGameSession();
            } catch (IllegalStateException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("initialized first") : "Should mention initialization requirement";
            }
            
            assert exceptionThrown : "Should throw exception when starting without initialization";
        });
        
        // Test claim validation
        runTest("Claim input validation", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob");
            
            controller.initializeGame(2, playerNames);
            controller.startGameSession();
            
            Player currentPlayer = controller.getCurrentPlayer();
            
            // Test mismatched count and indices
            boolean exceptionThrown = false;
            try {
                controller.handleClaim(currentPlayer, 3, Arrays.asList(0, 1)); // count=3, indices=2
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("Count must match") : "Should mention count mismatch";
            }
            
            assert exceptionThrown : "Should throw exception for mismatched count";
            
            // Test invalid count range
            exceptionThrown = false;
            try {
                controller.handleClaim(currentPlayer, 0, Arrays.asList()); // count=0
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("between 1 and 5") : "Should mention valid count range";
            }
            
            assert exceptionThrown : "Should throw exception for invalid count";
        });
        
        // Test challenge without claim
        runTest("Challenge without active claim", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob");
            
            controller.initializeGame(2, playerNames);
            controller.startGameSession();
            
            Player currentPlayer = controller.getCurrentPlayer();
            boolean exceptionThrown = false;
            
            try {
                controller.handleChallenge(currentPlayer);
            } catch (IllegalStateException e) {
                exceptionThrown = true;
                assert e.getMessage().contains("No claim exists") : "Should mention no claim";
            }
            
            assert exceptionThrown : "Should throw exception when no claim to challenge";
        });
        
        // Test game state consistency
        runTest("Game state consistency", () -> {
            GameController controller = new GameControllerImpl();
            List<String> playerNames = Arrays.asList("Alice", "Bob");
            
            // Before initialization
            assert controller.getCurrentGameState() == null : "Game state should be null before init";
            assert !controller.isGameActive() : "Game should not be active before init";
            
            controller.initializeGame(2, playerNames);
            
            // After initialization but before start
            assert !controller.isGameActive() : "Game should not be active before start";
            
            controller.startGameSession();
            
            // After start
            assert controller.isGameActive() : "Game should be active after start";
            assert controller.getCurrentGameState() != null : "Game state should exist after start";
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
}