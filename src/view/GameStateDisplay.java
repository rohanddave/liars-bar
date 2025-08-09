package view;

import model.game.*;
import java.util.List;

public class GameStateDisplay {
    private final PlayersView playersView;
    private final TableView tableView;
    private final SimpleDisplayManager displayManager;
    
    public GameStateDisplay() {
        this.playersView = new PlayersView();
        this.tableView = new TableView();
        this.displayManager = new SimpleDisplayManager();
    }
    
    public void displayCompleteGameState(GameState state, Player currentPlayer, 
                                       Rank currentRank, Claim currentClaim, 
                                       int roundNumber, String lastAction) {
        displayManager.clearScreen();
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("LIAR'S BAR - ROUND " + roundNumber, 58));
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        if (currentRank != null) {
            System.out.printf("Current Card Type: %s%n", currentRank.toString());
            System.out.println();
        }
        
        tableView.renderTable(state, currentClaim, currentRank);
        System.out.println();
        
        if (state != null && currentPlayer != null) {
            playersView.renderAllPlayers(state, currentPlayer.getId());
            System.out.println();
        }
        
        if (lastAction != null && !lastAction.isEmpty()) {
            System.out.println("Last Action: " + lastAction);
            System.out.println();
        }
    }
    
    public void displayPlayerHand(Player player) {
        if (player == null || player.getHand() == null) {
            System.out.println("No hand to display");
            return;
        }
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText(player.getId() + "'s Hand", 58));
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        Hand hand = player.getHand();
        int cardIndex = 0;
        
        System.out.print("Cards: ");
        try {
            while (true) {
                Card card = hand.getAt(cardIndex);
                System.out.printf("[%d] %s  ", cardIndex + 1, card.getRank().toString());
                cardIndex++;
            }
        } catch (IndexOutOfBoundsException e) {
            // Expected when we reach the end
        }
        
        System.out.println();
        System.out.printf("Total: %d cards%n", cardIndex);
        System.out.println();
    }
    
    public void displayClaimDetails(Claim claim, Rank currentRank) {
        if (claim == null) {
            System.out.println("No active claim");
            return;
        }
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("CURRENT CLAIM", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║ Player: %-47s ║%n", claim.getPlayer().getId());
        System.out.printf("║ Claims: %d %s%-40s ║%n", 
            claim.getCount(), 
            currentRank != null ? currentRank.toString() : "cards", "");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public void displayRoundInfo(Rank currentRank, int roundNumber) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("ROUND " + roundNumber, 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        
        if (currentRank != null) {
            System.out.printf("║ Current Card Type: %-37s ║%n", currentRank.toString());
            System.out.printf("║ Players must claim cards of this type%-16s ║%n", "");
            System.out.printf("║ (Aces count as wildcards)%-30s ║%n", "");
        } else {
            System.out.printf("║ No active round%-41s ║%n", "");
        }
        
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public void displayPlayerStatus(List<Player> players, Player currentPlayer) {
        if (players == null || players.isEmpty()) {
            System.out.println("No players to display");
            return;
        }
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("PLAYER STATUS", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        
        for (Player player : players) {
            String indicator = (currentPlayer != null && player.equals(currentPlayer)) ? ">>> " : "    ";
            String status = player.isAlive() ? "ALIVE" : "ELIMINATED";
            int cardCount = getPlayerCardCount(player);
            
            System.out.printf("║%s%-12s Cards: %2d  Status: %-12s ║%n", 
                indicator, player.getId(), cardCount, status);
        }
        
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public void displayChallengeResult(Claim challengedClaim, boolean claimWasTrue, 
                                     Player challenger, List<Card> revealedCards) {
        tableView.renderChallengeResult(challengedClaim, claimWasTrue, revealedCards);
        System.out.println();
        
        if (challenger != null) {
            if (claimWasTrue) {
                System.out.printf("%s must use the revolver (challenged a true claim)%n", 
                    challenger.getId());
            } else {
                System.out.printf("%s must use the revolver (made a false claim)%n", 
                    challengedClaim.getPlayer().getId());
            }
            System.out.println();
        }
    }
    
    public void displayRevolverResult(Player player, boolean eliminated) {
        tableView.renderRevolverResult(player, eliminated);
        System.out.println();
        
        if (eliminated) {
            System.out.printf("💀 %s has been eliminated from the game!%n", player.getId());
        } else {
            System.out.printf("🍀 %s survives and continues playing!%n", player.getId());
        }
        System.out.println();
    }
    
    public void displayGameOver(Player winner, List<Player> allPlayers) {
        displayManager.clearScreen();
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("GAME OVER", 58));
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        if (winner != null) {
            System.out.println("🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉");
            System.out.printf("🎉%s🎉%n", centerText("WINNER: " + winner.getId(), 56));
            System.out.println("🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉");
        } else {
            System.out.println("Game ended without a clear winner");
        }
        
        System.out.println();
        
        if (allPlayers != null) {
            tableView.renderGameSummary(allPlayers, winner);
        }
    }
    
    public void displayError(String error) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("ERROR", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-56s ║%n", error);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    public void displayMessage(String message) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║%s║%n", centerText("MESSAGE", 58));
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-56s ║%n", message);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
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
    
    private int getPlayerCardCount(Player player) {
        if (player.getHand() == null) return 0;
        
        int count = 0;
        try {
            while (true) {
                player.getHand().getAt(count);
                count++;
            }
        } catch (IndexOutOfBoundsException e) {
            // Expected when we reach the end
        }
        return count;
    }
}