package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.actions.ClaimAction;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

import java.util.Scanner;

/**
 * Command for making claims in the game
 */
public class ClaimCommand extends AbstractGameCommand {
    private final Scanner scanner;
    
    public ClaimCommand(CommandRequest request, Scanner scanner) {
        super(request, new ClaimAction(scanner));
        this.scanner = scanner;
    }
    
    @Override
    protected ActionResult preExecute(Game game, Player player) {
        // Validate claim count parameter if provided
        Integer count = request.getIntParameter("count");
        if (count != null) {
            if (count <= 0) {
                return ActionResult.failure("Claim count must be positive: " + count);
            }
            
            if (player.getHand() != null && count > player.getHand().getSize()) {
                return ActionResult.failure("Cannot claim " + count + " cards, only have " + 
                    player.getHand().getSize() + " cards in hand");
            }
        }
        
        return ActionResult.success("Claim validation passed");
    }
    
    @Override
    protected boolean isValidCommand(Game game, Player player) {
        // Additional validation beyond the base GameAction validation
        if (game.isGameOver()) {
            return false;
        }
        
        if (!player.equals(game.getCurrentPlayer())) {
            return false;
        }
        
        return player.getHand() != null && player.getHand().getSize() > 0;
    }
    
    @Override
    public String getDescription() {
        Integer count = request.getIntParameter("count");
        if (count != null) {
            return "Make a claim of " + count + " cards";
        } else {
            return "Make a claim (count will be prompted)";
        }
    }
    
    /**
     * Gets the claim count from the command request
     * @return Claim count or null if not specified
     */
    public Integer getClaimCount() {
        return request.getIntParameter("count");
    }
}