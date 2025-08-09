package view;

import model.game.Player;
import model.game.Hand;
import model.game.Claim;
import model.game.Rank;
import model.game.Card;
import model.exceptions.NoSuchCardException;
import util.InputHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Test class for RevolverView terminal interface.
 * Tests revolver visualization, outcomes, and player elimination displays.
 */
public class RevolverViewTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running RevolverView Tests...");
        System.out.println("=============================");
        
        testDisplayRevolverSpin();
        testDisplaySpinningAnimation();
        testDisplayChamberVisualization();
        testDisplayTriggerPull();
        testDisplayRevolverOutcomeEliminated();
        testDisplayRevolverOutcomeSurvived();
        testDisplayPlayerElimination();
        testDisplayRevolverStats();
        testLongPlayerName();
        
        System.out.println("\n=============================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
        }
    }
    
    private static void testDisplayRevolverSpin() {
        runTest("Display revolver spin", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                TestPlayer player = new TestPlayer("Alice");
                
                revolverView.displayRevolverSpin(player);
                
                String output = outputStream.toString();
                assert output.contains("🔫 REVOLVER TIME 🔫") : "Should contain revolver title";
                assert output.contains("Alice must spin the revolver") : "Should contain player name";
                assert output.contains("Russian Roulette - 1 bullet, 6 chambers") : "Should contain game description";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testDisplaySpinningAnimation() {
        runTest("Display spinning animation", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                
                revolverView.displaySpinningAnimation(3);
                
                String output = outputStream.toString();
                assert output.contains("SPINNING CYLINDER...") : "Should contain spinning message";
                assert output.contains("REVOLVER CHAMBERS") : "Should contain chamber visualization";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testDisplayChamberVisualization() {
        runTest("Display chamber visualization", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                
                revolverView.displayChamberVisualization(4, false);
                
                String output = outputStream.toString();
                assert output.contains("REVOLVER CHAMBERS") : "Should contain chamber title";
                assert output.contains("⚪") : "Should contain chamber symbols";
                assert output.contains("🔫") : "Should contain revolver symbol";
                assert !output.contains("💥 Bullet was in chamber") : "Should not reveal bullet location";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testDisplayTriggerPull() {
        runTest("Display trigger pull", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                TestPlayer player = new TestPlayer("Bob");
                
                revolverView.displayTriggerPull(player);
                
                String output = outputStream.toString();
                assert output.contains("PULLING TRIGGER") : "Should contain trigger pull title";
                assert output.contains("Bob aims the revolver") : "Should contain player name";
                assert output.contains("🔫 → 😰") : "Should contain trigger pull visualization";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testDisplayRevolverOutcomeEliminated() {
        runTest("Display revolver outcome eliminated", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                TestPlayer player = new TestPlayer("Charlie");
                
                revolverView.displayRevolverOutcome(player, true, 5);
                
                String output = outputStream.toString();
                assert output.contains("💥 BANG! 💥") : "Should contain bang message";
                assert output.contains("The bullet was in chamber 5") : "Should show chamber number";
                assert output.contains("Charlie has been eliminated!") : "Should show elimination";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testDisplayRevolverOutcomeSurvived() {
        runTest("Display revolver outcome survived", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                TestPlayer player = new TestPlayer("Diana");
                
                revolverView.displayRevolverOutcome(player, false, 2);
                
                String output = outputStream.toString();
                assert output.contains("🔫 *CLICK* 🔫") : "Should contain click message";
                assert output.contains("Chamber 2 was empty!") : "Should show empty chamber";
                assert output.contains("Diana survives!") : "Should show survival";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testDisplayPlayerElimination() {
        runTest("Display player elimination", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                TestPlayer player = new TestPlayer("Eve");
                
                revolverView.displayPlayerElimination(player, 2);
                
                String output = outputStream.toString();
                assert output.contains("PLAYER ELIMINATED") : "Should contain elimination title";
                assert output.contains("Eliminated: Eve") : "Should show eliminated player";
                assert output.contains("Players remaining: 2") : "Should show remaining count";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testDisplayRevolverStats() {
        runTest("Display revolver stats", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                TestPlayer player = new TestPlayer("Frank");
                
                revolverView.displayRevolverStats(player, 3, 2);
                
                String output = outputStream.toString();
                assert output.contains("REVOLVER STATUS") : "Should contain status title";
                assert output.contains("Player: Frank") : "Should show player name";
                assert output.contains("Current chamber: 3") : "Should show chamber position";
                assert output.contains("Shots until bullet: 2") : "Should show shots remaining";
                
                inputHandler.close();
            } finally {
                System.setOut(originalOut);
                System.setIn(System.in);
            }
        });
    }
    
    private static void testLongPlayerName() {
        runTest("Long player name handling", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            System.setIn(new ByteArrayInputStream("\n".getBytes()));
            
            try {
                InputHandler inputHandler = new InputHandler();
                RevolverView revolverView = new RevolverView(inputHandler);
                TestPlayer player = new TestPlayer("VeryLongPlayerNameThatMightCauseIssues");
                
                revolverView.displayRevolverSpin(player);
                
                String output = outputStream.toString();
                assert output.contains("VeryLongPlayerNameThatMightCauseIssues must spin the revolver") : 
                    "Should handle long player names";
                
                inputHandler.close();
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
     * Test implementation of Player interface for testing purposes.
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
        public Claim claim(Rank rank, int count, List<Card> droppedCards) throws NoSuchCardException {
            return new TestClaim(this, count);
        }
        
        @Override
        public void challengeClaim(Claim claim) {
            // Test implementation
        }
        
        @Override
        public boolean shoot() {
            alive = false;
            return true;
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