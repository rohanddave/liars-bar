package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.actions.GameAction;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Abstract base class for game commands that bridge to GameAction implementations
 */
public abstract class AbstractGameCommand implements GameCommand {
    protected final CommandRequest request;
    protected final GameAction gameAction;
    
    protected AbstractGameCommand(CommandRequest request, GameAction gameAction) {
        this.request = request;
        this.gameAction = gameAction;
    }
    
    @Override
    public ActionResult execute(Game game, Player player) {
        if (gameAction == null) {
            return ActionResult.failure("No game action available for command: " + getCommandName());
        }
        
        // Pre-execution hook for command-specific logic
        ActionResult preCheck = preExecute(game, player);
        if (!preCheck.isSuccess()) {
            return preCheck;
        }
        
        // Execute the underlying game action
        ActionResult result = gameAction.execute(game, player);
        
        // Post-execution hook for command-specific logic
        postExecute(game, player, result);
        
        return result;
    }
    
    @Override
    public boolean canExecute(Game game, Player player) {
        if (gameAction == null) {
            return false;
        }
        return gameAction.isValidFor(game, player) && isValidCommand(game, player);
    }
    
    @Override
    public String getCommandName() {
        return request.getCommandName();
    }
    
    /**
     * Pre-execution hook for command-specific validation and setup
     * @param game The game context
     * @param player The player executing the command
     * @return ActionResult indicating if execution should proceed
     */
    protected ActionResult preExecute(Game game, Player player) {
        return ActionResult.success("Pre-execution check passed");
    }
    
    /**
     * Post-execution hook for command-specific cleanup or additional processing
     * @param game The game context
     * @param player The player who executed the command
     * @param result The result of the main execution
     */
    protected void postExecute(Game game, Player player, ActionResult result) {
        // Default implementation does nothing
    }
    
    /**
     * Command-specific validation beyond the underlying GameAction validation
     * @param game The game context
     * @param player The player attempting to execute
     * @return true if command is valid
     */
    protected boolean isValidCommand(Game game, Player player) {
        return true; // Default implementation allows all
    }
    
    /**
     * Gets the original command request
     * @return CommandRequest that created this command
     */
    public CommandRequest getRequest() {
        return request;
    }
    
    /**
     * Gets the underlying game action
     * @return GameAction being executed
     */
    public GameAction getGameAction() {
        return gameAction;
    }
}