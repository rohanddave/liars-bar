package view;

import model.game.Hand;
import model.game.Card;
import model.game.Player;
import java.util.List;

public class HandView {
    private static final String CARD_TOP = "┌─────┐";
    private static final String CARD_BOTTOM = "└─────┘";
    private static final String CARD_SIDE = "│";
    
    public void displayPlayerHand(Player player, boolean showCards) {
        if (player == null || player.getHand() == null) {
            displayEmptyHand(player != null ? player.getId() : "Unknown");
            return;
        }
        
        Hand hand = player.getHand();
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText(player.getId() + "'s Hand", 58));
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        if (showCards) {
            displayCardsVisual(hand);
        } else {
            displayCardBacks(getHandSize(hand));
        }
        
        System.out.println();
        displayHandSummary(hand, showCards);
    }
    
    public void displayHandSelection(Hand hand, List<Integer> selectedIndices) {
        if (hand == null) {
            System.out.println("No hand to display");
            return;
        }
        
        System.out.println("Select cards from your hand:");
        System.out.println("─".repeat(40));
        
        int cardIndex = 0;
        try {
            while (true) {
                Card card = hand.getAt(cardIndex);
                String selected = selectedIndices.contains(cardIndex) ? "[X]" : "[ ]";
                System.out.printf("%s %d. %s%n", selected, cardIndex + 1, card.getRank().toString());
                cardIndex++;
            }
        } catch (IndexOutOfBoundsException e) {
            // Expected when we reach the end
        }
        
        System.out.println("─".repeat(40));
        System.out.printf("Selected: %d cards%n", selectedIndices.size());
        System.out.println();
    }
    
    public void displayCardComparison(List<Card> claimedCards, List<Card> actualCards) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("CARD REVEAL", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        
        System.out.printf("║ Claimed: %-46s ║%n", formatCardList(claimedCards));
        System.out.printf("║ Actual:  %-46s ║%n", formatCardList(actualCards));
        
        boolean match = cardsMatch(claimedCards, actualCards);
        String result = match ? "✅ CLAIM WAS TRUE" : "❌ CLAIM WAS FALSE";
        System.out.printf("║ Result:  %-46s ║%n", result);
        
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public void displayHandAfterDiscard(Player player, List<Card> discardedCards) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("CARDS DISCARDED", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║ Player: %-47s ║%n", player.getId());
        System.out.printf("║ Discarded: %-44s ║%n", formatCardList(discardedCards));
        System.out.printf("║ Remaining: %d cards%-36s ║%n", 
            getHandSize(player.getHand()), "");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public void displayCardSelectionPrompt(int maxCards) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("SELECT CARDS", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║ Select up to %d cards from your hand%-22s ║%n", maxCards, "");
        System.out.printf("║ Enter card numbers separated by spaces%-18s ║%n", "");
        System.out.printf("║ Example: 1 3 5%-44s ║%n", "");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    private void displayCardsVisual(Hand hand) {
        int cardCount = getHandSize(hand);
        
        // Display card tops
        for (int i = 0; i < cardCount; i++) {
            System.out.print(CARD_TOP + " ");
        }
        System.out.println();
        
        // Display card content
        for (int i = 0; i < cardCount; i++) {
            try {
                Card card = hand.getAt(i);
                String rank = card.getRank().toString();
                System.out.printf("%s %-3s %s ", CARD_SIDE, rank.substring(0, Math.min(3, rank.length())), CARD_SIDE);
            } catch (IndexOutOfBoundsException e) {
                System.out.printf("%s     %s ", CARD_SIDE, CARD_SIDE);
            }
        }
        System.out.println();
        
        // Display card numbers
        for (int i = 0; i < cardCount; i++) {
            System.out.printf("%s [%d] %s ", CARD_SIDE, i + 1, CARD_SIDE);
        }
        System.out.println();
        
        // Display card bottoms
        for (int i = 0; i < cardCount; i++) {
            System.out.print(CARD_BOTTOM + " ");
        }
        System.out.println();
    }
    
    private void displayCardBacks(int cardCount) {
        // Display card tops
        for (int i = 0; i < cardCount; i++) {
            System.out.print(CARD_TOP + " ");
        }
        System.out.println();
        
        // Display card backs
        for (int i = 0; i < cardCount; i++) {
            System.out.printf("%s ??? %s ", CARD_SIDE, CARD_SIDE);
        }
        System.out.println();
        
        // Display card numbers
        for (int i = 0; i < cardCount; i++) {
            System.out.printf("%s [%d] %s ", CARD_SIDE, i + 1, CARD_SIDE);
        }
        System.out.println();
        
        // Display card bottoms
        for (int i = 0; i < cardCount; i++) {
            System.out.print(CARD_BOTTOM + " ");
        }
        System.out.println();
    }
    
    private void displayEmptyHand(String playerName) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText(playerName + "'s Hand", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║%s║%n", centerText("No cards in hand", 58));
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    private void displayHandSummary(Hand hand, boolean showDetails) {
        int cardCount = getHandSize(hand);
        
        System.out.printf("Total cards: %d%n", cardCount);
        
        if (showDetails && cardCount > 0) {
            System.out.print("Cards: ");
            for (int i = 0; i < cardCount; i++) {
                try {
                    Card card = hand.getAt(i);
                    System.out.printf("%s ", card.getRank().toString());
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            System.out.println();
        }
    }
    
    private String formatCardList(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return "None";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(cards.get(i).getRank().toString());
        }
        return sb.toString();
    }
    
    private boolean cardsMatch(List<Card> claimed, List<Card> actual) {
        if (claimed == null || actual == null) return false;
        if (claimed.size() != actual.size()) return false;
        
        // Simple comparison - in a real game this would be more sophisticated
        for (int i = 0; i < claimed.size(); i++) {
            if (!claimed.get(i).getRank().equals(actual.get(i).getRank())) {
                return false;
            }
        }
        return true;
    }
    
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
}