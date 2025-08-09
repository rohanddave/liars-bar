package com.tfc.liarsbar.model.actions;

import com.tfc.liarsbar.model.game.Card;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;


/**
 * Strategy for making a claim action
 */
public class ClaimAction implements GameAction {
  private final Scanner scanner;
  
  public ClaimAction(Scanner scanner) {
    this.scanner = scanner;
  }
  
  @Override
  public ActionResult execute(Game game, Player player) {
    try {
      System.out.print("📊 Enter count of " + game.getRank() + "(s) to claim: ");
      int count = scanner.nextInt();
      
      List<Card> discardedCards = new ArrayList<>(count);
      for (int i = 0; i < count; i++) {
        printHand(player);
        System.out.print("🃏 Enter card number to discard (" + (i + 1) + "/" + count + "): ");
        int cardIndex = scanner.nextInt();
        discardedCards.add(player.getHand().getAt(cardIndex));
      }
      
      System.out.println("📝 Processing claim...");
      game.claim(player, count, discardedCards, game.getRank());
      game.moveToNextMove();
      
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
    return player.equals(game.getCurrentPlayer()) && 
           player.getHand().getSize() > 0 &&
           !game.isGameOver();
  }
  
  private void printHand(Player player) {
    System.out.println("🃏 " + player.getName() + "'s Hand:");
    System.out.println("╔════════════════════════════╗");
    System.out.println("║ " + player.getHand().toString() + " ║");
    System.out.println("╚════════════════════════════╝");
  }
}