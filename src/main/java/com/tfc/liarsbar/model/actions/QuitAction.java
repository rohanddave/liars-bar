package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Strategy for quitting the game.
 * Handles player removal with appropriate game state validation and cleanup.
 */
public class QuitAction implements GameAction {
    @Override
    public ActionResult execute(Game game, Player player) {
        try {
            // Check if this is the current player
            boolean isCurrentPlayer = player.equals(game.getCurrentPlayer());
            
            // Store active player count before removal
            int playerCountBefore = game.getActivePlayers().size();
            
            // Remove the player from the game
            game.removePlayer(player);
            
            // Check if game is still valid (has enough players)
            if (game.getActivePlayers().size() < 2) {
                // If only one player remains, they are the winner
                if (game.getActivePlayers().size() == 1) {
                    Player winner = game.getActivePlayers().get(0);
                    String message = "Player " + player.getName() + " has quit the game. " +
                            "Only " + winner.getName() + " remains and is declared the winner!";
                    System.out.println(message);
                    return ActionResult.success(message);
                } else {
                    // No players left
                    String message = "Player " + player.getName() + " has quit the game. " +
                            "Not enough players remain to continue. Game ended.";
                    System.out.println(message);
                    return ActionResult.success(message);
                }
            }
            
            // If the current player quit, we need to settle any pending actions
            if (isCurrentPlayer) {
                // If there was an active claim by this player, settle it
                if (game.getLastClaim() != null && player.equals(game.getLastClaim().getPlayer())) {
                    game.settleLastClaim();
                }
                
                // Move to the next player's turn
                game.moveToNextMove();
            }
            
            String message = "Player " + player.getName() + " has quit the game. " +
                    "Game continues with " + game.getActivePlayers().size() + " players.";
            System.out.println(message);
            return ActionResult.success(message);
        } catch (Exception e) {
            return ActionResult.failure("Failed to quit game: " + e.getMessage());
        }
    }

    @Override
    public String getActionName() {
        return "Quit";
    }

    @Override
    public boolean isValidFor(Game game, Player player) {
        return game != null && player != null && game.isGameStarted();
    }
}
