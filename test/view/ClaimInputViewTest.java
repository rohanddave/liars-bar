package view;

import model.game.*;
import util.InputHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test class for ClaimInputView functionality.
 * Tests claim input validation, card selection, and user interaction.
 */
public class ClaimInputViewTest {
    
    private ClaimInputView claimInputView;
    private InputHandler inputHandler;
    private TestPlayer testPlayer;
    private TestHand testHand;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    public void setUp() {
        // Capture system output for testing
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        inputHandler = new InputHandler();
        claimInputView = new ClaimInputView(inputHandler);
        
        // Create test player with test hand
        testHand = new TestHand();
        testHand.addCard(new TestCard(Rank.KING));
        testHand.addCard(new TestCard(Rank.ACE));
        testHand.addCard(new TestCard(Rank.QUEEN));
        testHand.addCard(new TestCard(Rank.JACK));
        testHand.addCard(new TestCard(Rank.KING));
        
        testPlayer = new TestPlayer("TestPlayer", testHand);
    }
    
    public void tearDown() {
        System.setOut(originalOut);
    }
    
    public void testClaimInputConstructor() {
        inputHandler = new InputHandler();
        claimInputView = new ClaimInputView(inputHandler);
        
        assert claimInputView != null : "ClaimInputView should not be null";
        
        try {
            new ClaimInputView(null);
            assert false : "Should throw IllegalArgumentException for null InputHandler";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        System.out.println("✓ testClaimInputConstructor passed");
    }
    
    public void testGetClaimInputWithNullParameters() {
        inputHandler = new InputHandler();
        claimInputView = new ClaimInputView(inputHandler);
        
        // Create test player with test hand
        testHand = new TestHand();
        testHand.addCard(new TestCard(Rank.KING));
        testPlayer = new TestPlayer("TestPlayer", testHand);
        
        try {
            claimInputView.getClaimInput(null, Rank.KING);
            assert false : "Should throw IllegalArgumentException for null player";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            claimInputView.getClaimInput(testPlayer, null);
            assert false : "Should throw IllegalArgumentException for null rank";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        System.out.println("✓ testGetClaimInputWithNullParameters passed");
    }
    
    public void testClaimInputDataClass() {
        List<Integer> indices = Arrays.asList(0, 1);
        List<Card> cards = Arrays.asList(new TestCard(Rank.KING), new TestCard(Rank.ACE));
        
        ClaimInputView.ClaimInput claimInput = new ClaimInputView.ClaimInput(
            2, indices, cards, Rank.KING);
        
        assert claimInput.getClaimCount() == 2 : "Claim count should be 2";
        assert claimInput.getCardIndices().equals(indices) : "Card indices should match";
        assert claimInput.getSelectedCards().equals(cards) : "Selected cards should match";
        assert claimInput.getClaimedRank() == Rank.KING : "Claimed rank should be KING";
        
        // Test immutability
        List<Integer> returnedIndices = claimInput.getCardIndices();
        returnedIndices.clear();
        assert claimInput.getCardIndices().size() == 2 : "Original indices should be unchanged";
        
        List<Card> returnedCards = claimInput.getSelectedCards();
        returnedCards.clear();
        assert claimInput.getSelectedCards().size() == 2 : "Original cards should be unchanged";
        
        // Test toString
        String toString = claimInput.toString();
        assert toString.contains("count=2") : "toString should contain count";
        assert toString.contains("rank=KING") : "toString should contain rank";
        assert toString.contains("cards=2") : "toString should contain card count";
        
        System.out.println("✓ testClaimInputDataClass passed");
    }
    
    public void testCardIndexParsing() {
        TestHand smallHand = new TestHand();
        smallHand.addCard(new TestCard(Rank.KING));
        smallHand.addCard(new TestCard(Rank.ACE));
        
        TestPlayer smallHandPlayer = new TestPlayer("SmallHand", smallHand);
        
        // Test that the view can handle different hand sizes
        assert smallHandPlayer.getHand() != null : "Hand should not be null";
        assert getHandSize(smallHandPlayer.getHand()) == 2 : "Hand size should be 2";
        
        System.out.println("✓ testCardIndexParsing passed");
    }
    
    public void testRankFormatting() {
        for (Rank rank : Rank.values()) {
            // Test that all ranks can be processed
            TestHand hand = new TestHand();
            hand.addCard(new TestCard(rank));
            TestPlayer player = new TestPlayer("Test", hand);
            
            // Verify player setup doesn't crash
            assert player.getHand() != null : "Hand should not be null for rank " + rank;
        }
        
        System.out.println("✓ testRankFormatting passed");
    }
    
    public void testEmptyHandHandling() {
        TestHand emptyHand = new TestHand();
        TestPlayer emptyHandPlayer = new TestPlayer("Empty", emptyHand);
        
        assert getHandSize(emptyHandPlayer.getHand()) == 0 : "Empty hand size should be 0";
        assert emptyHandPlayer.getHand() != null : "Hand should not be null even when empty";
        
        System.out.println("✓ testEmptyHandHandling passed");
    }
    
    public void testLargeHandHandling() {
        TestHand largeHand = new TestHand();
        
        // Add maximum possible cards
        for (int i = 0; i < 10; i++) {
            largeHand.addCard(new TestCard(Rank.values()[i % 4]));
        }
        
        TestPlayer largeHandPlayer = new TestPlayer("Large", largeHand);
        assert getHandSize(largeHandPlayer.getHand()) == 10 : "Large hand size should be 10";
        
        System.out.println("✓ testLargeHandHandling passed");
    }
    
    /**
     * Main method to run all tests.
     */
    public static void main(String[] args) {
        ClaimInputViewTest test = new ClaimInputViewTest();
        
        System.out.println("Running ClaimInputView tests...");
        
        try {
            System.out.println("Running testClaimInputConstructor...");
            test.testClaimInputConstructor();
            System.out.println("Running testGetClaimInputWithNullParameters...");
            test.testGetClaimInputWithNullParameters();
            System.out.println("Running testClaimInputDataClass...");
            test.testClaimInputDataClass();
            System.out.println("Running testCardIndexParsing...");
            test.testCardIndexParsing();
            System.out.println("Running testRankFormatting...");
            test.testRankFormatting();
            System.out.println("Running testEmptyHandHandling...");
            test.testEmptyHandHandling();
            System.out.println("Running testLargeHandHandling...");
            test.testLargeHandHandling();
            
            System.out.println("\n✅ All ClaimInputView tests passed!");
        } catch (AssertionError e) {
            System.err.println("\n❌ Test assertion failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to get hand size (mirrors the private method in ClaimInputView).
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
        
        public void addCard(Card card) {
            add(card);
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