package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Action for starting the game
 */
public class StartAction implements GameAction {
    
    @Override
    public ActionResult execute(Game game, Player player) {
        try {
            System.out.println("inside start action execute function");
            if (game.isGameOver()) {
                return ActionResult.failure("Cannot start game - previous game is still active");
            }
            
            game.startGame();
            return ActionResult.success("Game started successfully with " + 
                game.getActivePlayers().size() + " players");
                
        } catch (Exception e) {
            return ActionResult.failure("Failed to start game: " + e.getMessage());
        }
    }
    
    @Override
    public String getActionName() {
        return "Start Game";
    }
    
    @Override
    public boolean isValidFor(Game game, Player player) {
        if (game == null || player == null) {
            return false;
        }
        
        // Check if game is not already started
        try {
            boolean isGameOver = game.isGameOver();
            System.out.println("Active number of players: " + game.getActivePlayers().size());
            boolean hasEnoughPlayers = game.getActivePlayers().size() >= 2;
            System.out.println("isGameOver: " + isGameOver + "\t hasEnoughPlayers: " + hasEnoughPlayers);
            return !isGameOver && hasEnoughPlayers;
        } catch (Exception e) {
            // If we can't determine game state, allow the attempt
            return true;
        }
    }
}