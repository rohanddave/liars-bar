package view;

import model.game.*;
import util.InputHandler;
import java.util.List;
import java.util.ArrayList;

/**
 * Terminal-based claim input system for handling player claims.
 * Provides card selection interface using indices, claim count validation,
 * and user prompts for making claims with current round's card type.
 */
public class ClaimInputView {
    private static final String BORDER = "═".repeat(60);
    private final InputHandler inputHandler;
    private final HandView handView;
    private final ActionView actionView;
    
    public ClaimInputView(InputHandler inputHandler) {
        if (inputHandler == null) {
            throw new IllegalArgumentException("InputHandler cannot be null");
        }
        this.inputHandler = inputHandler;
        this.handView = new HandView();
        this.actionView = new ActionView();
    }
    
    /**
     * Handles the complete claim input process for a player.
     * @param player The player making the claim
     * @param currentRank The current round's card type
     * @return ClaimInput containing the claim details, or null if cancelled
     */
    public ClaimInput getClaimInput(Player player, Rank currentRank) {
        if (player == null || currentRank == null) {
            throw new IllegalArgumentException("Player and current rank cannot be null");
        }
        
        // Display player's hand
        handView.displayPlayerHand(player, true);
        
        // Get claim count
        int claimCount = promptForClaimCount(currentRank, getHandSize(player.getHand()));
        if (claimCount == -1) {
            return null; // Cancelled
        }
        
        // Get card selection
        List<Integer> cardIndices = promptForCardSelection(player, claimCount);
        if (cardIndices == null || cardIndices.isEmpty()) {
            return null; // Cancelled
        }
        
        // Validate and convert indices to cards
        List<Card> selectedCards = validateAndGetCards(player.getHand(), cardIndices);
        if (selectedCards == null) {
            return null; // Invalid selection
        }
        
        // Confirm the claim
        if (!confirmClaim(player, claimCount, currentRank, selectedCards)) {
            return null; // Not confirmed
        }
        
        return new ClaimInput(claimCount, cardIndices, selectedCards, currentRank);
    }
    
