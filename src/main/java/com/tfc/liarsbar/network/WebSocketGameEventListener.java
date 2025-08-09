package com.tfc.liarsbar.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tfc.liarsbar.model.events.GameEvent;
import com.tfc.liarsbar.model.events.GameEventListener;
import com.tfc.liarsbar.model.events.GameEventType;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.Player;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketGameEventListener implements GameEventListener {
  private static final Logger logger = Logger.getLogger(WebSocketGameEventListener.class.getName());
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  // Map to store player sessions by player ID
  private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
  
  /**
   * Register a player's WebSocket session
   * @param playerId The player's unique ID
   * @param session The WebSocket session
   */
  public void registerPlayerSession(String playerId, WebSocketSession session) {
    if (playerId != null && session != null) {
      logger.info("Registering player session: " + playerId);
      playerSessions.put(playerId, session);
    }
  }
  
  /**
   * Unregister a player's WebSocket session
   * @param playerId The player's unique ID
   */
  public void unregisterPlayerSession(String playerId) {
    if (playerId != null) {
      logger.info("Unregistering player session: " + playerId);
      playerSessions.remove(playerId);
    }
  }

  @Override
  public void onGameEvent(GameEvent event) {
    if (event == null) {
      return;
    }
    
    logger.info("Received game event: " + event.getEventType() + " - " + event.getMessage());
    
    try {
      GameEventType eventType = event.getEventType();
      
      // Handle different event types
      switch (eventType) {
        case CHALLENGE_MADE:
        case CHALLENGE_RESULT:
          handleChallengeEvent(event);
          break;
        case CLAIM_MADE:
          handleClaimEvent(event);
          break;
        case PLAYER_INITIALIZED:
          // Handle player initialization (includes hand data)
          handleHandUpdatedEvent(event);
          break;
        case GAME_STARTED:
        case TURN_CHANGED:
        case PLAYER_ELIMINATED:
        case GAME_ENDED:
        case PLAYER_SHOT:
        case ROUND_STARTED:
        case ROUND_ENDED:
          // Broadcast these events to all connected players
          broadcastEventToAll(event);
          break;
        default:
          // Default broadcast for other events
          broadcastEventToAll(event);
          break;
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error processing game event: " + e.getMessage(), e);
    }
  }
  
  /**
   * Handle challenge events
   * @param event The challenge event
   */
  private void handleChallengeEvent(GameEvent event) {
    // Extract data from event message if available
    // For challenge events, broadcast to all players
    broadcastEventToAll(event);
  }
  
  /**
   * Handle claim events
   * @param event The claim event
   */
  private void handleClaimEvent(GameEvent event) {
    // Extract data from event message if available
    // For claim events, broadcast to all players
    broadcastEventToAll(event);
  }
  
  /**
   * Handle hand updated events
   * @param event The hand updated event
   */
  private void handleHandUpdatedEvent(GameEvent event) {
    try {
      // Extract player ID and hand data from event
      Map<String, Object> eventData = extractEventData(event.getMessage());
      if (eventData != null && eventData.containsKey("playerId")) {
        String playerId = (String) eventData.get("playerId");
        WebSocketSession session = playerSessions.get(playerId);
        
        if (session != null && session.isOpen()) {
          // Send hand data only to the specific player
          sendEventToSession(session, event);
        }
      } else {
        // If we can't extract player-specific data, broadcast to all as fallback
        broadcastEventToAll(event);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error handling hand updated event: " + e.getMessage(), e);
    }
  }
  
  /**
   * Extract data from event message JSON
   * @param message The event message
   * @return Map of extracted data
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> extractEventData(String message) {
    try {
      return objectMapper.readValue(message, Map.class);
    } catch (Exception e) {
      logger.log(Level.WARNING, "Could not parse event data: " + e.getMessage(), e);
      return null;
    }
  }
  
  /**
   * Broadcast an event to all connected player sessions
   * @param event The event to broadcast
   */
  private void broadcastEventToAll(GameEvent event) {
    playerSessions.values().forEach(session -> {
      if (session.isOpen()) {
        sendEventToSession(session, event);
      }
    });
  }
  
  /**
   * Send an event to a specific WebSocket session
   * @param session The WebSocket session
   * @param event The event to send
   */
  private void sendEventToSession(WebSocketSession session, GameEvent event) {
    try {
      ObjectNode eventNode = objectMapper.createObjectNode();
      eventNode.put("type", event.getEventType().toString());
      eventNode.put("message", event.getMessage());
      eventNode.put("timestamp", event.getTimestamp());
      
      String eventJson = objectMapper.writeValueAsString(eventNode);
      session.sendMessage(new TextMessage(eventJson));
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error sending event to session: " + e.getMessage(), e);
    }
  }
}
