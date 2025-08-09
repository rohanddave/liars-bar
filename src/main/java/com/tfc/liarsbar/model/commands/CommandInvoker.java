package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Invoker class for the Command pattern
 * Manages command execution and maintains execution history
 */
public class CommandInvoker {
    private static final Logger logger = Logger.getLogger(CommandInvoker.class.getName());
    
    private final List<GameCommand> commandHistory = new ArrayList<>();
    private final int maxHistorySize;
    
    public CommandInvoker() {
        this(100); // Default history size
    }
    
    public CommandInvoker(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }
    
    /**
     * Executes a command and adds it to history if successful
     * @param command The command to execute
     * @param game The game context
     * @param player The player executing the command
     * @return ActionResult of the execution
     */
    public ActionResult executeCommand(GameCommand command, Game game, Player player) {
        if (command == null) {
            return ActionResult.failure("Command cannot be null");
        }
        
        if (game == null) {
            return ActionResult.failure("Game context cannot be null");
        }
        
        if (player == null) {
            return ActionResult.failure("Player cannot be null");
        }
        
        // Validate command before execution
        if (!command.canExecute(game, player)) {
            String reason = "Command '" + command.getCommandName() + "' is not valid in current game state";
            logger.info("Command validation failed: " + reason + " for player: " + player.getName());
            return ActionResult.failure(reason);
        }
        
        try {
            logger.info("Executing command: " + command.getCommandName() + " for player: " + player.getName());
            ActionResult result = command.execute(game, player);
            
            if (result.isSuccess()) {
                addToHistory(command);
                logger.info("Command executed successfully: " + command.getCommandName());
            } else {
                logger.warning("Command execution failed: " + command.getCommandName() + " - " + result.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            String errorMsg = "Error executing command '" + command.getCommandName() + "': " + e.getMessage();
            logger.severe(errorMsg);
            return ActionResult.failure(errorMsg);
        }
    }
    
    /**
     * Adds command to history, maintaining size limit
     * @param command Command to add to history
     */
    private void addToHistory(GameCommand command) {
        commandHistory.add(command);
        
        // Maintain history size limit
        while (commandHistory.size() > maxHistorySize) {
            commandHistory.remove(0);
        }
    }
    
    /**
     * Gets the command history (read-only)
     * @return Unmodifiable list of executed commands
     */
    public List<GameCommand> getCommandHistory() {
        return Collections.unmodifiableList(commandHistory);
    }
    
    /**
     * Gets the last executed command
     * @return Last command or null if no commands executed
     */
    public GameCommand getLastCommand() {
        if (commandHistory.isEmpty()) {
            return null;
        }
        return commandHistory.get(commandHistory.size() - 1);
    }
    
    /**
     * Clears the command history
     */
    public void clearHistory() {
        commandHistory.clear();
        logger.info("Command history cleared");
    }
    
    /**
     * Gets the number of commands in history
     * @return Command count
     */
    public int getHistorySize() {
        return commandHistory.size();
    }
}