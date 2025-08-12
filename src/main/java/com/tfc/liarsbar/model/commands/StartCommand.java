package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.actions.StartAction;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Command for starting the game
 */
public class StartCommand extends AbstractGameCommand {
    
    public StartCommand(CommandRequest request) {
        super(request, new StartAction());
    }
    
    @Override
    protected ActionResult preExecute(Game game, Player player) {
        // Validate that there are enough players to start
        if (game.getActivePlayers().size() < 2) {
            return ActionResult.failure("Need at least 2 players to start the game. Current players: " + 
                game.getActivePlayers().size());
        }
        
        return ActionResult.success("Start game validation passed");
    }
    
    @Override
    protected boolean isValidCommand(Game game, Player player) {
        boolean isGameOver = game.isGameOver();
        boolean isGameStarted = game.isGameStarted();
        boolean hasEnoughPlayers = game.getActivePlayers().size() >= 2;
        
        System.out.println("Game validation - isGameOver: " + isGameOver + ", isGameStarted: " + isGameStarted + ", hasEnoughPlayers: " + hasEnoughPlayers);
        
        if (!hasEnoughPlayers) {
            System.out.println("insufficient number of players");
            return false;
        }
        
        // Allow starting if game is over (needs reset) or not started yet
        if (isGameOver) {
            System.out.println("Game is over, allowing restart");
            return true;
        }
        
        if (isGameStarted) {
            System.out.println("Game is already active");
            return false;
        }
        
        return true; // Game not started yet and we have enough players
    }
    
    @Override
    protected void postExecute(Game game, Player player, ActionResult result) {
        if (result.isSuccess()) {
            System.out.println("Game started by " + player.getName() + 
                " with " + game.getActivePlayers().size() + " players");
        }
    }
    
    @Override
    public String getDescription() {
        return "Start the game with current players";
    }
}