import model.view.table.PlayerPosition;
import model.view.table.TableDimensions;
import model.view.table.TableSide;
import java.util.ArrayList;
import java.util.List;

public class PlayersView {
    private final TableDimensions dimensions;
    private final List<PlayerPosition> playerPositions;
    
    public PlayersView(TableDimensions dimensions) {
        this.dimensions = dimensions;
        this.playerPositions = new ArrayList<>();
    }
    
    public void renderAllPlayers(GameState state, String currentPlayerName) {
        // Calculate player positions based on number of players
        calculatePlayerPositions(state);
        
        // Render each player at their calculated position
        for (int i = 0; i < state.getPlayers().size(); i++) {
            Player player = state.getPlayers().get(i);
            PlayerPosition position = playerPositions.get(i);
            boolean isActive = player.getName().equals(currentPlayerName);
            
            renderPlayer(position.getY(), position.getX(), player, isActive);
        }
    }
    
    private void calculatePlayerPositions(GameState state) {
        playerPositions.clear();
        int playerCount = state.getPlayers().size();
        
        // Distribute players according to the specified order: bottom, top, left, right
        if (playerCount >= 1) {
            // First player at bottom
            addPlayerAtSide(TableSide.BOTTOM, 0);
        }
        
        if (playerCount >= 2) {
            // Second player at top
            addPlayerAtSide(TableSide.TOP, 0);
        }
        
        if (playerCount >= 3) {
            // Third player at left
            addPlayerAtSide(TableSide.LEFT, 0);
        }
        
        if (playerCount >= 4) {
            // Fourth player at right
            addPlayerAtSide(TableSide.RIGHT, 0);
        }
    }
    
    private void addPlayerAtSide(TableSide side, int position) {
        int x = 0;
        int y = 0;
        
        // Calculate x,y coordinates based on table dimensions and side
        int width = dimensions.getWidth();
        int height = dimensions.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        
        switch (side) {
            case TOP:
                x = centerX;
                y = 0;
                break;
            case BOTTOM:
                x = centerX;
                y = height - 1;
                break;
            case LEFT:
                x = 0;
                y = centerY;
                break;
            case RIGHT:
                x = width - 1;
                y = centerY;
                break;
        }
        
        playerPositions.add(new PlayerPosition(side, position, x, y));
    }
    
    private void renderPlayer(int row, int col, Player player, boolean isActive) {
        // Player Name
        // Cards: 3
        // Status: Active/Waiting/Eliminated
        
        // Highlight current player's turn if active
        String status = isActive ? "Active" : player.isAlive() ? "Waiting" : "Eliminated";
        int cardCount = player.getHand().getCards().size();
        
        // Actual rendering implementation will be expanded in task 4
    }
}

