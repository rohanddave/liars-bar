package com.tfc.liarsbar.model.commands;

import com.tfc.liarsbar.model.actions.ActionFactory;
import com.tfc.liarsbar.model.events.GameEventPublisher;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Factory that bridges between the Command pattern and existing ActionFactory
 * Converts string input to GameCommand instances using ActionFactory
 */
public class ActionCommandFactory {
    private static final Logger logger = Logger.getLogger(ActionCommandFactory.class.getName());
    
    private final CommandParser commandParser;
    private final ActionFactory actionFactory;

    public ActionCommandFactory(GameEventPublisher eventPublisher) {
        this.commandParser = new CommandParser();
        this.actionFactory = new ActionFactory(eventPublisher);
    }
    
    public ActionCommandFactory(CommandParser parser, ActionFactory factory, Scanner scanner) {
        this.commandParser = parser;
        this.actionFactory = factory;
    }
    
    /**
     * Creates a GameCommand from string input
     * @param input The string command input
     * @return GameCommand instance or null if input is invalid
     */
    public GameCommand createCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            logger.warning("Cannot create command from null or empty input");
            return null;
        }
        
        try {
            // Parse the string input
            CommandRequest request = commandParser.parseCommand(input);
            if (request == null) {
                logger.warning("Could not parse command: " + input);
                return null;
            }
            
            // Create the appropriate command based on the parsed request
            return createCommandFromRequest(request);
            
        } catch (Exception e) {
            logger.severe("Error creating command from input '" + input + "': " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Creates a GameCommand from a parsed CommandRequest
     * @param request The parsed command request
     * @return GameCommand instance or null if request is invalid
     */
    public GameCommand createCommandFromRequest(CommandRequest request) {
        if (request == null) {
            return null;
        }
        
        String commandName = request.getCommandName().toLowerCase();
        
        return switch (commandName) {
            case "claim" -> new ClaimCommand(request);
            case "challenge" -> new ChallengeCommand(request);
            case "shoot" -> new ShootCommand(request);
            case "start" -> new StartCommand(request);
            default -> {
                logger.warning("Unknown command type: " + commandName);
                yield null;
            }
        };
    }
    
    /**
     * Validates if a string input can be converted to a valid command
     * @param input The string to validate
     * @return true if input can be parsed into a command
     */
    public boolean isValidCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        try {
            CommandRequest request = commandParser.parseCommand(input);
            return request != null && createCommandFromRequest(request) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets help text for available commands
     * @return String describing available commands and formats
     */
    public String getCommandHelp() {
        return commandParser.getCommandHelp();
    }
    
    /**
     * Gets the command parser instance
     * @return CommandParser being used
     */
    public CommandParser getCommandParser() {
        return commandParser;
    }
    
    /**
     * Gets the action factory instance
     * @return ActionFactory being used
     */
    public ActionFactory getActionFactory() {
        return actionFactory;
    }
}