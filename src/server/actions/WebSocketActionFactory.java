package server.actions;

import model.actions.ActionFactory;
import model.actions.GameAction;
import model.game.Game;
import model.game.Player;
import server.ClientConnection;
import server.GameRoomManager;

import java.util.ArrayList;
import java.util.List;

public class WebSocketActionFactory {
    private final GameRoomManager roomManager;
    
    public WebSocketActionFactory(GameRoomManager roomManager) {
        this.roomManager = roomManager;
    }
    
    public List<String> getAvailableActionNames(Game game, Player player) {
        List<String> actions = new ArrayList<>();
        
        // Check if player can make a claim
        if (canMakeClaim(game, player)) {
            actions.add("MAKE_CLAIM");
        }
        
        // Check if player can challenge
        if (canChallenge(game, player)) {
            actions.add("CHALLENGE_CLAIM");
        }
        
        // Check if player needs to shoot
        if (needsToShoot(game, player)) {
            actions.add("SHOOT_REVOLVER");
        }
        
        return actions;
    }
    
    private boolean canMakeClaim(Game game, Player player) {
        // Player can claim if it's their turn and they have cards
        return game.getCurrentPlayer() != null && 
               game.getCurrentPlayer().equals(player) && 
               player.getHand() != null && 
               player.getHand().getSize() > 0;
    }
    
    private boolean canChallenge(Game game, Player player) {
        // Player can challenge if there's an active claim and it's not their claim
        var lastClaim = game.getLastClaim();
        return lastClaim != null && 
               !lastClaim.getPlayer().equals(player) &&
               game.getCurrentPlayer() != null &&
               game.getCurrentPlayer().equals(player);
    }
    
    private boolean needsToShoot(Game game, Player player) {
        // This would be determined by game logic - after losing a challenge
        // For now, we'll let the game event system handle when shooting is required
        return false;
    }
}