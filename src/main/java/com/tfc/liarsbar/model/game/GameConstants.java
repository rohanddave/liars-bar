package com.tfc.liarsbar.model.game;

/**
 * Constants used throughout the Liar's Bar game
 */
public final class GameConstants {
  
  // Game configuration
  public static final int MAX_PLAYERS = 4;
  public static final int MIN_PLAYERS = 2;
  public static final int INITIAL_HAND_SIZE = 5;
  public static final int CARDS_PER_RANK = 4; // In a standard deck
  
  // Room configuration
  public static final int DEFAULT_ROOM_CAPACITY = 4;
  
  // Revolver configuration
  public static final int REVOLVER_CHAMBERS = 6;
  public static final int BULLETS_PER_REVOLVER = 1;
  
  // Round configuration
  public static final Rank[] ROUND_SEQUENCE = {Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK};
  
  // Prevent instantiation
  private GameConstants() {
    throw new UnsupportedOperationException("Constants class cannot be instantiated");
  }
}