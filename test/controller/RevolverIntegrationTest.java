package controller;

import model.game.*;
import model.exceptions.GameFullException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Arrays;

/**
 * Integration test for revolver mechanics in GameControllerImpl.
 * Tests the complete revolver flow including visualization and player elimination.
 */
public class RevolverIntegrationTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running Revolver Integration Tests...");
        System.out.println("====================================");
        
        testRevolverSpinBasicFunctionality();
        testRevolverSpinWithElimination();
        testRevolverSpinWithDeadPlayer();
        testRevolverDisplaysCorrectInterface();
        
        System.out.println("\n====================================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
        }
    }
    
    private static void testRevolverSpinBasicFunctionality() {
        runTest("Revolver spin basic functionality", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            String input = "\n\n\n\n\n"; // Multiple Enter presses
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            try {
                GameControllerImpl controller = new GameControllerImpl();
                List<String> playerNames = Arrays.asList("Alice", "Bob");
                controller.initializeGame(2, playerNames);
                
                TestPlayer player = new TestPlayer("Alice");
                
                boolean eliminated = controller.handleRevolverSpin(player);
                
                String output = outputStream.toString();
                
                // Verify revolver interface was displayed
                assert output.contains("🔫 REVOLVER TIME 🔫") : "Should display revolver title";
                assert output.contains("Alice must spin the revolver") : "Should show player name";
                assert output.contains("Russian Roulette") : "Should show game description";
                assert output.contains("PULLING TRIGGER") : "Should show trigger pull interface";
                
                // The result depends on the random outcome, but we should see outcome display
                assert output.contains("CLICK") || output.contains("BANG") : "Should show outcome";
                
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testRevolverSpinWithElimination() {
        runTest("Revolver spin with elimination", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            String input = "\n\n\n\n\n"; // Multiple Enter presses
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            try {
                GameControllerImpl controller = new GameControllerImpl();
                List<String> playerNames = Arrays.asList("Charlie", "Diana");
                controller.initializeGame(2, playerNames);
                
                // Create a test player that will be eliminated
                TestPlayer player = new TestPlayer("Charlie") {
                    @Override
                    public boolean shoot() {
                        setAlive(false);
                        return true; // Force elimination
                    }
                };
                
                boolean eliminated = controller.handleRevolverSpin(player);
                
                assert eliminated : "Player should be eliminated";
                assert !player.isAlive() : "Player should not be alive after elimination";
                
                String output = outputStream.toString();
                
                // Verify elimination display
                assert output.contains("💥 BANG! 💥") : "Should show bang message";
                assert output.contains("Charlie has been eliminated!") : "Should show elimination message";
                assert output.contains("PLAYER ELIMINATED") : "Should show elimination status";
                
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testRevolverSpinWithDeadPlayer() {
        runTest("Revolver spin with dead player", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            String input = "\n\n\n\n\n"; // Multiple Enter presses
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            try {
                GameControllerImpl controller = new GameControllerImpl();
                List<String> playerNames = Arrays.asList("Eve", "Frank");
                controller.initializeGame(2, playerNames);
                
                TestPlayer deadPlayer = new TestPlayer("Eve");
                deadPlayer.setAlive(false);
                
                // Should throw exception when trying to spin revolver with dead player
                try {
                    controller.handleRevolverSpin(deadPlayer);
                    assert false : "Should throw exception for dead player";
                } catch (IllegalArgumentException e) {
                    // Expected
                }
                
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testRevolverDisplaysCorrectInterface() {
        runTest("Revolver displays correct interface", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            String input = "\n\n\n\n\n"; // Multiple Enter presses
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            try {
                GameControllerImpl controller = new GameControllerImpl();
                List<String> playerNames = Arrays.asList("Jack", "Kate");
                controller.initializeGame(2, playerNames);
                
                TestPlayer player = new TestPlayer("Jack");
                
                controller.handleRevolverSpin(player);
                
                String output = outputStream.toString();
                
                // Should display chamber visualization
                assert output.contains("REVOLVER CHAMBERS") : "Should show chamber visualization";
                assert output.contains("⚪") : "Should show chamber symbols";
                assert output.contains("🔫") : "Should show revolver symbol";
                
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
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
     * Test implementation of Player interface for integration testing.
     */
    private static class TestPlayer implements Player {
        private final String id;
        private Hand hand;
        private boolean alive = true;
        
        public TestPlayer(String id) {
            this.id = id;
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
            // Random outcome for testing
            boolean eliminated = Math.random() < 0.5;
            if (eliminated) {
                alive = false;
            }
            return eliminated;
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
        
        public void setAlive(boolean alive) {
            this.alive = alive;
        }
    }
    
    /**
     * Test implementation of Claim interface.
     */
    private static class TestClaim implements Claim {
        private final Player player;
        private final int count;
        
        public TestClaim(Player player, int count) {
            this.player = player;
            this.count = count;
        }
        
        @Override
        public Player getPlayer() {
            return player;
        }
        
        @Override
        public int getCount() {
            return count;
        }
    }
}