package com.tfc.liarsbar.model.actions;


import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

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
      
      // Mark the claim as settled after the challenge is resolved
      game.settleLastClaim();
      
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
      System.out.println("No last claim found");
      return false;
    }
    
    Player lastClaimPlayer = game.getLastClaim().getPlayer();

    boolean isCurrentPlayer = player.equals(game.getCurrentPlayer());
    boolean isLastClaimPlayerAlive = lastClaimPlayer != null &&
            lastClaimPlayer.isAlive();
    boolean isChallengingOwnClaim = player.equals(lastClaimPlayer);
    boolean isGameOver = game.isGameOver();

    System.out.println("isCurrentPlayer: " + isCurrentPlayer + "\t isLastClaimPlayerAlive: " + isLastClaimPlayerAlive + "\t isChallengingOwnClaim: " + isChallengingOwnClaim + "\t isGameOver: " + isGameOver);

    return isCurrentPlayer &&
           isLastClaimPlayerAlive && // Check if the player who made the claim is still alive
           !isChallengingOwnClaim && // Can't challenge own claim
           !isGameOver;
  }
}