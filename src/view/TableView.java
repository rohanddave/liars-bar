package view;

import model.game.GameState;
import model.game.Claim;
import model.game.Player;
import model.game.Rank;
import java.util.List;

public class TableView {
    private static final String TABLE_TOP = "╔══════════════════════════════════════════════════════════╗";
    private static final String TABLE_BOTTOM = "╚══════════════════════════════════════════════════════════╝";
    private static final String TABLE_SIDE = "║";
    
    public void renderCurrentClaim(Claim claim) {
        if (claim == null) {
            renderEmptyTable();
            return;
        }
        
        System.out.println(TABLE_TOP);
        System.out.printf("║%s║%n", centerText("CURRENT CLAIM", 58));
        System.out.println("║" + "─".repeat(58) + "║");
        System.out.printf("║ Player: %-47s ║%n", claim.getPlayer().getId());
        System.out.printf("║ Count: %-48d ║%n", claim.getCount());
        System.out.println(TABLE_BOTTOM);
    }
    
    public void renderTable(GameState state, Claim currentClaim, Rank currentRank) {
        System.out.println(TABLE_TOP);
        System.out.printf("║%s║%n", centerText("GAME TABLE", 58));
        System.out.println("║" + "─".repeat(58) + "║");
        
        if (currentRank != null) {
            System.out.printf("║ Current Round: %-42s ║%n", currentRank.toString());
        }
        
        if (currentClaim != null) {
            System.out.printf("║ Active Claim: %s claims %d cards%-20s ║%n", 
                currentClaim.getPlayer().getId(), 
                currentClaim.getCount(), "");
        } else {
            System.out.printf("║ Active Claim: %-42s ║%n", "None");
        }
        
        System.out.println("║" + "─".repeat(58) + "║");
        
        if (state != null && state.getPlayers() != null) {
            renderPlayersAroundTable(state.getPlayers());
        }
        
        System.out.println(TABLE_BOTTOM);
    }
    
    public void renderEmptyTable() {
        System.out.println(TABLE_TOP);
        System.out.printf("║%s║%n", centerText("GAME TABLE", 58));
        System.out.println("║" + "─".repeat(58) + "║");
        System.out.printf("║%s║%n", centerText("No active claim", 58));
        System.out.println(TABLE_BOTTOM);
    }
    
    public void renderPlayersAroundTable(List<Player> players) {
        if (players == null || players.isEmpty()) {
            System.out.printf("║%s║%n", centerText("No players", 58));
            return;
        }
        
        System.out.printf("║%s║%n", centerText("PLAYERS", 58));
        System.out.println("║" + "─".repeat(58) + "║");
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String status = player.isAlive() ? "ALIVE" : "ELIMINATED";
            int cardCount = getPlayerCardCount(player);
            
            System.out.printf("║ [%d] %-15s Cards: %2d  Status: %-12s ║%n", 
                i + 1, player.getId(), cardCount, status);
        }
    }
    
    public void renderRevolverResult(Player player, boolean eliminated) {
        System.out.println(TABLE_TOP);
        System.out.printf("║%s║%n", centerText("REVOLVER RESULT", 58));
        System.out.println("║" + "─".repeat(58) + "║");
        System.out.printf("║ Player: %-47s ║%n", player.getId());
        
        if (eliminated) {
            System.out.printf("║%s║%n", centerText("💥 BANG! Player eliminated! 💥", 58));
        } else {
            System.out.printf("║%s║%n", centerText("🔫 Click... Player survives", 58));
        }
        
        System.out.println(TABLE_BOTTOM);
    }
    
    public void renderChallengeResult(Claim claim, boolean claimWasTrue, List<model.game.Card> revealedCards) {
        System.out.println(TABLE_TOP);
        System.out.printf("║%s║%n", centerText("CHALLENGE RESULT", 58));
        System.out.println("║" + "─".repeat(58) + "║");
        System.out.printf("║ Challenged Claim: %s claimed %d cards%-15s ║%n", 
            claim.getPlayer().getId(), claim.getCount(), "");
        
        if (revealedCards != null && !revealedCards.isEmpty()) {
            System.out.printf("║ Revealed Cards: %-40s ║%n", 
                revealedCards.size() + " cards");
        }
        
        if (claimWasTrue) {
            System.out.printf("║%s║%n", centerText("✅ CLAIM WAS TRUE!", 58));
            System.out.printf("║%s║%n", centerText("Challenger must use revolver", 58));
        } else {
            System.out.printf("║%s║%n", centerText("❌ CLAIM WAS FALSE!", 58));
            System.out.printf("║%s║%n", centerText("Claimer must use revolver", 58));
        }
        
        System.out.println(TABLE_BOTTOM);
    }
    
    public void renderGameSummary(List<Player> players, Player winner) {
        System.out.println(TABLE_TOP);
        System.out.printf("║%s║%n", centerText("GAME SUMMARY", 58));
        System.out.println("║" + "─".repeat(58) + "║");
        
        if (winner != null) {
            System.out.printf("║%s║%n", centerText("🏆 WINNER: " + winner.getId() + " 🏆", 58));
        }
        
        System.out.println("║" + "─".repeat(58) + "║");
        System.out.printf("║%s║%n", centerText("Final Player Status", 58));
        
        for (Player player : players) {
            String status = player.isAlive() ? "SURVIVED" : "ELIMINATED";
            int cardCount = getPlayerCardCount(player);
            System.out.printf("║ %-15s Cards: %2d  Status: %-12s ║%n", 
                player.getId(), cardCount, status);
        }
        
        System.out.println(TABLE_BOTTOM);
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
