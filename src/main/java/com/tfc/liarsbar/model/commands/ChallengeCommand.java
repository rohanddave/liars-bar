package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.actions.ChallengeAction;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Command for challenging claims in the game
 */
public class ChallengeCommand extends AbstractGameCommand {
    
    public ChallengeCommand(CommandRequest request) {
        super(request, new ChallengeAction());
    }
    
    @Override
    protected ActionResult preExecute(Game game, Player player) {
        // Validate that there's a claim to challenge
        if (game.getLastClaim() == null) {
            return ActionResult.failure("No claim to challenge");
        }
        
        Player lastClaimPlayer = game.getLastClaim().getPlayer();
        if (lastClaimPlayer == null) {
            return ActionResult.failure("Invalid claim state - no player found");
        }
        
        if (player.equals(lastClaimPlayer)) {
            return ActionResult.failure("Cannot challenge your own claim");
        }
        
        if (!lastClaimPlayer.isAlive()) {
            return ActionResult.failure("Cannot challenge claim from eliminated player");
        }
        
        return ActionResult.success("Challenge validation passed");
    }
    
    @Override
    protected boolean isValidCommand(Game game, Player player) {
        if (game.isGameOver()) {
            return false;
        }
        
        if (!player.equals(game.getCurrentPlayer())) {
            return false;
        }
        
        // Must have a valid claim to challenge
        return game.getLastClaim() != null && 
               game.getLastClaim().getPlayer() != null &&
               game.getLastClaim().getPlayer().isAlive() &&
               !player.equals(game.getLastClaim().getPlayer());
    }
    
    @Override
    protected void postExecute(Game game, Player player, ActionResult result) {
        if (result.isSuccess()) {
            // Log the challenge result
            Object data = result.getData();
            if (data instanceof Player loser) {
                String message = "Challenge resolved - " + loser.getName() + 
                    (loser.isAlive() ? " survived" : " was eliminated");
                System.out.println("📊 " + message);
            }
        }
    }
    
    @Override
    public String getDescription() {
        return "Challenge the last claim made";
    }
}