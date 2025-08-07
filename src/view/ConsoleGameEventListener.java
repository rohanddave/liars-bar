package view;

import model.events.GameEvent;
import model.events.GameEventListener;

/**
 * Console implementation of GameEventListener for displaying game events
 */
public class ConsoleGameEventListener implements GameEventListener {
  
  @Override
  public void onGameEvent(GameEvent event) {
    switch (event.getEventType()) {
      case GAME_STARTED:
        System.out.println("🎮 " + event.getMessage());
        break;
      case GAME_ENDED:
        System.out.println("🏆 " + event.getMessage());
        break;
      case PLAYER_INITIALIZED:
        System.out.println("  ✅ " + event.getMessage());
        break;
      case ROUND_STARTED:
        System.out.println("🎲 " + event.getMessage());
        break;
      case ROUND_ENDED:
        System.out.println("🏁 " + event.getMessage());
        break;
      case CLAIM_MADE:
        System.out.println("🃏 " + event.getMessage());
        break;
      case CHALLENGE_MADE:
        System.out.println("⚔️ " + event.getMessage());
        break;
      case CHALLENGE_RESULT:
        System.out.println("  " + (event.getMessage().contains("successful") ? "✅" : "❌") + " " + event.getMessage());
        break;
      case PLAYER_SHOT:
        System.out.println("🔫 " + event.getMessage());
        break;
      case PLAYER_ELIMINATED:
        System.out.println("💥 " + event.getMessage());
        break;
      case TURN_CHANGED:
        System.out.println("🔄 " + event.getMessage());
        break;
      case ROOM_JOINED:
        System.out.println("✅ " + event.getMessage());
        break;
      default:
        System.out.println(event.getMessage());
    }
  }
}