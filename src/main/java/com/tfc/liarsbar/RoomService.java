package com.tfc.liarsbar;

import com.tfc.liarsbar.model.events.GameEvent;
import com.tfc.liarsbar.model.events.GameEventImpl;
import com.tfc.liarsbar.model.events.GameEventListener;
import com.tfc.liarsbar.model.events.GameEventType;
import com.tfc.liarsbar.model.exceptions.RoomFullException;
import com.tfc.liarsbar.network.Room;
import com.tfc.liarsbar.network.RoomFactory;
import com.tfc.liarsbar.network.User;
import com.tfc.liarsbar.network.WebSocketGameEventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class RoomService {
  private static final Logger logger = Logger.getLogger(RoomService.class.getName());
  
  private final Map<String, Room> rooms = new ConcurrentHashMap<>();
  private final Map<String, GameEventListener> eventListenerMap = new ConcurrentHashMap<>();
  private final Map<WebSocketSession, User> sessionUserMap = new ConcurrentHashMap<>();
  
  private final RoomFactory roomFactory;
  
  @Autowired
  public RoomService(RoomFactory roomFactory) {
    this.roomFactory = roomFactory;
  }

  public RoomService() {
    this.roomFactory = new RoomFactory();
  }

  public void addUserToRoom(String roomId, User user) {
    if (roomId == null || roomId.trim().isEmpty()) {
      throw new IllegalArgumentException("Room ID cannot be null or empty");
    }
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (user.getSession() == null) {
      throw new IllegalArgumentException("User session cannot be null");
    }
    
    try {
      sessionUserMap.put(user.getSession(), user);
      user.setRoomId(roomId);
      
      Room room = rooms.computeIfAbsent(roomId, id -> {
        GameEventListener listener = new WebSocketGameEventListener();
        eventListenerMap.put(id, listener);
        return roomFactory.createRoom(id, listener);
      });
      
      room.addUser(user);
      
      GameEvent event = new GameEventImpl(GameEventType.ROOM_JOINED, 
        "User " + user.getUserName() + " joined room " + roomId);
      room.getGameEventPublisher().publishEvent(event);
      
      logger.info("User " + user.getUserName() + " successfully added to room " + roomId);
      
    } catch (RoomFullException e) {
      sessionUserMap.remove(user.getSession());
      user.setRoomId(null);
      logger.warning("Failed to add user " + user.getUserName() + " to room " + roomId + ": " + e.getMessage());
      throw e;
    } catch (Exception e) {
      sessionUserMap.remove(user.getSession());
      user.setRoomId(null);
      logger.severe("Unexpected error adding user to room: " + e.getMessage());
      throw new RuntimeException("Failed to add user to room", e);
    }
  }

  public void removeUser(WebSocketSession session) {
    if (session == null) {
      logger.warning("Attempted to remove user with null session");
      return;
    }
    
    User user = sessionUserMap.remove(session);
    if (user == null) {
      logger.info("No user found for session during removal");
      return;
    }
    
    String roomId = user.getRoomId();
    if (roomId == null) {
      logger.info("User " + user.getUserName() + " was not in any room");
      return;
    }
    
    try {
      Room room = rooms.get(roomId);
      if (room != null) {
        room.removeUser(user);
        
        GameEvent event = new GameEventImpl(GameEventType.ROOM_LEFT, 
          "User " + user.getUserName() + " left room " + roomId);
        room.getGameEventPublisher().publishEvent(event);
        
        // Clean up empty rooms
        if (room.getUsers().isEmpty()) {
          rooms.remove(roomId);
          eventListenerMap.remove(roomId);
          logger.info("Removed empty room: " + roomId);
        }
        
        logger.info("User " + user.getUserName() + " successfully removed from room " + roomId);
      } else {
        logger.warning("Room " + roomId + " not found when removing user " + user.getUserName());
      }
    } catch (Exception e) {
      logger.severe("Error removing user " + user.getUserName() + " from room " + roomId + ": " + e.getMessage());
    } finally {
      user.setRoomId(null);
    }
  }
  
  public Room getRoom(String roomId) {
    if (roomId == null || roomId.trim().isEmpty()) {
      return null;
    }
    return rooms.get(roomId);
  }
  
  public User getUserBySession(WebSocketSession session) {
    if (session == null) {
      return null;
    }
    return sessionUserMap.get(session);
  }
  
  public int getRoomCount() {
    return rooms.size();
  }
  
  public int getTotalUserCount() {
    return sessionUserMap.size();
  }

}
