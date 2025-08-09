package view;

import model.game.*;
import util.InputHandler;
import java.util.List;
import java.util.ArrayList;

/**
 * Terminal-based challenge system with feedback for handling player challenges.
 * Provides challenge confirmation interface, card revelation and claim verification display,
 * and logic for determining challenge outcomes.
 */
public class ChallengeView {
    private static final String BORDER = "═".repeat(60);
    private final InputHandler inputHandler;
    private final HandView handView;
    private final ActionView actionView;
    
    public ChallengeView(InputHandler inputHandler) {
        if (inputHandler == null) {
            throw new IllegalArgumentException("InputHandler cannot be null");
        }
        this.inputHandler = inputHandler;
        this.handView = new HandView();
        this.actionView = new ActionView();
    }
    
    /**
     * Prompts the player to decide whether to challenge the current claim.
     * @param challenger The player considering the challenge
     * @param currentClaim The claim being considered for challenge
     * @param currentRank The current round's card type
     * @return true if player wants to challenge, false otherwise
     */
    public boolean promptForChallenge(Player challenger, Claim currentClaim, Rank currentRank) {
        if (challenger == null || currentClaim == null || currentRank == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        displayChallengePrompt(challenger, currentClaim, currentRank);
        
        return inputHandler.getChallenge();
    }
    
    /**
     * Displays the challenge confirmation interface.
     * @param challenger The player considering the challenge
     * @param currentClaim The claim being considered
     * @param currentRank The current round's card type
     */
    private void displayChallengePrompt(Player challenger, Claim currentClaim, Rank currentRank) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("CHALLENGE DECISION", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Challenger: %-45s ║%n", challenger.getId());
        System.out.printf("║ Claimed by: %-45s ║%n", currentClaim.getPlayer().getId());
        System.out.printf("║ Claim: %d %s%-38s ║%n", 
            currentClaim.getCount(), 
            formatRank(currentRank), "");
        System.out.printf("║%s║%n", centerText("Do you believe this claim?", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Displays the card revelation and claim verification.
     * @param challengedClaim The claim that was challenged
     * @param actualCards The actual cards that were played
     * @param claimedRank The rank that was claimed
     * @return ChallengeResult containing the outcome details
     */
    public ChallengeResult displayChallengeReveal(Claim challengedClaim, List<Card> actualCards, Rank claimedRank) {
        if (challengedClaim == null || actualCards == null || claimedRank == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        // Count how many cards actually match the claimed rank (including Aces as wildcards)
        int actualMatchingCards = countMatchingCards(actualCards, claimedRank);
        int claimedCount = challengedClaim.getCount();
        
        boolean claimWasTrue = actualMatchingCards >= claimedCount;
        
        displayRevealHeader();
        displayClaimDetails(challengedClaim, claimedRank);
        displayActualCards(actualCards);
        displayCardAnalysis(actualCards, claimedRank, actualMatchingCards);
        displayChallengeOutcome(claimWasTrue, challengedClaim.getPlayer());
        
        inputHandler.waitForEnter();
        
        return new ChallengeResult(claimWasTrue, actualCards, actualMatchingCards, claimedCount);
    }
    
    /**
     * Displays the challenge result header.
     */
    private void displayRevealHeader() {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("🎭 CHALLENGE REVEAL 🎭", 60));
        System.out.println("╠" + BORDER + "╣");
    }
    
    /**
     * Displays the details of the original claim.
     * @param claim The challenged claim
     * @param claimedRank The rank that was claimed
     */
    private void displayClaimDetails(Claim claim, Rank claimedRank) {
        System.out.printf("║ Original Claim:%-43s ║%n", "");
        System.out.printf("║   Player: %-47s ║%n", claim.getPlayer().getId());
        System.out.printf("║   Claimed: %d %s%-36s ║%n", 
            claim.getCount(), formatRank(claimedRank), "");
        System.out.println("╠" + BORDER + "╣");
    }
    
    /**
     * Displays the actual cards that were played.
     * @param actualCards The cards that were actually played
     */
    private void displayActualCards(List<Card> actualCards) {
        System.out.printf("║ Actual Cards Played:%-36s ║%n", "");
        
        StringBuilder cardDisplay = new StringBuilder();
        for (int i = 0; i < actualCards.size(); i++) {
            if (i > 0) cardDisplay.append(", ");
            cardDisplay.append(formatRank(actualCards.get(i).getRank()));
        }
        
        // Split long card lists across multiple lines if needed
        String cardString = cardDisplay.toString();
        if (cardString.length() <= 50) {
            System.out.printf("║   %s%-50s ║%n", cardString, 
                " ".repeat(Math.max(0, 50 - cardString.length())));
        } else {
            // Split into multiple lines
            String[] words = cardString.split(", ");
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                if (currentLine.length() + word.length() + 2 > 50) {
                    System.out.printf("║   %s%-50s ║%n", currentLine.toString(),
                        " ".repeat(Math.max(0, 50 - currentLine.length())));
                    currentLine = new StringBuilder(word);
                } else {
                    if (currentLine.length() > 0) currentLine.append(", ");
                    currentLine.append(word);
                }
            }
            
            if (currentLine.length() > 0) {
                System.out.printf("║   %s%-50s ║%n", currentLine.toString(),
                    " ".repeat(Math.max(0, 50 - currentLine.length())));
            }
        }
        
        System.out.println("╠" + BORDER + "╣");
    }
    
    /**
     * Displays analysis of the cards vs the claim.
     * @param actualCards The actual cards played
     * @param claimedRank The rank that was claimed
     * @param matchingCount Number of cards that match the claim
     */
    private void displayCardAnalysis(List<Card> actualCards, Rank claimedRank, int matchingCount) {
        System.out.printf("║ Card Analysis:%-44s ║%n", "");
        System.out.printf("║   Total cards played: %d%-32s ║%n", actualCards.size(), "");
        System.out.printf("║   %s found: %d%-35s ║%n", 
            formatRank(claimedRank), matchingCount, "");
        
        // Show breakdown of card types
        int[] rankCounts = new int[Rank.values().length];
        for (Card card : actualCards) {
            rankCounts[card.getRank().ordinal()]++;
        }
        
        for (Rank rank : Rank.values()) {
            int count = rankCounts[rank.ordinal()];
            if (count > 0) {
                String wildcard = (rank == Rank.ACE && claimedRank != Rank.ACE) ? " (wildcards)" : "";
                System.out.printf("║     %s: %d%s%-30s ║%n", 
                    formatRank(rank), count, wildcard,
                    " ".repeat(Math.max(0, 30 - wildcard.length())));
            }
        }
        
        System.out.println("╠" + BORDER + "╣");
    }
    
    /**
     * Displays the final challenge outcome.
     * @param claimWasTrue Whether the claim was truthful
     * @param claimedPlayer The player who made the original claim
     */
    private void displayChallengeOutcome(boolean claimWasTrue, Player claimedPlayer) {
        if (claimWasTrue) {
            System.out.printf("║%s║%n", centerText("✅ CLAIM WAS TRUE!", 60));
            System.out.printf("║%s║%n", centerText("Challenger must spin the revolver", 60));
        } else {
            System.out.printf("║%s║%n", centerText("❌ CLAIM WAS FALSE!", 60));
            System.out.printf("║%s║%n", centerText(claimedPlayer.getId() + " must spin the revolver", 60));
        }
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Displays a challenge action in progress.
     * @param challenger The player making the challenge
     * @param challengedClaim The claim being challenged
     */
    public void displayChallengeAction(Player challenger, Claim challengedClaim) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("⚔️ CHALLENGE ISSUED ⚔️", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Challenger: %-45s ║%n", challenger.getId());
        System.out.printf("║ Challenged: %-45s ║%n", challengedClaim.getPlayer().getId());
        System.out.printf("║ Disputed Claim: %d cards%-32s ║%n", challengedClaim.getCount(), "");
        System.out.printf("║%s║%n", centerText("Revealing cards...", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Displays waiting message while challenge is being processed.
     * @param message The message to display
     */
    public void displayChallengeProcessing(String message) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("PROCESSING CHALLENGE", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText(message, 60));
        System.out.printf("║%s║%n", centerText("⏳ Please wait...", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Displays an error message related to challenging.
     * @param errorMessage The error message to display
     */
    public void displayChallengeError(String errorMessage) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("CHALLENGE ERROR", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ ❌ %-55s ║%n", errorMessage);
        System.out.printf("║%s║%n", centerText("Please try again", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Counts how many cards match the claimed rank (including Aces as wildcards).
     * @param cards The cards to check
     * @param claimedRank The rank that was claimed
     * @return Number of matching cards
     */
    private int countMatchingCards(List<Card> cards, Rank claimedRank) {
        int count = 0;
        for (Card card : cards) {
            if (card.getRank() == claimedRank || card.getRank() == Rank.ACE) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Formats a rank for display.
     * @param rank The rank to format
     * @return Formatted rank string
     */
    private String formatRank(Rank rank) {
        if (rank == null) return "Unknown";
        
        switch (rank) {
            case ACE: return "Aces";
            case KING: return "Kings";
            case QUEEN: return "Queens";
            case JACK: return "Jacks";
            default: return rank.toString();
        }
    }
    
    /**
     * Centers text within a given width.
     * @param text Text to center
     * @param width Total width
     * @return Centered text
     */
    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        
        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        
        while (sb.length() < width) {
            sb.append(" ");
        }
        
        return sb.toString();
    }
    
    /**
     * Data class to hold challenge result information.
     */
    public static class ChallengeResult {
        private final boolean claimWasTrue;
        private final List<Card> actualCards;
        private final int actualMatchingCards;
        private final int claimedCount;
        
        public ChallengeResult(boolean claimWasTrue, List<Card> actualCards, 
                              int actualMatchingCards, int claimedCount) {
            this.claimWasTrue = claimWasTrue;
            this.actualCards = new ArrayList<>(actualCards);
            this.actualMatchingCards = actualMatchingCards;
            this.claimedCount = claimedCount;
        }
        
        public boolean wasClaimTrue() {
            return claimWasTrue;
        }
        
        public List<Card> getActualCards() {
            return new ArrayList<>(actualCards);
        }
        
        public int getActualMatchingCards() {
            return actualMatchingCards;
        }
        
        public int getClaimedCount() {
            return claimedCount;
        }
        
        @Override
        public String toString() {
            return String.format("ChallengeResult{claimTrue=%s, actualMatching=%d, claimed=%d}", 
                claimWasTrue, actualMatchingCards, claimedCount);
        }
    }
}