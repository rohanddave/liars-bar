package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Strategy for showing hand.
 */
public class ShowHandAction implements GameAction {

  @Override
  public ActionResult execute(Game game, Player player) {
    try {
      String message = "Showing player " + player.getName() +" their hand \n" + player.getHand().toString();
      System.out.println(message);
      return ActionResult.success(message);
    } catch (Exception e) {
      return ActionResult.failure("Failed to process shot: " + e.getMessage());
    }
  }

  @Override
  public String getActionName() {
    return "Show Hand";
  }

  @Override
  public boolean isValidFor(Game game, Player player) {
    return player.isAlive() && player.getHand() != null && player.getHand().getSize() > 0 &&
            !game.isGameOver();
  }
}