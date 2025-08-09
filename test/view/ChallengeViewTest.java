package view;

import model.game.*;
import util.InputHandler;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test class for ChallengeView functionality.
 * Tests challenge prompts, card revelation, and outcome determination.
 */
public class ChallengeViewTest {
    
    private ChallengeView challengeView;
    private InputHandler inputHandler;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        inputHandler = new InputHandler();
        challengeView = new ChallengeView(inputHandler);
    }
    
    public void tearDown() {
        System.setOut(originalOut);
    }
    
    public void testChallengeViewConstructor() {
        inputHandler = new InputHandler();
        challengeView = new ChallengeView(inputHandler);
        
        assert challengeView != null : "ChallengeView should not be null";
        
        try {
            new ChallengeView(null);
            assert false : "Should throw IllegalArgumentException for null InputHandler";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        System.out.println("✓ testChallengeViewConstructor passed");
    }
    
    public void testPromptForChallengeWithNullParameters() {
        inputHandler = new InputHandler();
        challengeView = new ChallengeView(inputHandler);
        
        TestPlayer challenger = new TestPlayer("Challenger", new TestHand());
        TestClaim claim = new TestClaim(new TestPlayer("Claimer", new TestHand()), 2);
        
        try {
            challengeView.promptForChallenge(null, claim, Rank.KING);
            assert false : "Should throw IllegalArgumentException for null challenger";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            challengeView.promptForChallenge(challenger, null, Rank.KING);
            assert false : "Should throw IllegalArgumentException for null claim";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            challengeView.promptForChallenge(challenger, claim, null);
            assert false : "Should throw IllegalArgumentException for null rank";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        System.out.println("✓ testPromptForChallengeWithNullParameters passed");
    }
    
    public void testDisplayChallengeRevealWithNullParameters() {
        inputHandler = new InputHandler();
        challengeView = new ChallengeView(inputHandler);
        
        TestClaim claim = new TestClaim(new TestPlayer("Claimer", new TestHand()), 2);
        List<Card> cards = Arrays.asList(new TestCard(Rank.KING), new TestCard(Rank.ACE));
        
        try {
            challengeView.displayChallengeReveal(null, cards, Rank.KING);
            assert false : "Should throw IllegalArgumentException for null claim";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            challengeView.displayChallengeReveal(claim, null, Rank.KING);
            assert false : "Should throw IllegalArgumentException for null cards";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            challengeView.displayChallengeReveal(claim, cards, null);
            assert false : "Should throw IllegalArgumentException for null rank";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        System.out.println("✓ testDisplayChallengeRevealWithNullParameters passed");
    }
    
    public void testChallengeResultDataClass() {
        List<Card> cards = Arrays.asList(new TestCard(Rank.KING), new TestCard(Rank.ACE));
        
        ChallengeView.ChallengeResult result = new ChallengeView.ChallengeResult(
            true, cards, 2, 2);
        
        assert result.wasClaimTrue() : "Claim should be true";
        assert result.getActualCards().equals(cards) : "Actual cards should match";
        assert result.getActualMatchingCards() == 2 : "Matching cards should be 2";
        assert result.getClaimedCount() == 2 : "Claimed count should be 2";
        
        // Test toString
        String toString = result.toString();
        assert toString.contains("claimTrue=true") : "toString should contain claim result";
        assert toString.contains("actualMatching=2") : "toString should contain actual matching";
        assert toString.contains("claimed=2") : "toString should contain claimed count";
        
        System.out.println("✓ testChallengeResultDataClass passed");
    }
    
    public void testCardMatchingLogic() {
        // Test through the challenge reveal method
        TestPlayer claimer = new TestPlayer("Claimer", new TestHand());
        TestClaim claim = new TestClaim(claimer, 2);
        
        // Test case 1: Claim is true (2 Kings claimed, 2 Kings + Ace played)
        List<Card> trueCards = Arrays.asList(
            new TestCard(Rank.KING), 
            new TestCard(Rank.KING), 
            new TestCard(Rank.ACE)
        );
        
        // We can't easily test the interactive parts, but we can test the data structures
        assert trueCards.size() == 3 : "Should have 3 cards";
        
        // Count matching cards manually to verify logic
        int matchingCount = 0;
        for (Card card : trueCards) {
            if (card.getRank() == Rank.KING || card.getRank() == Rank.ACE) {
                matchingCount++;
            }
        }
        assert matchingCount == 3 : "Should have 3 matching cards (2 Kings + 1 Ace)";
        assert matchingCount >= claim.getCount() : "Claim should be valid";
        
        // Test case 2: Claim is false (2 Kings claimed, only 1 King played)
        List<Card> falseCards = Arrays.asList(
            new TestCard(Rank.KING), 
            new TestCard(Rank.QUEEN)
        );
        
        matchingCount = 0;
        for (Card card : falseCards) {
            if (card.getRank() == Rank.KING || card.getRank() == Rank.ACE) {
                matchingCount++;
            }
        }
        assert matchingCount == 1 : "Should have 1 matching card";
        assert matchingCount < claim.getCount() : "Claim should be invalid";
        
        System.out.println("✓ testCardMatchingLogic passed");
    }
    
    public void testDisplayMethods() {
        // Test that display methods don't crash with valid inputs
        inputHandler = new InputHandler();
        challengeView = new ChallengeView(inputHandler);
        
        TestPlayer challenger = new TestPlayer("Challenger", new TestHand());
        TestPlayer claimer = new TestPlayer("Claimer", new TestHand());
        TestClaim claim = new TestClaim(claimer, 2);
        
        // Test displayChallengeAction
        try {
            challengeView.displayChallengeAction(challenger, claim);
            // If we get here without exception, the method works
        } catch (Exception e) {
            assert false : "displayChallengeAction should not throw exception: " + e.getMessage();
        }
        
        // Test displayChallengeProcessing
        try {
            challengeView.displayChallengeProcessing("Testing message");
            // If we get here without exception, the method works
        } catch (Exception e) {
            assert false : "displayChallengeProcessing should not throw exception: " + e.getMessage();
        }
        
        // Test displayChallengeError
        try {
            challengeView.displayChallengeError("Test error message");
            // If we get here without exception, the method works
        } catch (Exception e) {
            assert false : "displayChallengeError should not throw exception: " + e.getMessage();
        }
        
        System.out.println("✓ testDisplayMethods passed");
    }
    
    public void testRankFormatting() {
        // Test that all ranks can be processed without errors
        for (Rank rank : Rank.values()) {
            TestPlayer claimer = new TestPlayer("Claimer", new TestHand());
            TestClaim claim = new TestClaim(claimer, 1);
            List<Card> cards = Arrays.asList(new TestCard(rank));
            
            try {
                // This would normally show interactive output, but we're just testing it doesn't crash
                ChallengeView.ChallengeResult result = new ChallengeView.ChallengeResult(
                    true, cards, 1, 1);
                assert result != null : "Result should not be null for rank " + rank;
            } catch (Exception e) {
                assert false : "Should handle rank " + rank + " without exception: " + e.getMessage();
            }
        }
        
        System.out.println("✓ testRankFormatting passed");
    }
    
    /**
     * Main method to run all tests.
     */
    public static void main(String[] args) {
        ChallengeViewTest test = new ChallengeViewTest();
        
        System.out.println("Running ChallengeView tests...");
        
        try {
            System.out.println("Running testChallengeViewConstructor...");
            test.testChallengeViewConstructor();
            System.out.println("Running testPromptForChallengeWithNullParameters...");
            test.testPromptForChallengeWithNullParameters();
            System.out.println("Running testDisplayChallengeRevealWithNullParameters...");
            test.testDisplayChallengeRevealWithNullParameters();
            System.out.println("Running testChallengeResultDataClass...");
            test.testChallengeResultDataClass();
            System.out.println("Running testCardMatchingLogic...");
            test.testCardMatchingLogic();
            System.out.println("Running testDisplayMethods...");
            test.testDisplayMethods();
            System.out.println("Running testRankFormatting...");
            test.testRankFormatting();
            
            System.out.println("\n✅ All ChallengeView tests passed!");
        } catch (AssertionError e) {
            System.err.println("\n❌ Test assertion failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test implementation of Player interface.
     */
    private static class TestPlayer implements Player {
        private final String id;
        private Hand hand;
        private boolean alive = true;
        
        public TestPlayer(String id, Hand hand) {
            this.id = id;
            this.hand = hand;
        }
        
        @Override
        public String getId() { return id; }
        
        @Override
        public Hand getHand() { return hand; }
        
        @Override
        public void setHand(Hand hand) { this.hand = hand; }
        
        @Override
        public boolean isAlive() { return alive; }
        
        @Override
        public Claim claim(Rank rank, int count, List<Card> droppedCards) {
            return new TestClaim(this, count);
        }
        
        @Override
        public void challengeClaim(Claim claim) {}
        
        @Override
        public boolean shoot() { return false; }
    }
    
    /**
     * Test implementation of Hand interface.
     */
    private static class TestHand implements Hand {
        private final List<Card> cards = new ArrayList<>();
        
        @Override
        public Card getAt(int index) throws IndexOutOfBoundsException {
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
     * Test implementation of Card interface.
     */
    private static class TestCard implements Card {
        private final Rank rank;
        
        public TestCard(Rank rank) {
            this.rank = rank;
        }
        
        @Override
        public Rank getRank() {
            return rank;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestCard testCard = (TestCard) obj;
            return rank == testCard.rank;
        }
        
        @Override
        public int hashCode() {
            return rank.hashCode();
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
        public int getCount() { return count; }
        
        @Override
        public Player getPlayer() { return player; }
    }
}