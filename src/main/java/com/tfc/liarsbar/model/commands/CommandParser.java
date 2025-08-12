package com.tfc.liarsbar.model.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 * Parser for converting string input into structured CommandRequest objects
 */
public class CommandParser {
    private static final Logger logger = Logger.getLogger(CommandParser.class.getName());
    
    // Regular expressions for different command patterns
    private static final Pattern CLAIM_PATTERN = Pattern.compile(
        "^(?:claim|play\\s+claim)\\s+(\\d+)\\s+([0-9,]+)$", Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern CHALLENGE_PATTERN = Pattern.compile(
        "^(?:challenge|call)$", Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern SHOOT_PATTERN = Pattern.compile(
        "^(?:shoot|fire|pull)$", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SHOW_PATTERN = Pattern.compile(
            "^(?:show|hand)$", Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern START_PATTERN = Pattern.compile(
        "^(?:start|begin|init)$", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern QUIT_PATTERN = Pattern.compile(
            "^(?:quit|quit)$", Pattern.CASE_INSENSITIVE
    );
    
    // Command aliases mapping
    private static final Map<String, String> COMMAND_ALIASES = new HashMap<>();
    
    static {
        COMMAND_ALIASES.put("c", "claim");
        COMMAND_ALIASES.put("ch", "challenge");
        COMMAND_ALIASES.put("call", "challenge");
        COMMAND_ALIASES.put("s", "shoot");
        COMMAND_ALIASES.put("fire", "shoot");
        COMMAND_ALIASES.put("pull", "shoot");
        COMMAND_ALIASES.put("play", "claim");
        COMMAND_ALIASES.put("begin", "start");
        COMMAND_ALIASES.put("init", "start");
        COMMAND_ALIASES.put("show", "hand");
        COMMAND_ALIASES.put("quit", "quit");
    }
    
    /**
     * Parses a string input into a CommandRequest
     * @param input The raw string input
     * @return CommandRequest object or null if input is invalid
     */
    public CommandRequest parseCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            logger.warning("Received null or empty command input");
            return null;
        }
        
        String normalizedInput = input.trim().toLowerCase();
        logger.info("Parsing command: " + normalizedInput);
        
        try {
            // Try to match start command
            Matcher startMatcher = START_PATTERN.matcher(normalizedInput);
            if (startMatcher.matches()) {
                return new CommandRequest("start", input);
            }

            // Try to match claim command
            Matcher claimMatcher = CLAIM_PATTERN.matcher(normalizedInput);
            if (claimMatcher.matches()) {
                return parseClaimCommand(claimMatcher, input);
            }
            
            // Try to match challenge command
            Matcher challengeMatcher = CHALLENGE_PATTERN.matcher(normalizedInput);
            if (challengeMatcher.matches()) {
                return new CommandRequest("challenge", input);
            }
            
            // Try to match show command
            Matcher shootMatcher = SHOW_PATTERN.matcher(normalizedInput);
            if (shootMatcher.matches()) {
                return new CommandRequest("show", input);
            }

            Matcher quitMatcher = QUIT_PATTERN.matcher(normalizedInput);
            if (quitMatcher.matches()) {
                return new CommandRequest("quit", input);
            }

            // Try simple word-based parsing for aliases
            String[] words = normalizedInput.split("\\s+");
            if (words.length > 0) {
                String firstWord = words[0];
                if (COMMAND_ALIASES.containsKey(firstWord)) {
                    String commandName = COMMAND_ALIASES.get(firstWord);
                    Map<String, Object> parameters = new HashMap<>();
                    
                    // Extract numeric parameters if present
                    if (words.length > 1 && words[1].matches("\\d+")) {
                        parameters.put("count", Integer.parseInt(words[1]));
                    }
                    
                    return new CommandRequest(commandName, parameters, input);
                }
            }
            
            logger.warning("Could not parse command: " + input);
            return null;
            
        } catch (Exception e) {
            logger.severe("Error parsing command '" + input + "': " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parses claim command with count and discard indices
     * @param matcher The regex matcher for claim pattern
     * @param originalInput Original input string
     * @return CommandRequest for claim command
     */
    private CommandRequest parseClaimCommand(Matcher matcher, String originalInput) {
        Map<String, Object> parameters = new HashMap<>();
        
        try {
            // Extract count (required)
            String countStr = matcher.group(1);
            int count = Integer.parseInt(countStr);
            if (count <= 0) {
                logger.warning("Invalid count in claim command: " + count);
                return null;
            }
            parameters.put("count", count);
            
            // Extract discard indices (required)
            String indicesStr = matcher.group(2);
            String[] indices = indicesStr.split(",");
            
            if (indices.length != count) {
                logger.warning("Number of discard indices (" + indices.length + 
                    ") doesn't match claim count (" + count + ")");
                return null;
            }
            
            int[] discardIndices = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                try {
                    discardIndices[i] = Integer.parseInt(indices[i].trim());
                    if (discardIndices[i] < 0) {
                        logger.warning("Invalid discard index: " + discardIndices[i]);
                        return null;
                    }
                } catch (NumberFormatException e) {
                    logger.warning("Could not parse discard index: " + indices[i]);
                    return null;
                }
            }
            
            parameters.put("discardIndices", discardIndices);
            
        } catch (Exception e) {
            logger.severe("Error parsing claim command: " + e.getMessage());
            return null;
        }
        
        return new CommandRequest("claim", parameters, originalInput);
    }
    
    /**
     * Validates if a string could be a valid command
     * @param input The string to validate
     * @return true if input looks like a valid command
     */
    public boolean isValidCommandFormat(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        CommandRequest request = parseCommand(input);
        return request != null;
    }
    
    /**
     * Gets help text for available command formats
     * @return String describing available commands
     */
    public String getCommandHelp() {
        return """
            Available Commands:
            • claim [count] [indices] - Make a claim (e.g., 'claim 2 0,1')
            • challenge - Challenge the last claim
            • shoot - Pull the trigger
            • start - Start the game
            
            Aliases:
            • ch - Short for challenge
            • s - Short for shoot
            • call - Same as challenge
            • fire/pull - Same as shoot
            • begin/init - Same as start
            
            Examples:
            • claim 2 0,1 - Claim 2 cards, discard cards at indices 0 and 1
            • claim 1 3 - Claim 1 card, discard card at index 3
            """;
    }
}