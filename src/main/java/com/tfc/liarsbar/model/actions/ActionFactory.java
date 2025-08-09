package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Factory for creating game actions
 */
public class ActionFactory {
  private final Scanner scanner;
  
  public ActionFactory(Scanner scanner) {
    this.scanner = scanner;
  }
  
  /**
   * Gets all available actions for a player in the current game state
   * @param game The game context
   * @param player The player
   * @return List of valid actions
   */
  public List<GameAction> getAvailableActions(Game game, Player player) {
    List<GameAction> actions = new ArrayList<>();
    
    ClaimAction claimAction = new ClaimAction(scanner);
    if (claimAction.isValidFor(game, player)) {
      actions.add(claimAction);
    }
    
    ChallengeAction challengeAction = new ChallengeAction();
    if (challengeAction.isValidFor(game, player)) {
      actions.add(challengeAction);
    }
    
    ShootAction shootAction = new ShootAction();
    if (shootAction.isValidFor(game, player)) {
      actions.add(shootAction);
    }
    
    return actions;
  }
  
  /**
   * Creates a specific action by name
   * @param actionName The name of the action
   * @return The action instance or null if not found
   */
  public GameAction createAction(String actionName) {
    switch (actionName.toLowerCase()) {
      case "claim":
      case "play claim":
        return new ClaimAction(scanner);
      case "challenge":
        return new ChallengeAction();
      case "shoot":
        return new ShootAction();
      default:
        return null;
    }
  }
}