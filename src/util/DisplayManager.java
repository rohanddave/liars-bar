package util;

import model.game.*;
import java.util.List;

/**
 * Manages terminal display formatting and output for the game.
 * Provides methods for rendering game state, player information, and UI elements.
 */
public class DisplayManager {
    private static final String SEPARATOR = "================================================";
    private static final String TITLE = "LIAR'S BAR";
    
    /**
     * Clears the terminal screen.
     */
    public void clearScreen() {
        // ANSI escape code to clear screen and move cursor to top
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
    
    /**
     * Shows the welcome message and game title.
     */
    public void showWelcomeMessage() {
        clearScreen();
        System.out.println(SEPARATOR);
        System.out.println(centerText(TITLE));
        System.out.println(SEPARATOR);
        System.out.println();
        System.out.println("Welcome to Liar's Bar!");
        System.out.println("A multiplayer bluffing game for 2-4 players.");
        System.out.println();
    }
    
    /**
     * Renders the current game state to terminal.
     * @param state Current game state
     * @param currentPlayer The player whose turn it is
     */
    public void renderGameState(GameState state, Player currentPlayer) {
        renderGameState(state, currentPlayer, 1, Rank.KING, null, null);
    }
    
    /**
     * Renders the current game state to terminal with additional context.
     * @param state Current game state
     * @param currentPlayer The player whose turn it is
     * @param roundNumber Current round number
     * @param currentRank Current card rank for the round
     * @param currentClaim Current claim if any
     * @param lastAction Last action description
     */
    public void renderGameState(GameState state, Player currentPlayer, int roundNumber, 
                               Rank currentRank, Claim currentClaim, String lastAction) {
        clearScreen();
        System.out.println(SEPARATOR);
        System.out.println(centerText(TITLE));
        System.out.println(SEPARATOR);
        
        // Game info header
        System.out.printf("Round: %d | Current Card: %s | Players: %d%n", 
            roundNumber, 
            getCurrentRankDisplay(currentRank),
            getActivePlayerCount(state.getPlayers()));
        System.out.println();
        
        // Player status
        renderPlayerStatus(state.getPlayers(), currentPlayer);
        System.out.println();
        
        // Current claim if exists
        if (currentClaim != null) {
            renderCurrentClaim(currentClaim);
            System.out.println();
        }
        
        // Last action
        if (lastAction != null && !lastAction.isEmpty()) {
            System.out.println("Last Action: " + lastAction);
            System.out.println();
        }
    }
    
    /**
     * Renders a player's hand with card indices.
     * @param hand Player's hand to display
     */
    public void renderPlayerHand(Hand hand) {
        System.out.println("Your Hand:");
        
        // Since Hand interface doesn't expose size or getCards, we'll iterate until we hit an exception
        int index = 0;
        while (true) {
            try {
                Card card = hand.getAt(index);
                System.out.printf("[%d] %-6s ", index + 1, getCardDisplay(card));
                index++;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        System.out.println();
        System.out.println();
    }
    
    /**
     * Renders the current claim information.
     * @param claim Current claim to display
     */
    public void renderCurrentClaim(Claim claim) {
        renderCurrentClaim(claim, Rank.KING); // Default rank
    }
    
    /**
     * Renders the current claim information with specified rank.
     * @param claim Current claim to display
     * @param rank The rank being claimed
     */
    public void renderCurrentClaim(Claim claim, Rank rank) {
        if (claim != null) {
            System.out.printf("Current Claim: %s claims %d %s%n", 
                claim.getPlayer().getId(), // Using getId() since getName() doesn't exist
                claim.getCount(),
                getCurrentRankDisplay(rank));
        }
    }
    
    /**
     * Renders player status showing card counts and elimination status.
     * @param players List of all players
     * @param currentPlayer The player whose turn it is
     */
    public void renderPlayerStatus(List<Player> players, Player currentPlayer) {
        System.out.println("Player Status:");
        
        for (Player player : players) {
            String indicator = player.equals(currentPlayer) ? "[*]" : "[ ]";
            String status = player.isAlive() ? 
                String.format("(%d cards)", getHandSize(player.getHand())) : 
                "(ELIMINATED)";
            
            System.out.printf("%s %-12s %-12s %s%n", 
                indicator, 
                player.getId(), // Using getId() since getName() doesn't exist
                status,
                getHealthBar(player));
        }
    }
    
    /**
     * Renders round information.
     * @param currentRank Current round's target card type
     */
    public void renderRoundInfo(Rank currentRank) {
        System.out.println("Round Information:");
        System.out.printf("Current Card Type: %s%n", getCurrentRankDisplay(currentRank));
        System.out.println("Players must claim cards of this type (Aces count as wildcards)");
        System.out.println();
    }
    
    /**
     * Shows game over message with winner.
     * @param winner The winning player
     */
    public void showGameOverMessage(Player winner) {
        clearScreen();
        System.out.println(SEPARATOR);
        System.out.println(centerText("GAME OVER"));
        System.out.println(SEPARATOR);
        System.out.println();
        
        if (winner != null) {
            System.out.println(centerText("🎉 " + winner.getId() + " WINS! 🎉"));
        } else {
            System.out.println(centerText("Game ended"));
        }
        
        System.out.println();
        System.out.println(SEPARATOR);
    }
    
    /**
     * Shows a formatted message to the user.
     * @param message Message to display
     */
    public void showMessage(String message) {
        System.out.println();
        System.out.println(">>> " + message);
        System.out.println();
    }
    
    /**
     * Shows an error message with formatting.
     * @param error Error message to display
     */
    public void showError(String error) {
        System.out.println();
        System.out.println("❌ ERROR: " + error);
        System.out.println();
    }
    
    /**
     * Shows available actions menu.
     * @param actions List of available actions
     */
    public void showActionsMenu(List<String> actions) {
        System.out.println("Available Actions:");
        for (int i = 0; i < actions.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, actions.get(i));
        }
        System.out.println();
    }
    
    /**
     * Centers text within the separator width.
     * @param text Text to center
     * @return Centered text
     */
    private String centerText(String text) {
        int padding = (SEPARATOR.length() - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        return sb.toString();
    }
    
    /**
     * Gets display string for a card.
     * @param card Card to display
     * @return Formatted card string
     */
    private String getCardDisplay(Card card) {
        return card.getRank().toString();
    }
    
    /**
     * Gets display string for current rank.
     * @param rank Rank to display
     * @return Formatted rank string
     */
    private String getCurrentRankDisplay(Rank rank) {
        if (rank == null) return "UNKNOWN";
        return rank.toString();
    }
    
    /**
     * Gets health bar representation for player.
     * @param player Player to show health for
     * @return Health bar string
     */
    private String getHealthBar(Player player) {
        if (!player.isAlive()) {
            return "[ELIMINATED]";
        }
        
        int cardCount = getHandSize(player.getHand());
        StringBuilder bar = new StringBuilder("[");
        
        // Show filled circles for cards
        for (int i = 0; i < cardCount; i++) {
            bar.append("●");
        }
        
        // Show empty circles for missing cards (up to 5 total)
        for (int i = cardCount; i < 5; i++) {
            bar.append("○");
        }
        
        bar.append("]");
        return bar.toString();
    }
    
    /**
     * Gets the size of a hand by iterating through it.
     * @param hand Hand to measure
     * @return Number of cards in hand
     */
    private int getHandSize(Hand hand) {
        int size = 0;
        while (true) {
            try {
                hand.getAt(size);
                size++;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return size;
    }
    
    /**
     * Counts active (alive) players.
     * @param players List of all players
     * @return Number of active players
     */
    private int getActivePlayerCount(List<Player> players) {
        int count = 0;
        for (Player player : players) {
            if (player.isAlive()) {
                count++;
            }
        }
        return count;
    }
}