package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles user input from terminal with validation and error handling.
 * Provides methods for getting various types of validated input from users.
 */
public class InputHandler {
    private final Scanner scanner;
    
    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Gets a menu choice from user within valid range.
     * @param maxOptions Maximum number of valid options (1 to maxOptions)
     * @return Valid menu choice
     */
    public int getMenuChoice(int maxOptions) {
        while (true) {
            try {
                System.out.print("Enter your choice (1-" + maxOptions + "): ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Please enter a valid choice.");
                    continue;
                }
                
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= maxOptions) {
                    return choice;
                }
                System.out.println("Please enter a number between 1 and " + maxOptions + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Gets a player name with validation.
     * @return Non-empty player name
     */
    public String getPlayerName() {
        while (true) {
            System.out.print("Enter player name: ");
            String name = scanner.nextLine().trim();
            
            if (!name.isEmpty() && name.length() <= 20) {
                return name;
            }
            
            if (name.isEmpty()) {
                System.out.println("Player name cannot be empty.");
            } else {
                System.out.println("Player name must be 20 characters or less.");
            }
        }
    }
    
    /**
     * Gets number of players for the game.
     * @return Valid player count (2-4)
     */
    public int getPlayerCount() {
        while (true) {
            try {
                System.out.print("Enter number of players (2-4): ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Please enter the number of players.");
                    continue;
                }
                
                int count = Integer.parseInt(input);
                if (count >= 2 && count <= 4) {
                    return count;
                }
                System.out.println("Number of players must be between 2 and 4.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Gets card count for claims.
     * @return Valid card count (1-5)
     */
    public int getCardCount() {
        while (true) {
            try {
                System.out.print("Enter number of cards to claim: ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Please enter the number of cards.");
                    continue;
                }
                
                int count = Integer.parseInt(input);
                if (count >= 1 && count <= 5) {
                    return count;
                }
                System.out.println("Card count must be between 1 and 5.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Gets card indices from user for card selection.
     * @param handSize Size of player's hand for validation
     * @return List of valid card indices
     */
    public List<Integer> getCardIndices(int handSize) {
        while (true) {
            try {
                System.out.print("Enter card indices (e.g., 1 3 5): ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Please enter card indices.");
                    continue;
                }
                
                String[] parts = input.split("\\s+");
                List<Integer> indices = new ArrayList<>();
                
                for (String part : parts) {
                    int index = Integer.parseInt(part);
                    if (index < 1 || index > handSize) {
                        System.out.println("Card index " + index + " is not valid. Must be between 1 and " + handSize + ".");
                        indices.clear();
                        break;
                    }
                    if (indices.contains(index)) {
                        System.out.println("Duplicate card index " + index + ". Please enter unique indices.");
                        indices.clear();
                        break;
                    }
                    indices.add(index);
                }
                
                if (!indices.isEmpty()) {
                    return indices;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter valid numbers separated by spaces.");
            }
        }
    }
    
    /**
     * Gets yes/no confirmation for challenges.
     * @return True if user wants to challenge, false otherwise
     */
    public boolean getChallenge() {
        while (true) {
            System.out.print("Do you want to challenge? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            
            System.out.println("Please enter 'y' for yes or 'n' for no.");
        }
    }
    
    /**
     * Gets confirmation for any action.
     * @param action Description of the action to confirm
     * @return True if confirmed, false otherwise
     */
    public boolean confirmAction(String action) {
        while (true) {
            System.out.print(action + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            
            System.out.println("Please enter 'y' for yes or 'n' for no.");
        }
    }
    
    /**
     * Waits for user to press Enter to continue.
     */
    public void waitForEnter() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Closes the scanner when done.
     */
    public void close() {
        scanner.close();
    }
}