package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

/**
 * Strategy for self-shooting action
 */
public class ShootAction implements GameAction {
  
  @Override
  public ActionResult execute(Game game, Player player) {
    try {
      System.out.println("🔫 Player chooses to shoot themselves...");
      boolean eliminated = player.shoot();
      game.moveToNextMove();
      
      if (eliminated) {
        return ActionResult.success("Player eliminated themselves", eliminated);
      } else {
        return ActionResult.success("Player survived the shot", eliminated);
      }
      
    } catch (Exception e) {
      return ActionResult.failure("Failed to process shot: " + e.getMessage());
    }
  }
  
  @Override
  public String getActionName() {
    return "Shoot";
  }
  
  @Override
  public boolean isValidFor(Game game, Player player) {
    return player.equals(game.getCurrentPlayer()) && 
           player.isAlive() &&
           !game.isGameOver();
  }
}