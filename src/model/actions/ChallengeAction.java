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
      boolean wasEliminated = loser.shoot();
      
      // Always move to the next person's turn after a challenge
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
    if (game.getLastClaim() == null) {
      return false;
    }
    
    Player lastClaimPlayer = game.getLastClaim().getPlayer();
    
    return player.equals(game.getCurrentPlayer()) && 
           lastClaimPlayer != null &&
           lastClaimPlayer.isAlive() && // Check if the player who made the claim is still alive
           !player.equals(lastClaimPlayer) && // Can't challenge own claim
           !game.isGameOver();
  }
}