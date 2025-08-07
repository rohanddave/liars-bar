package model.actions;

import model.game.Game;
import model.game.Player;

/**
 * Strategy for challenging a claim action
 */
public class ChallengeAction implements GameAction {
  
  @Override
  public ActionResult execute(Game game, Player player) {
    try {
      System.out.println("⚔️ Challenging the last claim...");
      Player loser = game.challengeClaim(player);
      loser.shoot();
      game.moveToNextMove();
      
      return ActionResult.success("Challenge processed successfully", loser);
      
    } catch (Exception e) {
      return ActionResult.failure("Failed to process challenge: " + e.getMessage());
    }
  }
  
  @Override
  public String getActionName() {
    return "Challenge";
  }
  
  @Override
  public boolean isValidFor(Game game, Player player) {
    return player.equals(game.getCurrentPlayer()) && 
           game.getLastClaim() != null &&
           !player.equals(game.getLastClaim().getPlayer()) && // Can't challenge own claim
           !game.isGameOver();
  }
}