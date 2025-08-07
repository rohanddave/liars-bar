package model.events;

/**
 * Interface for listening to game events
 */
public interface GameEventListener {
  /**
   * Called when a game event occurs
   * @param event The game event that occurred
   */
  void onGameEvent(GameEvent event);
}