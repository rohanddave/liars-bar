package view;

import model.game.GameState;
import model.game.Player;
import java.util.List;

public class PlayersView {
    private static final int TABLE_WIDTH = 60;
    private static final int TABLE_HEIGHT = 20;
    
    public void renderAllPlayers(GameState state, String currentPlayerName) {
        if (state == null || state.getPlayers() == null) {
            System.out.println("No players to display");
            return;
        }
        
        List<Player> players = state.getPlayers();
        
        System.out.println("┌" + "─".repeat(TABLE_WIDTH - 2) + "┐");
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            boolean isActive = player.getId().equals(currentPlayerName);
            
            switch (i) {
                case 0:
                    renderPlayer(2, TABLE_WIDTH / 2 - 10, player, isActive);
                    break;
                case 1:
                    renderPlayer(TABLE_HEIGHT / 2, 2, player, isActive);
                    break;
                case 2:
                    renderPlayer(TABLE_HEIGHT - 4, TABLE_WIDTH / 2 - 10, player, isActive);
                    break;
                case 3:
                    renderPlayer(TABLE_HEIGHT / 2, TABLE_WIDTH - 20, player, isActive);
                    break;
            }
        }
        
        System.out.println("└" + "─".repeat(TABLE_WIDTH - 2) + "┘");
    }
    
    private void renderPlayer(int row, int col, Player player, boolean isActive) {
        String status = player.isAlive() ? "Active" : "Eliminated";
        String indicator = isActive ? ">>> " : "    ";
        int cardCount = getPlayerCardCount(player);
        
        System.out.printf("%s%s%n", " ".repeat(col), indicator + player.getId());
        System.out.printf("%sCards: %d%n", " ".repeat(col), cardCount);
        System.out.printf("%sStatus: %s%n", " ".repeat(col), status);
        
        if (isActive) {
            System.out.printf("%s[YOUR TURN]%n", " ".repeat(col));
        }
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

