package model.events;

/**
 * Base interface for all game events
 */
public interface GameEvent {
  /**
   * Gets the type of event
   * @return The event type
   */
  GameEventType getEventType();
  
  /**
   * Gets the message associated with this event
   * @return Event message
   */
  String getMessage();
  
  /**
   * Gets the timestamp when the event occurred
   * @return Event timestamp
   */
  long getTimestamp();
}