    /**
     * Prompts the player for the number of cards they want to claim.
     * @param currentRank The current round's card type
     * @param maxCards Maximum number of cards player can claim (hand size)
     * @return Claim count, or -1 if cancelled
     */
    private int promptForClaimCount(Rank currentRank, int maxCards) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("MAKE YOUR CLAIM", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Current round: %s%-40s ║%n", formatRank(currentRank), "");
        System.out.printf("║ You must claim cards of this type%-23s ║%n", "");
        System.out.printf("║ (Aces count as wildcards)%-31s ║%n", "");
        System.out.printf("║ Maximum cards you can claim: %d%-25s ║%n", maxCards, "");
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        while (true) {
            try {
                System.out.printf("How many %s do you claim? (1-%d, or 0 to cancel): ", 
                    formatRank(currentRank), maxCards);
                
                String input = System.console() != null ? 
                    System.console().readLine() : 
                    new java.util.Scanner(System.in).nextLine();
                
                if (input == null || input.trim().isEmpty()) {
                    System.out.println("Please enter a number.");
                    continue;
                }
                
                input = input.trim();
                if (input.equals("0")) {
                    return -1; // Cancelled
                }
                
                int count = Integer.parseInt(input);
                if (count >= 1 && count <= maxCards) {
                    return count;
                }
                
                System.out.printf("Please enter a number between 1 and %d, or 0 to cancel.%n", maxCards);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Prompts the player to select cards from their hand.
     * @param player The player selecting cards
     * @param claimCount Number of cards to select
     * @return List of card indices (0-based), or null if cancelled
     */
    private List<Integer> promptForCardSelection(Player player, int claimCount) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("SELECT CARDS", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Select %d cards from your hand%-28s ║%n", claimCount, "");
        System.out.printf("║ Enter card numbers separated by spaces%-18s ║%n", "");
        System.out.printf("║ Example: 1 3 5%-44s ║%n", "");
        System.out.printf("║ Enter 0 to cancel%-42s ║%n", "");
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        // Display hand with indices
        displayHandWithIndices(player.getHand());
        
        while (true) {
            System.out.print("Enter card numbers: ");
            String input = System.console() != null ? 
                System.console().readLine() : 
                new java.util.Scanner(System.in).nextLine();
            
            if (input == null || input.trim().isEmpty()) {
                System.out.println("Please enter card numbers or 0 to cancel.");
                continue;
            }
            
            input = input.trim();
            if (input.equals("0")) {
                return null; // Cancelled
            }
            
            List<Integer> indices = parseCardIndices(input, claimCount, getHandSize(player.getHand()));
            if (indices != null) {
                return indices;
            }
            // Error message already displayed by parseCardIndices
        }
    }
    
    /**
     * Parses and validates card indices from user input.
     * @param input User input string
     * @param expectedCount Expected number of indices
     * @param handSize Size of player's hand for validation
     * @return List of valid 0-based indices, or null if invalid
     */
    private List<Integer> parseCardIndices(String input, int expectedCount, int handSize) {
        try {
            String[] parts = input.split("\\s+");
            
            if (parts.length != expectedCount) {
                System.out.printf("Please enter exactly %d card numbers.%n", expectedCount);
                return null;
            }
            
            List<Integer> indices = new ArrayList<>();
            for (String part : parts) {
                int index = Integer.parseInt(part);
                
                // Convert to 0-based index
                int zeroBasedIndex = index - 1;
                
                if (zeroBasedIndex < 0 || zeroBasedIndex >= handSize) {
                    System.out.printf("Card number %d is not valid. Must be between 1 and %d.%n", 
                        index, handSize);
                    return null;
                }
                
                if (indices.contains(zeroBasedIndex)) {
                    System.out.printf("Duplicate card number %d. Please enter unique numbers.%n", index);
                    return null;
                }
                
                indices.add(zeroBasedIndex);
            }
            
            return indices;
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers separated by spaces.");
            return null;
        }
    }
    
    /**
     * Validates card indices and retrieves the actual cards.
     * @param hand Player's hand
     * @param indices List of card indices
     * @return List of cards, or null if validation fails
     */
    private List<Card> validateAndGetCards(Hand hand, List<Integer> indices) {
        List<Card> cards = new ArrayList<>();
        
        try {
            for (Integer index : indices) {
                Card card = hand.getAt(index);
                cards.add(card);
            }
            return cards;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: Invalid card selection. Please try again.");
            return null;
        }
    }
    
    /**
     * Confirms the claim with the player before submitting.
     * @param player The player making the claim
     * @param claimCount Number of cards claimed
     * @param currentRank Current round's card type
     * @param selectedCards Cards selected by player
     * @return True if confirmed, false otherwise
     */
    private boolean confirmClaim(Player player, int claimCount, Rank currentRank, List<Card> selectedCards) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("CONFIRM CLAIM", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Player: %-49s ║%n", player.getId());
        System.out.printf("║ Claiming: %d %s%-38s ║%n", claimCount, formatRank(currentRank), "");
        System.out.printf("║ Selected cards: %-39s ║%n", formatCardList(selectedCards));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        return inputHandler.confirmAction("Confirm this claim?");
    }
    
    /**
     * Displays the player's hand with numbered indices.
     * @param hand Player's hand
     */
    private void displayHandWithIndices(Hand hand) {
        if (hand == null) {
            System.out.println("No cards in hand");
            return;
        }
        
        System.out.println("Your hand:");
        System.out.println("─".repeat(40));
        
        int handSize = getHandSize(hand);
        for (int i = 0; i < handSize; i++) {
            try {
                Card card = hand.getAt(i);
                System.out.printf("%d. %s%n", i + 1, formatRank(card.getRank()));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        
        System.out.println("─".repeat(40));
        System.out.println();
    }
    
    /**
     * Gets the size of a hand by counting cards.
     * @param hand The hand to measure
     * @return Number of cards in hand
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
     * Formats a list of cards for display.
     * @param cards List of cards
     * @return Formatted string
     */
    private String formatCardList(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return "None";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(formatRank(cards.get(i).getRank()));
        }
        return sb.toString();
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
     * Data class to hold claim input results.
     */
    public static class ClaimInput {
        private final int claimCount;
        private final List<Integer> cardIndices;
        private final List<Card> selectedCards;
        private final Rank claimedRank;
        
        public ClaimInput(int claimCount, List<Integer> cardIndices, 
                         List<Card> selectedCards, Rank claimedRank) {
            this.claimCount = claimCount;
            this.cardIndices = new ArrayList<>(cardIndices);
            this.selectedCards = new ArrayList<>(selectedCards);
            this.claimedRank = claimedRank;
        }
        
        public int getClaimCount() {
            return claimCount;
        }
        
        public List<Integer> getCardIndices() {
            return new ArrayList<>(cardIndices);
        }
        
        public List<Card> getSelectedCards() {
            return new ArrayList<>(selectedCards);
        }
        
        public Rank getClaimedRank() {
            return claimedRank;
        }
        
        @Override
        public String toString() {
            return String.format("ClaimInput{count=%d, rank=%s, cards=%d}", 
                claimCount, claimedRank, selectedCards.size());
        }
    }
}