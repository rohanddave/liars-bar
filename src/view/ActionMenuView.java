package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.game.Card;
import model.game.Game;
import model.game.Hand;
import model.game.Player;
import model.game.Rank;


public class ActionMenuView {
    private Scanner scanner;
    
    public ActionMenuView() {
        this.scanner = new Scanner(System.in);
    }
    
    public void renderActionMenu(int row, boolean hasActiveClaim) {
        System.out.println("\n--- Available Actions ---");
        System.out.println("[P]lay Card  " + (hasActiveClaim ? "[C]hallenge  " : "") + "[H]elp  [Q]uit");
        System.out.print("Select action: ");
    }
    
    public PlayCardResult handlePlayCardAction(Game game, Player player) {
        // Get the current round rank
        Rank currentRank = game.getRank();
        System.out.println("\nCurrent round: " + formatRank(currentRank));
        
        // Ask how many cards of the current rank they want to claim
        int claimCount = promptForClaimCount();
        
        // Display player's hand and let them select cards to play
        List<Card> selectedCards = selectCardsFromHand(player);
        
        if (selectedCards.isEmpty()) {
            System.out.println("Card selection cancelled.");
            return null;
        }
        
        return new PlayCardResult(claimCount, selectedCards, currentRank);
    }
    
    private int promptForClaimCount() {
        int count = 0;
        boolean validInput = false;
        
        while (!validInput) {
            System.out.print("How many cards do you want to claim? (1-4): ");
            try {
                count = Integer.parseInt(scanner.nextLine().trim());
                if (count >= 1 && count <= 4) {
                    validInput = true;
                } else {
                    System.out.println("Please enter a number between 1 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        
        return count;
    }
    
    private List<Card> selectCardsFromHand(Player player) {
        List<Card> selectedCards = new ArrayList<>();
        Hand hand = player.getHand();
        
        // Display the player's hand
        System.out.println("\nYour hand:");
        int handSize = 0;
        try {
            // Count cards in hand (this is a bit hacky but works without knowing the hand implementation)
            while (true) {
                hand.getAt(handSize);
                handSize++;
            }
        } catch (IndexOutOfBoundsException e) {
            // End of hand reached
        }
        
        // Display each card with an index
        for (int i = 0; i < handSize; i++) {
            try {
                Card card = hand.getAt(i);
                System.out.println((i + 1) + ". " + formatRank(card.getRank()));
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        
        // Let the player select cards
        for (int i = 0; i < cardCount; i++) {
            boolean validSelection = false;
            while (!validSelection) {
                System.out.print("\nSelect card " + (i + 1) + "/" + cardCount + " (enter number): ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Selection cancelled.");
                    return new ArrayList<>(); // Return empty list to indicate cancellation
                }
                
                try {
                    int index = Integer.parseInt(input) - 1; // Convert to 0-based index
                    if (index >= 0 && index < handSize) {
                        Card selectedCard = hand.getAt(index);
                        selectedCards.add(selectedCard);
                        System.out.println("Selected: " + formatRank(selectedCard.getRank()));
                        validSelection = true;
                    } else {
                        System.out.println("Invalid card number: " + (index + 1));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Card number out of range.");
                }
            }
        }
        
        return selectedCards;
    }

    private String formatRank(Rank rank) {
        if (rank == null) {
            return "Unknown";
        }
        
        switch (rank) {
            case ACE:
                return "Ace";
            case KING:
                return "King";
            case QUEEN:
                return "Queen";
            case JACK:
                return "Jack";
            default:
                return rank.toString();
        }
    }
    

    public static class PlayCardResult {
        private final int claimCount;
        private final List<Card> selectedCards;
        private final Rank claimedRank;
        
        public PlayCardResult(int claimCount, List<Card> selectedCards, Rank claimedRank) {
            this.claimCount = claimCount;
            this.selectedCards = selectedCards;
            this.claimedRank = claimedRank;
        }
        
        public int getClaimCount() {
            return claimCount;
        }
        
        public List<Card> getSelectedCards() {
            return selectedCards;
        }
        
        public Rank getClaimedRank() {
            return claimedRank;
        }
    }
}