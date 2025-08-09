package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * High-level processor for handling string commands in the game
 * Combines parsing, command creation, and execution with comprehensive error handling
 */
public class GameCommandProcessor {
    private static final Logger logger = Logger.getLogger(GameCommandProcessor.class.getName());
    
    private final ActionCommandFactory commandFactory;
    private final CommandInvoker commandInvoker;
    private final boolean enableHistory;
    
    public GameCommandProcessor(GameEventPublisher eventPublisher) {
        this(eventPublisher, true);
    }
    
    public GameCommandProcessor(GameEventPublisher eventPublisher, boolean enableHistory) {
        this.commandFactory = new ActionCommandFactory(eventPublisher);
        this.commandInvoker = enableHistory ? new CommandInvoker() : null;
        this.enableHistory = enableHistory;
    }
    
    /**
     * Processes a string command input and executes it
     * @param input The string command input
     * @param game The game context
     * @param player The player executing the command
     * @return ActionResult containing the execution outcome
     */
    public ActionResult processCommand(String input, Game game, Player player) {
        try {
            // Validate inputs
            if (input == null || input.trim().isEmpty()) {
                return ActionResult.failure("Command input cannot be empty");
            }
            
            if (game == null) {
                return ActionResult.failure("Game context is required");
            }
            
            if (player == null) {
                return ActionResult.failure("Player context is required");
            }
            
            // Parse and create command
            GameCommand command = commandFactory.createCommand(input);
            if (command == null) {
                String suggestion = getSuggestion(input);
                String message = "Unknown command: '" + input + "'";
                if (suggestion != null) {
                    message += ". Did you mean '" + suggestion + "'?";
                }
                return ActionResult.failure(message);
            }
            
            // Execute command
            if (enableHistory && commandInvoker != null) {
                return commandInvoker.executeCommand(command, game, player);
            } else {
                // Direct execution without history
                if (!command.canExecute(game, player)) {
                    return ActionResult.failure("Command '" + command.getCommandName() + 
                        "' is not valid in current game state");
                }
                return command.execute(game, player);
            }
            
        } catch (Exception e) {
            logger.severe("Error processing command '" + input + "': " + e.getMessage());
            return ActionResult.failure("Command processing failed: " + e.getMessage());
        }
    }
    
    /**
     * Validates if a string input represents a valid command
     * @param input The string to validate
     * @return true if input can be processed as a command
     */
    public boolean isValidCommand(String input) {
        return commandFactory.isValidCommand(input);
    }
    
    /**
     * Gets help text for available commands
     * @return String describing available commands
     */
    public String getCommandHelp() {
        return commandFactory.getCommandHelp();
    }
    
    /**
     * Gets the command history (if enabled)
     * @return CommandInvoker with history, or null if history is disabled
     */
    public CommandInvoker getCommandInvoker() {
        return commandInvoker;
    }
    
    /**
     * Clears the command history (if enabled)
     */
    public void clearHistory() {
        if (enableHistory && commandInvoker != null) {
            commandInvoker.clearHistory();
        }
    }
    
    /**
     * Attempts to provide a suggestion for invalid commands
     * @param input The invalid input
     * @return Suggested command or null if no suggestion available
     */
    private String getSuggestion(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        String normalized = input.trim().toLowerCase();
        
        // Simple fuzzy matching for common typos
        if (normalized.startsWith("chal") || normalized.equals("ch")) {
            return "challenge";
        }
        if (normalized.startsWith("clai") || normalized.equals("c")) {
            return "claim";
        }
        if (normalized.startsWith("sho") || normalized.equals("s") || normalized.equals("fire")) {
            return "shoot";
        }
        
        return null;
    }
    
    /**
     * Creates a CommandException for error handling
     * @param message Error message
     * @param input The input that caused the error
     * @param errorType Type of error
     * @return CommandException instance
     */
    public CommandException createCommandException(String message, String input, 
                                                   CommandException.CommandErrorType errorType) {
        return new CommandException(message, input, errorType);
    }
}