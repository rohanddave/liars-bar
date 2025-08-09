package com.tfc.liarsbar.model.game;

import com.tfc.liarsbar.model.exceptions.NoSuchCardException;

import java.util.List;

/**
 * This interface represents a Player of the game Liars Bar.
 */
public interface Player {
  String getId();

  String getName();

  Claim claim(Rank rank, int count, List<Card> droppedCards) throws NoSuchCardException;

  void challengeClaim(Claim claim);

  boolean shoot();

  boolean isAlive();

  Hand getHand();

  void setHand(Hand hand);

  Revolver getRevolver();

  void setRevolver(Revolver revolver);
}
