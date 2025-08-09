package view;

import controller.GameController;
import util.InputHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Main terminal interface class for the multiplayer Liar's Bar game.
 * Handles the complete user interaction flow including menu system,
 * player setup, and game state display.
 */
public class GameTerminal {
    private final SimpleDisplayManager displayManager;
    private final InputHandler inputHandler;
    private GameController controller;
    private boolean gameRunning;
    
    public GameTerminal() {
        this.displayManager = new SimpleDisplayManager();
        this.inputHandler = new InputHandler();
        this.gameRunning = false;
    }
    
    /**
     * Sets the game controller for this terminal interface.
     * @param controller The game controller to use
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }
    
    /**
     * Starts the game terminal interface.
     * Shows welcome message and main menu.
     */
    public void startGame() {
        displayManager.showWelcomeMessage();
        showMainMenu();
    }
    
    /**
     * Displays the main menu and handles user selection.
     * Provides options to start new game, view rules, or exit.
     */
    public void showMainMenu() {
        while (true) {
            displayManager.clearScreen();
            displayManager.showWelcomeMessage();
            
            List<String> menuOptions = new ArrayList<>();
            menuOptions.add("Start New Game");
            menuOptions.add("View Game Rules");
            menuOptions.add("Exit");
            
            displayManager.showActionsMenu(menuOptions);
            
            int choice = inputHandler.getMenuChoice(menuOptions.size());
            
            switch (choice) {
                case 1:
                    if (startNewGame()) {
                        return; // Exit menu to start game
                    }
                    break;
                case 2:
                    showGameRules();
                    break;
                case 3:
                    displayManager.showMessage("Thanks for playing Liar's Bar!");
                    cleanup();
                    System.exit(0);
                    break;
            }
        }
    }
    
    /**
     * Initiates the new game setup flow.
     * Handles player count selection and player name entry.
     * @return true if game setup was successful, false if cancelled
     */
    private boolean startNewGame() {
        displayManager.clearScreen();
        displayManager.showMessage("Setting up new game...");
        
        // Get number of players
        int playerCount = inputHandler.getPlayerCount();
        
        // Get player names
        List<String> playerNames = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            displayManager.showMessage("Player " + i + " setup:");
            String name = inputHandler.getPlayerName();
            
            // Check for duplicate names
            if (playerNames.contains(name)) {
                displayManager.showError("Player name '" + name + "' is already taken. Please choose a different name.");
                i--; // Retry this player
                continue;
            }
            
            playerNames.add(name);
        }
        
        // Confirm game setup
        displayManager.showMessage("Game Setup Complete!");
        displayManager.showMessage("Players: " + String.join(", ", playerNames));
        
