package com.tfc.liarsbar.model.events;

/**
 * Standard implementation of GameEvent
 */
public class GameEventImpl implements GameEvent {
  private final GameEventType eventType;
  private final String message;
  private final long timestamp;
  
  public GameEventImpl(GameEventType eventType, String message) {
    this.eventType = eventType;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }
  
  @Override
  public GameEventType getEventType() {
    return eventType;
  }
  
  @Override
  public String getMessage() {
    return message;
  }
  
  @Override
  public long getTimestamp() {
    return timestamp;
  }
  
  @Override
  public String toString() {
    return String.format("[%s] %s", eventType, message);
  }
}