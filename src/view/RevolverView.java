package view;

import model.game.Player;
import util.InputHandler;

/**
 * View class for handling revolver-related terminal interactions and displays.
 * Provides visual feedback for revolver spinning, outcomes, and player elimination.
 */
public class RevolverView {
    private static final String BORDER = "═".repeat(60);
    private final InputHandler inputHandler;
    
    public RevolverView(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }
    
    /**
     * Displays the revolver spinning interface and waits for player input.
     * @param player The player who must spin the revolver
     */
    public void displayRevolverSpin(Player player) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("🔫 REVOLVER TIME 🔫", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ %s must spin the revolver%-28s ║%n", player.getId(), "");
        System.out.printf("║%s║%n", centerText("Russian Roulette - 1 bullet, 6 chambers", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("Press Enter to spin the cylinder...", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        // Wait for player input
        inputHandler.waitForEnter();
    }
    
    /**
     * Displays the spinning animation and chamber selection.
     * @param chamberPosition The chamber position (1-6) where the bullet will be
     */
    public void displaySpinningAnimation(int chamberPosition) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("SPINNING CYLINDER...", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        // Show spinning animation
        String[] spinFrames = {"⚪", "⚫", "⚪", "⚫", "⚪", "⚫"};
        for (int i = 0; i < 12; i++) {
            System.out.print("\r" + centerText("🔫 " + spinFrames[i % 6] + " 🔫", 60));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
        System.out.println();
        
        // Show chamber visualization
        displayChamberVisualization(chamberPosition, false);
    }
    
    /**
     * Displays the revolver chamber visualization.
     * @param bulletChamber The chamber containing the bullet (1-6)
     * @param revealed Whether to reveal the bullet location
     */
    public void displayChamberVisualization(int bulletChamber, boolean revealed) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("REVOLVER CHAMBERS", 60));
        System.out.println("╠" + BORDER + "╣");
        
        // Draw the cylinder with 6 chambers in a circle
        System.out.printf("║%s║%n", centerText("     ⚪", 60));
        System.out.printf("║%s║%n", centerText("  ⚪     ⚪", 60));
        System.out.printf("║%s║%n", centerText("🔫         ", 60));
        System.out.printf("║%s║%n", centerText("  ⚪     ⚪", 60));
        System.out.printf("║%s║%n", centerText("     ⚪", 60));
        
        if (revealed) {
            System.out.println("╠" + BORDER + "╣");
            System.out.printf("║%s║%n", centerText("💥 Bullet was in chamber " + bulletChamber + " 💥", 60));
        }
        
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Displays the trigger pull interface.
     * @param player The player pulling the trigger
     */
    public void displayTriggerPull(Player player) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("PULLING TRIGGER", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ %s aims the revolver%-32s ║%n", player.getId(), "");
        System.out.printf("║%s║%n", centerText("🔫 → 😰", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("Press Enter to pull the trigger...", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        // Wait for player input
        inputHandler.waitForEnter();
    }
    
    /**
     * Displays the revolver outcome with dramatic effect.
     * @param player The player who spun the revolver
     * @param eliminated Whether the player was eliminated
     * @param chamberPosition The chamber that was fired
     */
    public void displayRevolverOutcome(Player player, boolean eliminated, int chamberPosition) {
        // Clear screen for dramatic effect
        System.out.println("\n".repeat(5));
        
        if (eliminated) {
            displayEliminationOutcome(player, chamberPosition);
        } else {
            displaySurvivalOutcome(player, chamberPosition);
        }
        
        // Pause for dramatic effect
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Displays the elimination outcome.
     * @param player The eliminated player
     * @param chamberPosition The chamber that contained the bullet
     */
    private void displayEliminationOutcome(Player player, int chamberPosition) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("💥 BANG! 💥", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("The bullet was in chamber " + chamberPosition, 60));
        System.out.printf("║%s║%n", centerText(player.getId() + " has been eliminated!", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("🪦 R.I.P. " + player.getId() + " 🪦", 60));
        System.out.printf("║%s║%n", centerText("Better luck next time!", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        // Show dramatic elimination effect
        for (int i = 0; i < 3; i++) {
            System.out.println(centerText("💥 ELIMINATED 💥", 60));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Displays the survival outcome.
     * @param player The surviving player
     * @param chamberPosition The empty chamber that was fired
     */
    private void displaySurvivalOutcome(Player player, int chamberPosition) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("🔫 *CLICK* 🔫", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("Chamber " + chamberPosition + " was empty!", 60));
        System.out.printf("║%s║%n", centerText(player.getId() + " survives!", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║%s║%n", centerText("😅 Lucky escape! 😅", 60));
        System.out.printf("║%s║%n", centerText("The game continues...", 60));
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
        
        // Show relief effect
        for (int i = 0; i < 2; i++) {
            System.out.println(centerText("😰 → 😅 SURVIVED!", 60));
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Displays player elimination status update.
     * @param player The eliminated player
     * @param remainingPlayers Number of players still alive
     */
    public void displayPlayerElimination(Player player, int remainingPlayers) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("PLAYER ELIMINATED", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Eliminated: %-45s ║%n", player.getId());
        System.out.printf("║ Players remaining: %-36s ║%n", remainingPlayers);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Displays revolver statistics for a player.
     * @param player The player
     * @param chamberPosition Current chamber position (1-6)
     * @param shotsRemaining Number of shots before bullet
     */
    public void displayRevolverStats(Player player, int chamberPosition, int shotsRemaining) {
        System.out.println("╔" + BORDER + "╗");
        System.out.printf("║%s║%n", centerText("REVOLVER STATUS", 60));
        System.out.println("╠" + BORDER + "╣");
        System.out.printf("║ Player: %-49s ║%n", player.getId());
        System.out.printf("║ Current chamber: %-40s ║%n", chamberPosition);
        System.out.printf("║ Shots until bullet: %-36s ║%n", shotsRemaining);
        System.out.println("╚" + BORDER + "╝");
        System.out.println();
    }
    
    /**
     * Centers text within a given width.
     * @param text The text to center
     * @param width The total width
     * @return Centered text string
     */
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