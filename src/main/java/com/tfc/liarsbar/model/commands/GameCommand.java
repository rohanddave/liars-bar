package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Command interface for the Command design pattern
 * Encapsulates game actions that can be executed, undone, and logged
 */
public interface GameCommand {
    
    /**
     * Executes the command
     * @param game The game context
     * @param player The player executing the command
     * @return ActionResult containing the outcome
     */
    ActionResult execute(Game game, Player player);
    
    /**
     * Gets the command name for logging and display
     * @return Command name string
     */
    String getCommandName();
    
    /**
     * Validates if the command can be executed in the current game state
     * @param game The game context
     * @param player The player attempting to execute
     * @return true if command is valid, false otherwise
     */
    boolean canExecute(Game game, Player player);
    
    /**
     * Gets a description of what this command does
     * @return Human-readable description
     */
    String getDescription();
}