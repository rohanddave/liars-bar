package view;

import model.game.Player;
import model.game.Claim;
import model.game.Rank;
import java.util.List;

public class ActionView {
    private static final String BORDER = "═".repeat(60);
    
    public void displayAvailableActions(List<String> actions, Player currentPlayer) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText(currentPlayer.getId() + "'s Turn", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("Choose your action:", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        for (int i = 0; i < actions.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, actions.get(i));
        }
        
        System.out.println();
        System.out.print("Enter your choice (1-" + actions.size() + "): ");
    }
    
    public void displayClaimAction(Player player, int count, Rank rank) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("MAKING CLAIM", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Player: %-49s ║%n", player.getId());
        System.out.printf("║ Claims: %d %s%-42s ║%n", count, rank.toString(), "");
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayChallengeAction(Player challenger, Claim challengedClaim) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("CHALLENGE ISSUED", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Challenger: %-45s ║%n", challenger.getId());
        System.out.printf("║ Challenged: %-45s ║%n", challengedClaim.getPlayer().getId());
        System.out.printf("║ Claim: %d cards%-42s ║%n", challengedClaim.getCount(), "");
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayRevolverAction(Player player) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("REVOLVER TIME", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ %s must spin the revolver%-28s ║%n", player.getId(), "");
        System.out.printf("║%s║%n", centerText("🔫 Russian Roulette 🔫", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        System.out.println("Press Enter to spin the revolver...");
    }
    
    public void displayClaimPrompt(Rank currentRank, int maxCards) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("MAKE YOUR CLAIM", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Current round: %s%-40s ║%n", currentRank.toString(), "");
        System.out.printf("║ You must claim cards of this type%-23s ║%n", "");
        System.out.printf("║ (Aces count as wildcards)%-31s ║%n", "");
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        System.out.printf("How many %s do you claim? (1-%d): ", currentRank.toString(), maxCards);
    }
    
    public void displayChallengePrompt(Claim currentClaim, Rank currentRank) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("CHALLENGE DECISION", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ %s claims %d %s%-30s ║%n", 
            currentClaim.getPlayer().getId(), 
            currentClaim.getCount(), 
            currentRank.toString(), "");
        System.out.printf("║%s║%n", centerText("Do you believe this claim?", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        System.out.println("1. Accept the claim (continue game)");
        System.out.println("2. Challenge the claim (reveal cards)");
        System.out.println();
        System.out.print("Enter your choice (1-2): ");
    }
    
    public void displayCardSelectionPrompt(int claimCount) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("SELECT CARDS", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Select %d cards from your hand%-28s ║%n", claimCount, "");
        System.out.printf("║ Enter card numbers separated by spaces%-18s ║%n", "");
        System.out.printf("║ Example: 1 3 5%-44s ║%n", "");
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        System.out.print("Enter card numbers: ");
    }
    
    public void displayActionResult(String action, boolean success, String message) {
        String status = success ? "SUCCESS" : "FAILED";
        String icon = success ? "✅" : "❌";
        
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("ACTION " + status, 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Action: %-49s ║%n", action);
        System.out.printf("║ %s %-54s ║%n", icon, message);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayTurnTransition(Player fromPlayer, Player toPlayer) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("TURN CHANGE", 60));
        System.out.println("╠" + BORDER + "╣");
        
        if (fromPlayer != null) {
            System.out.printf("║ Previous: %-45s ║%n", fromPlayer.getId());
        }
        
        System.out.printf("║ Current:  %-45s ║%n", toPlayer.getId());
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        System.out.printf("It's now %s's turn!%n", toPlayer.getId());
        System.out.println("Press Enter to continue...");
    }
    
    public void displayWaitingForPlayer(Player player, String action) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("WAITING", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Waiting for %s to %s%-25s ║%n", 
            player.getId(), action, "");
        System.out.printf("║%s║%n", centerText("⏳ Please wait...", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayActionHistory(List<String> recentActions) {
        if (recentActions == null || recentActions.isEmpty()) {
            return;
        }
        
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("RECENT ACTIONS", 60));
        System.out.println("╠" + BORDER + "╣");
        
        int displayCount = Math.min(5, recentActions.size());
        for (int i = recentActions.size() - displayCount; i < recentActions.size(); i++) {
            System.out.printf("║ • %-55s ║%n", recentActions.get(i));
        }
        
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayInvalidAction(String reason) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("INVALID ACTION", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ ❌ %-55s ║%n", reason);
        System.out.printf("║%s║%n", centerText("Please try again", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayActionConfirmation(String action, String details) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("CONFIRM ACTION", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Action: %-49s ║%n", action);
        System.out.printf("║ Details: %-48s ║%n", details);
        System.out.printf("║%s║%n", centerText("Are you sure?", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        System.out.print("Confirm (y/n): ");
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