        if (inputHandler.confirmAction("Start the game?")) {
            gameRunning = true;
            // Initialize game with controller if available
            if (controller != null) {
                // Note: This will be implemented when GameController interface is enhanced
                displayManager.showMessage("Starting game with " + playerCount + " players...");
                inputHandler.waitForEnter();
            } else {
                displayManager.showError("Game controller not available. Cannot start game.");
                inputHandler.waitForEnter();
                return false;
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Displays the game rules to the user.
     */
    private void showGameRules() {
        displayManager.clearScreen();
        System.out.println("==================== GAME RULES ====================");
        System.out.println();
        System.out.println("LIAR'S BAR - Multiplayer Bluffing Game");
        System.out.println();
        System.out.println("SETUP:");
        System.out.println("• 2-4 players");
        System.out.println("• Each player starts with 5 cards");
        System.out.println("• Deck: 6 Kings, 6 Queens, 6 Jacks, 2 Aces (wildcards)");
        System.out.println();
        System.out.println("GAMEPLAY:");
        System.out.println("• Players take turns making claims about cards they play");
        System.out.println("• Claims must match the current round's card type");
        System.out.println("• Aces count as wildcards (can be any card type)");
        System.out.println("• Other players can either add to the claim or challenge it");
        System.out.println();
        System.out.println("CHALLENGES:");
        System.out.println("• When challenged, all played cards are revealed");
        System.out.println("• If claim was true: challenger uses revolver");
        System.out.println("• If claim was false: player who made false claim uses revolver");
        System.out.println();
        System.out.println("REVOLVER:");
        System.out.println("• Russian roulette with random bullet placement");
        System.out.println("• Blank chamber: player continues");
        System.out.println("• Bullet: player is eliminated");
        System.out.println();
        System.out.println("WINNING:");
        System.out.println("• Last player standing wins");
        System.out.println("• Player who runs out of cards wins");
        System.out.println();
        System.out.println("====================================================");
        
        inputHandler.waitForEnter();
    }
    
    /**
     * Displays a basic game state message.
     * This is a placeholder until GameState integration is complete.
     * @param message Game state message to display
     */
    public void displayGameStateMessage(String message) {
        if (message == null || message.isEmpty()) {
            displayManager.showError("No game state available");
            return;
        }
        
        displayManager.showMessage("Game State: " + message);
    }
    
    /**
     * Displays the current game state with full information.
     * @param players List of all players
     * @param currentPlayer The player whose turn it is
     * @param roundNumber Current round number
     * @param currentRank Current card rank for the round
     * @param currentClaim Current claim if any
     * @param lastAction Last action description
     */
    public void displayFullGameState(java.util.List<model.game.Player> players, 
                                   model.game.Player currentPlayer, 
                                   int roundNumber,
                                   model.game.Rank currentRank, 
                                   model.game.Claim currentClaim, 
                                   String lastAction) {
        displayManager.renderGameState(players, currentPlayer, roundNumber, 
                                     currentRank, currentClaim, lastAction);
    }
    
    /**
     * Displays a player's hand with card indices.
     * @param hand Player's hand to display
     */
    public void displayPlayerHand(model.game.Hand hand) {
        if (hand == null) {
            displayManager.showError("No hand available to display");
            return;
        }
        
        displayManager.renderPlayerHand(hand);
    }
    
    /**
     * Displays the current claim information.
     * @param claim Current claim to display
     * @param rank The rank being claimed
     */
    public void displayCurrentClaim(model.game.Claim claim, model.game.Rank rank) {
        if (claim == null) {
            displayManager.showMessage("No active claim");
            return;
        }
        
        displayManager.renderCurrentClaim(claim, rank);
    }
    
    /**
     * Displays player status information.
     * @param players List of all players
     * @param currentPlayer The player whose turn it is
     */
    public void displayPlayerStatus(java.util.List<model.game.Player> players, 
                                  model.game.Player currentPlayer) {
        if (players == null || players.isEmpty()) {
            displayManager.showError("No players to display");
            return;
        }
        
        displayManager.renderPlayerStatus(players, currentPlayer);
    }
    
    /**
     * Displays round information.
     * @param currentRank Current round's target card type
     */
    public void displayRoundInfo(model.game.Rank currentRank) {
        displayManager.renderRoundInfo(currentRank);
    }
    
    /**
     * Displays game over message with winner.
     * @param winner The winning player
     */
    public void displayGameOver(model.game.Player winner) {
        displayManager.showGameOverMessage(winner);
    }
    
    /**
     * Handles player input during the game.
     * Processes user actions and coordinates with the game controller.
     */
    public void handlePlayerInput() {
        if (!gameRunning) {
            displayManager.showError("No active game session");
            return;
        }
        
        // This will be enhanced when GameController interface is expanded
        List<String> actions = new ArrayList<>();
        actions.add("Make Claim");
        actions.add("Challenge Claim");
        actions.add("View Hand");
        actions.add("View Rules");
        actions.add("Quit Game");
        
        displayManager.showActionsMenu(actions);
        
        int choice = inputHandler.getMenuChoice(actions.size());
        
        switch (choice) {
            case 1:
                handleMakeClaim();
                break;
            case 2:
                handleChallenge();
                break;
            case 3:
                handleViewHand();
                break;
            case 4:
                showGameRules();
                break;
            case 5:
                if (inputHandler.confirmAction("Are you sure you want to quit?")) {
                    gameRunning = false;
                    showMainMenu();
                }
                break;
        }
    }
    
    /**
     * Handles the make claim action.
     */
    private void handleMakeClaim() {
        displayManager.showMessage("Making a claim...");
        
        // Get claim count
        int count = inputHandler.getCardCount();
        
        // Get card selection (placeholder - will need actual hand size)
        List<Integer> cardIndices = inputHandler.getCardIndices(5); // Assuming max 5 cards
        
        displayManager.showMessage("Claim: " + count + " cards selected at indices " + cardIndices);
        
        // This will integrate with GameController when available
        inputHandler.waitForEnter();
    }
    
    /**
     * Handles the challenge action.
     */
    private void handleChallenge() {
        displayManager.showMessage("Challenging the current claim...");
        
        boolean challenge = inputHandler.getChallenge();
        
        if (challenge) {
            displayManager.showMessage("You chose to challenge!");
            // This will integrate with GameController when available
        } else {
            displayManager.showMessage("Challenge cancelled.");
        }
        
        inputHandler.waitForEnter();
    }
    
    /**
     * Handles viewing the player's hand.
     */
    private void handleViewHand() {
        displayManager.showMessage("Your current hand:");
        // This will show actual hand when integrated with GameController
        displayManager.showMessage("(Hand display will be implemented with game integration)");
        inputHandler.waitForEnter();
    }
    
    /**
     * Shows a message to the user.
     * @param message Message to display
     */
    public void showMessage(String message) {
        displayManager.showMessage(message);
    }
    
    /**
     * Shows an error message to the user.
     * @param error Error message to display
     */
    public void showError(String error) {
        displayManager.showError(error);
    }
    
    /**
     * Cleans up resources when the game terminal is closed.
     */
    public void cleanup() {
        gameRunning = false;
        inputHandler.close();
    }
    
    /**
     * Checks if the game is currently running.
     * @return true if game is active, false otherwise
     */
    public boolean isGameRunning() {
        return gameRunning;
    }
}