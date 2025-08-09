package view;

import java.util.List;

public class MenuView {
    private static final String BORDER = "═".repeat(60);
    private static final String SIDE_BORDER = "║";
    
    public void displayMainMenu() {
        clearScreen();
        
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("LIAR'S BAR", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("Multiplayer Bluffing Game", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        displayMenuOptions(List.of(
            "Start New Game",
            "View Game Rules", 
            "Exit"
        ));
    }
    
    public void displayGameMenu(List<String> actions) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("GAME ACTIONS", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        displayMenuOptions(actions);
    }
    
    public void displayPlayerSetupMenu(int playerNumber, int totalPlayers) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("PLAYER SETUP", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Setting up Player %d of %d%-32s ║%n", 
            playerNumber, totalPlayers, "");
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayGameSetupSummary(List<String> playerNames) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("GAME SETUP COMPLETE", 60));
        System.out.println("╠" + BORDER + "╣");
        
        for (int i = 0; i < playerNames.size(); i++) {
            System.out.printf("║ Player %d: %-46s ║%n", 
                i + 1, playerNames.get(i));
        }
        
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayRulesMenu() {
        clearScreen();
        
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("GAME RULES", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        displayRulesContent();
        
        System.out.println();
        System.out.println("Press Enter to return to main menu...");
    }
    
    public void displayConfirmationDialog(String message) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("CONFIRMATION", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ %-58s ║%n", message);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        System.out.print("Enter your choice (y/n): ");
    }
    
    public void displayInputPrompt(String prompt) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("INPUT REQUIRED", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ %-58s ║%n", prompt);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        System.out.print("Enter your input: ");
    }
    
    public void displayError(String error) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("ERROR", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ ❌ %-55s ║%n", error);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displaySuccess(String message) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("SUCCESS", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ ✅ %-55s ║%n", message);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    public void displayWaitingMessage(String message) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("PLEASE WAIT", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ ⏳ %-55s ║%n", message);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    private void displayMenuOptions(List<String> options) {
        System.out.println("Available Options:");
        System.out.println("─".repeat(20));
        
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, options.get(i));
        }
        
        System.out.println();
        System.out.print("Enter your choice (1-" + options.size() + "): ");
    }
    
    private void displayRulesContent() {
        String[] rules = {
            "SETUP:",
            "• 2-4 players",
            "• Each player starts with 5 cards",
            "• Deck: 6 Kings, 6 Queens, 6 Jacks, 2 Aces (wildcards)",
            "",
            "GAMEPLAY:",
            "• Players take turns making claims about cards they play",
            "• Claims must match the current round's card type",
            "• Aces count as wildcards (can be any card type)",
            "• Other players can either add to the claim or challenge it",
            "",
            "CHALLENGES:",
            "• When challenged, all played cards are revealed",
            "• If claim was true: challenger uses revolver",
            "• If claim was false: player who made false claim uses revolver",
            "",
            "REVOLVER:",
            "• Russian roulette with random bullet placement",
            "• Blank chamber: player continues",
            "• Bullet: player is eliminated",
            "",
            "WINNING:",
            "• Last player standing wins",
            "• Player who runs out of cards wins"
        };
        
        for (String rule : rules) {
            if (rule.isEmpty()) {
                System.out.println();
            } else {
                System.out.println(rule);
            }
        }
    }
    
    private void clearScreen() {
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
    
    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        
        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        
        while (sb.length() < width) {
            sb.append(" ");
        }
        
        return sb.toString();
    }
}