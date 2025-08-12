package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.actions.ShootAction;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Command for shooting (pulling the trigger) in the game
 */
public class ShootCommand extends AbstractGameCommand {
    
    public ShootCommand(CommandRequest request) {
        super(request, new ShootAction());
    }
    
    @Override
    protected ActionResult preExecute(Game game, Player player) {
        // Validate that player has a revolver
        if (player.getRevolver() == null) {
            return ActionResult.failure("Player does not have a revolver");
        }
        
        if (!player.isAlive()) {
            return ActionResult.failure("Eliminated players cannot shoot");
        }
        
        return ActionResult.success("Shoot validation passed");
    }
    
    @Override
    protected boolean isValidCommand(Game game, Player player) {
        if (game.isGameOver()) {
            return false;
        }
        
        // Shooting might be valid in different contexts, so we'll be permissive here
        // and let the underlying ShootAction determine specific validity
        return player.isAlive() && player.getRevolver() != null;
    }
    
    @Override
    protected void postExecute(Game game, Player player, ActionResult result) {
        if (result.isSuccess()) {
            // Log the shooting result
            if (player.isAlive()) {
                System.out.println("Click! " + player.getName() + " survives this round");
            } else {
                System.out.println("BANG! " + player.getName() + " was eliminated!");
            }
        }
    }
    
    @Override
    public String getDescription() {
        return "Pull the trigger on the revolver";
    }
}