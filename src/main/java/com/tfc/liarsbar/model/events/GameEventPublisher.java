package com.tfc.liarsbar.model.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Publisher for game events using Observer pattern
 */
public class GameEventPublisher {
  private final List<GameEventListener> listeners = new CopyOnWriteArrayList<>();
  
  /**
   * Adds a listener for game events
   * @param listener The listener to add
   */
  public void addListener(GameEventListener listener) {
    listeners.add(listener);
  }
  
  /**
   * Removes a listener for game events
   * @param listener The listener to remove
   */
  public void removeListener(GameEventListener listener) {
    listeners.remove(listener);
  }
  
  /**
   * Publishes an event to all registered listeners
   * @param event The event to publish
   */
  public void publishEvent(GameEvent event) {
    for (GameEventListener listener : listeners) {
      try {
        listener.onGameEvent(event);
      } catch (Exception e) {
        // Log the exception but don't let one listener break others
        System.err.println("Error in event listener: " + e.getMessage());
      }
    }
  }
  
  /**
   * Convenience method to publish an event with type and message
   * @param eventType The type of event
   * @param message The event message
   */
  public void publishEvent(GameEventType eventType, String message) {
    publishEvent(new GameEventImpl(eventType, message));
  }
}