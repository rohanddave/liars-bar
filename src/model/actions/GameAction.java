package model.actions;

import model.game.Game;
import model.game.Player;

/**
 * Base interface for all game actions using Strategy pattern
 */
public interface GameAction {
  /**
   * Executes the action for the given player in the given game
   * @param game The game context
   * @param player The player performing the action
   * @return ActionResult containing the outcome of the action
   */
  ActionResult execute(Game game, Player player);
  
  /**
   * Gets the name of this action
   * @return Action name for display
   */
  String getActionName();
  
  /**
   * Checks if this action is valid for the given player in the current game state
   * @param game The game context
   * @param player The player wanting to perform the action
   * @return true if action is valid, false otherwise
   */
  boolean isValidFor(Game game, Player player);
}