package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.game.Card;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Set;


/**
 * Strategy for making a claim action
 */
public class ClaimAction implements GameAction {
  private int claimCount;
  private Set<Integer> discardIndices;

  public ClaimAction(int claimCount, Set<Integer> discardIndices) {
    this.claimCount = claimCount;
    this.discardIndices = discardIndices;
  }

  @Override
  public ActionResult execute(Game game, Player player) {
    try {
      // TODO: remove hard coded count value
      int count = this.claimCount;
      List<Card> discardedCards = new ArrayList<>(count);

      for(int cardIndex: this.discardIndices) {
        discardedCards.add(player.getHand().getAt(cardIndex));
      }

      System.out.println("📝 Processing claim...");
      game.claim(player, count, discardedCards, game.getRank());

      return ActionResult.success("Claim processed successfully");
      
    } catch (Exception e) {
      return ActionResult.failure("Failed to process claim: " + e.getMessage());
    }
  }
  
  @Override
  public String getActionName() {
    return "Play Claim";
  }
  
  @Override
  public boolean isValidFor(Game game, Player player) {
    boolean isCurrentPlayer = player.equals(game.getCurrentPlayer());
    boolean isValidHandSize = player.getHand().getSize() >= this.claimCount;

    System.out.println("isCurrentPlayer: " + isCurrentPlayer + "\t isValidHandSize: " + isValidHandSize);
    return isCurrentPlayer && isValidHandSize && !game.isGameOver();
  }
  
  private void printHand(Player player) {
    System.out.println("🃏 " + player.getName() + "'s Hand:");
    System.out.println("╔════════════════════════════╗");
    System.out.println("║ " + player.getHand().toString() + " ║");
    System.out.println("╚════════════════════════════╝");
  }
}