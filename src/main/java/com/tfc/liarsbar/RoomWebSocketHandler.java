package com.tfc.liarsbar;

import com.tfc.liarsbar.model.actions.ActionResult;
import com.tfc.liarsbar.model.commands.GameCommandProcessor;
import com.tfc.liarsbar.network.Room;
import com.tfc.liarsbar.network.User;
import com.tfc.liarsbar.network.UserImpl;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class RoomWebSocketHandler extends TextWebSocketHandler {
  private static final Logger logger = Logger.getLogger(RoomWebSocketHandler.class.getName());
  
  private final RoomService roomService;
  private final ConcurrentHashMap<String, GameCommandProcessor> roomProcessors = new ConcurrentHashMap<>();

  public RoomWebSocketHandler() {
    this.roomService = new RoomService();
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    // Extract roomId & username from query params
    String uri = session.getUri().toString();
    String roomId = uri.split("roomId=")[1].split("&")[0];
    String username = uri.split("username=")[1];

    this.roomService.addUserToRoom(roomId, new UserImpl(username, session));

    session.sendMessage(new TextMessage("Welcome " + username + " to room " + roomId));
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    try {
      String payload = message.getPayload();
      logger.info("Received message: " + payload + " from session: " + session.getId());
      
      // Get user from session
      User user = roomService.getUserBySession(session);
      if (user == null) {
        logger.warning("No user found for session: " + session.getId());
        session.sendMessage(new TextMessage("{\"error\":\"User not found. Please reconnect.\"}"));
        return;
      }
      
      // Get room from user's room ID
      Room room = roomService.getRoom(user.getRoomId());
      if (room == null) {
        logger.warning("No room found for user: " + user.getUserName());
        session.sendMessage(new TextMessage("{\"error\":\"Room not found. Please rejoin.\"}"));
        return;
      }
      
      // Get or create command processor for this room
      GameCommandProcessor processor = roomProcessors.computeIfAbsent(user.getRoomId(), 
        roomId -> new GameCommandProcessor(room.getGameEventPublisher()));
      
      // Process the command
      // Note: For now, we'll create a mock game and player since Game integration may not be complete
      // This should be replaced with actual game instance retrieval
      if (processor.isValidCommand(payload)) {
        // TODO: Replace with actual Game and Player instances from room/game state
         ActionResult result = processor.processCommand(payload, room.getGame(), roomService.getUserBySession(session));
        
        // For now, just acknowledge the valid command
        String response = "{\"status\":\"success\",\"message\":\"Command '" + payload + "' recognized and queued for processing\"}";
        session.sendMessage(new TextMessage(response));
        
        // Broadcast to room that user sent a command
        String broadcast = "{\"type\":\"player_action\",\"player\":\"" + user.getUserName() + "\",\"action\":\"" + payload + "\"}";
        broadcastToRoom(room, broadcast, session);

        for (User u : room.getUsers()) {
          try {
            WebSocketSession userSession = u.getSession();
            if (userSession != null && userSession.isOpen()) {
              String latestGameStateForPlayer = room.getGame().getGameState(u);
              userSession.sendMessage(new TextMessage(latestGameStateForPlayer));
            }
          } catch (Exception e) {
            logger.warning("Failed to send message to user " + user.getUserName() + ": " + e.getMessage());
          }
        }
      } else {
        // Invalid command - send help
        String help = processor.getCommandHelp();
        String response = "{\"error\":\"Invalid command: '" + payload + "'\",\"help\":\"" + help.replace("\n", "\\n") + "\"}";
        session.sendMessage(new TextMessage(response));
      }
      
    } catch (Exception e) {
      logger.severe("Error processing message: " + e.getMessage());
      session.sendMessage(new TextMessage("{\"error\":\"Failed to process command: " + e.getMessage() + "\"}"));
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    // Clean up user and room processor if room becomes empty
    User user = roomService.getUserBySession(session);
    if (user != null && user.getRoomId() != null) {
      String roomId = user.getRoomId();
      roomService.removeUser(session);
      
      // Remove processor if room is now empty
      Room room = roomService.getRoom(roomId);
      if (room == null || room.getUsers().isEmpty()) {
        roomProcessors.remove(roomId);
        logger.info("Removed command processor for empty room: " + roomId);
      }
    }
  }
  
  /**
   * Broadcasts a message to all users in a room except the sender
   * @param room The room to broadcast to
   * @param message The message to send
   * @param senderSession The session of the sender (excluded from broadcast)
   */
  private void broadcastToRoom(Room room, String message, WebSocketSession senderSession) {
    if (room == null || room.getUsers().isEmpty()) {
      return;
    }
    
    for (User user : room.getUsers()) {
      try {
        WebSocketSession userSession = user.getSession();
        if (userSession != null && !userSession.equals(senderSession) && userSession.isOpen()) {
          userSession.sendMessage(new TextMessage(message));
        }
      } catch (Exception e) {
        logger.warning("Failed to send message to user " + user.getUserName() + ": " + e.getMessage());
      }
    }
  }
}
