package util;

import model.game.*;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstration of the terminal input/output infrastructure.
 * Shows the capabilities of InputHandler, DisplayManager, and TerminalUtils.
 */
public class TerminalDemo {
    
    public static void main(String[] args) {
        DisplayManager display = new DisplayManager();
        InputHandler input = new InputHandler();
        
        // Show welcome message
        display.showWelcomeMessage();
        input.waitForEnter();
        
        // Demonstrate terminal utilities
        demonstrateTerminalUtils();
        input.waitForEnter();
        
        // Demonstrate input handling
        demonstrateInputHandling(input, display);
        
        // Demonstrate display formatting
        demonstrateDisplayFormatting(display);
        input.waitForEnter();
        
        display.showMessage("Terminal infrastructure demonstration complete!");
        input.close();
    }
    
    private static void demonstrateTerminalUtils() {
        TerminalUtils.clearScreen();
        TerminalUtils.printHeader("TERMINAL UTILITIES DEMO");
        
        TerminalUtils.printInfo("This demonstrates various terminal utilities");
        TerminalUtils.printSuccess("Success messages look like this");
        TerminalUtils.printWarning("Warning messages look like this");
        TerminalUtils.printError("Error messages look like this");
        
        TerminalUtils.printSpacing();
        System.out.println("Progress bars:");
        System.out.println("Empty:  " + TerminalUtils.createProgressBar(0, 10, 20));
        System.out.println("Half:   " + TerminalUtils.createProgressBar(5, 10, 20));
        System.out.println("Full:   " + TerminalUtils.createProgressBar(10, 10, 20));
        
        TerminalUtils.printSpacing();
        System.out.println("Text formatting:");
        System.out.println("Padded: '" + TerminalUtils.padToWidth("Hello", 15) + "'");
        System.out.println("Truncated: '" + TerminalUtils.formatToWidth("This is a very long text that will be truncated", 20) + "'");
        
        TerminalUtils.printSeparator();
    }
    
    private static void demonstrateInputHandling(InputHandler input, DisplayManager display) {
        display.clearScreen();
        TerminalUtils.printHeader("INPUT HANDLING DEMO");
        
        display.showMessage("Let's test input validation...");
        
        // Test menu choice
        List<String> menuOptions = Arrays.asList("Option 1", "Option 2", "Option 3");
        display.showActionsMenu(menuOptions);
        int choice = input.getMenuChoice(3);
        display.showMessage("You selected option " + choice);
        
        // Test player name
        String playerName = input.getPlayerName();
        display.showMessage("Hello, " + playerName + "!");
        
        // Test yes/no confirmation
        boolean playAgain = input.confirmAction("Would you like to continue the demo?");
        if (playAgain) {
            display.showMessage("Great! Continuing...");
        } else {
            display.showMessage("Okay, skipping to display demo...");
        }
    }
    
    private static void demonstrateDisplayFormatting(DisplayManager display) {
        display.clearScreen();
        TerminalUtils.printHeader("DISPLAY FORMATTING DEMO");
        
        display.showMessage("This demonstrates various display capabilities");
        
        // Show different message types
        display.showMessage("This is a regular message");
        display.showError("This is an error message");
        
        // Show actions menu
        List<String> actions = Arrays.asList(
            "Make a claim", 
            "Challenge opponent", 
            "View game rules",
            "Quit game"
        );
        display.showActionsMenu(actions);
        
        TerminalUtils.printSpacing();
        display.renderRoundInfo(Rank.KING);
        
        // Show game over message
        TerminalUtils.printSpacing();
        display.showGameOverMessage(null);
    }
}