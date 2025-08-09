package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

import java.util.List;
import java.util.ArrayList;

/**
 * Factory for creating game actions
 */
public class ActionFactory {
  private final GameEventPublisher gameEventPublisher;

  public ActionFactory(GameEventPublisher gameEventPublisher) {
    this.gameEventPublisher = gameEventPublisher;
  }
  
  /**
   * Gets all available actions for a player in the current game state
   * @param game The game context
   * @param player The player
   * @return List of valid actions
   */
  public List<GameAction> getAvailableActions(Game game, Player player) {
    List<GameAction> actions = new ArrayList<>();
    
    // Note: ClaimAction now requires parameters, so we skip it in generic available actions
    // ClaimAction claimAction = new ClaimAction();
    // if (claimAction.isValidFor(game, player)) {
    //   actions.add(claimAction);
    // }
    
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
    return switch (actionName.toLowerCase()) {
      case "claim", "play claim" -> null; // ClaimAction requires parameters, handled by CommandParser
      case "challenge" -> new ChallengeAction();
      case "shoot" -> new ShootAction();
      case "start", "begin", "init" -> new StartAction();
      default -> null;
    };
  }
}