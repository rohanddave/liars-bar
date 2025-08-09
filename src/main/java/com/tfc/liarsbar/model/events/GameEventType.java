package com.tfc.liarsbar.model.events;

/**
 * Enumeration of all possible game event types
 */
public enum GameEventType {
  GAME_STARTED,
  GAME_ENDED,
  PLAYER_INITIALIZED,
  ROUND_STARTED,
  ROUND_ENDED,
  CLAIM_MADE,
  CHALLENGE_MADE,
  CHALLENGE_RESULT,
  PLAYER_SHOT,
  PLAYER_ELIMINATED,
  TURN_CHANGED,
  ROOM_JOINED
}