package view;

import model.game.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for game state display functionality.
 * Tests display formatting and state rendering methods.
 */
public class GameStateDisplayTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running Game State Display Tests...");
        System.out.println("===================================");
        
        testGameStateRendering();
        testPlayerHandDisplay();
        testClaimDisplay();
        testPlayerStatusDisplay();
        testRoundInfoDisplay();
        testGameOverDisplay();
        
        System.out.println("\n===================================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All Game State Display tests passed!");
        } else {
            System.out.println("❌ Some Game State Display tests failed!");
        }
    }
    
    private static void testGameStateRendering() {
        System.out.println("\nTesting game state rendering...");
        
        runTest("Full game state display", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            List<Player> players = createTestPlayers();
            Player currentPlayer = players.get(0);
            Claim claim = new TestClaim(players.get(1), 2);
            
            gameTerminal.displayFullGameState(players, currentPlayer, 1, 
                                            Rank.KING, claim, "Player2 made a claim");
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("LIAR'S BAR") : "Should display game title";
            assert output.contains("Round: 1") : "Should display round number";
            assert output.contains("Current Card: KING") : "Should display current rank";
            assert output.contains("Player Status:") : "Should display player status";
            assert output.contains("Current Claim:") : "Should display current claim";
            assert output.contains("Last Action:") : "Should display last action";
            
            gameTerminal.cleanup();
        });
    }
    
    private static void testPlayerHandDisplay() {
        System.out.println("\nTesting player hand display...");
        
        runTest("Display player hand", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            Hand testHand = new TestHand();
            
            gameTerminal.displayPlayerHand(testHand);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("Your Hand:") : "Should display hand header";
            assert output.contains("[1]") : "Should display card indices";
            assert output.contains("KING") : "Should display card ranks";
            
            gameTerminal.cleanup();
        });
        
        runTest("Display null hand", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.displayPlayerHand(null);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("No hand available") : "Should show error for null hand";
            
            gameTerminal.cleanup();
        });
    }
    
    private static void testClaimDisplay() {
        System.out.println("\nTesting claim display...");
        
        runTest("Display current claim", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            List<Player> players = createTestPlayers();
            Claim claim = new TestClaim(players.get(0), 3);
            
            gameTerminal.displayCurrentClaim(claim, Rank.QUEEN);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("TestPlayer1 claims 3 QUEEN") : "Should display claim details";
            
            gameTerminal.cleanup();
        });
        
        runTest("Display null claim", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.displayCurrentClaim(null, Rank.KING);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("No active claim") : "Should show message for no claim";
            
            gameTerminal.cleanup();
        });
    }
    
    private static void testPlayerStatusDisplay() {
        System.out.println("\nTesting player status display...");
        
        runTest("Display player status", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            List<Player> players = createTestPlayers();
            Player currentPlayer = players.get(1);
            
            gameTerminal.displayPlayerStatus(players, currentPlayer);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("Player Status:") : "Should display status header";
            assert output.contains("[*] TestPlayer2") : "Should mark current player";
            assert output.contains("[ ] TestPlayer1") : "Should show non-current players";
            assert output.contains("(3 cards)") : "Should show card counts";
            assert output.contains("[●●●○○]") : "Should show health bars";
            
            gameTerminal.cleanup();
        });
        
        runTest("Display empty player list", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.displayPlayerStatus(new ArrayList<>(), null);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("No players to display") : "Should show error for empty list";
            
            gameTerminal.cleanup();
        });
    }
    
    private static void testRoundInfoDisplay() {
        System.out.println("\nTesting round info display...");
        
        runTest("Display round information", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.displayRoundInfo(Rank.JACK);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("Round Information:") : "Should display round header";
            assert output.contains("Current Card Type: JACK") : "Should display current rank";
            assert output.contains("Aces count as wildcards") : "Should explain wildcard rule";
            
            gameTerminal.cleanup();
        });
    }
    
    private static void testGameOverDisplay() {
        System.out.println("\nTesting game over display...");
        
        runTest("Display game over with winner", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            List<Player> players = createTestPlayers();
            Player winner = players.get(0);
            
            gameTerminal.displayGameOver(winner);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("GAME OVER") : "Should display game over header";
            assert output.contains("TestPlayer1 WINS!") : "Should display winner";
            assert output.contains("🎉") : "Should include celebration emoji";
            
            gameTerminal.cleanup();
        });
        
        runTest("Display game over without winner", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.displayGameOver(null);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("GAME OVER") : "Should display game over header";
            assert output.contains("Game ended") : "Should show generic end message";
            
            gameTerminal.cleanup();
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
     * Creates a list of test players for testing.
     * @return List of test players
     */
    private static List<Player> createTestPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new TestPlayer("TestPlayer1"));
        players.add(new TestPlayer("TestPlayer2"));
        players.add(new TestPlayer("TestPlayer3"));
        return players;
    }
    
    /**
     * Test implementation of Player for testing purposes.
     */
    private static class TestPlayer implements Player {
        private final String id;
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
            return null;
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
            return new TestHand();
        }
        
        @Override
        public void setHand(Hand hand) {
            // Test implementation
        }
    }
    
    /**
     * Test implementation of Hand for testing purposes.
     */
    private static class TestHand implements Hand {
        @Override
        public Card getAt(int index) throws IndexOutOfBoundsException {
            if (index >= 3) { // Simulate 3 cards in hand
                throw new IndexOutOfBoundsException();
            }
            return new TestCard();
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
     * Test implementation of Card for testing purposes.
     */
    private static class TestCard implements Card {
        @Override
        public Rank getRank() {
            return Rank.KING;
        }
        
        @Override
        public boolean equals(Object obj) {
            return obj instanceof TestCard;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
    }
    
    /**
     * Test implementation of Claim for testing purposes.
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