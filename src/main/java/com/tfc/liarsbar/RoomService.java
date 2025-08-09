package com.tfc.liarsbar;

import com.tfc.liarsbar.model.events.GameEvent;
import com.tfc.liarsbar.model.events.GameEventImpl;
import com.tfc.liarsbar.model.events.GameEventListener;
import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.events.GameEventType;
import com.tfc.liarsbar.network.Room;
import com.tfc.liarsbar.network.RoomImpl;
import com.tfc.liarsbar.network.User;
import com.tfc.liarsbar.network.WebSocketGameEventListener;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

  private final Map<String, Room> rooms = new ConcurrentHashMap<>();
  private final Map<String, GameEventListener> eventListenerMap = new ConcurrentHashMap<>();
  private final Map<WebSocketSession, User> sessionUserMap = new ConcurrentHashMap<>();

  public void addUserToRoom(String roomId, User user) {
    sessionUserMap.put(user.getSession(), user);
    GameEvent event = new GameEventImpl(GameEventType.ROOM_JOINED, "User " + user.getUserName() + " joined the room!");
    if (rooms.containsKey(roomId)) {
      rooms.compute(roomId, (k, room) -> room);
    } else {
      GameEventListener gameEventListener = new WebSocketGameEventListener();
      GameEventPublisher eventPublisher = new GameEventPublisher();
      eventPublisher.addListener(gameEventListener);
      rooms.put(roomId, new RoomImpl(roomId, eventPublisher));
      eventListenerMap.put(roomId, gameEventListener);
    }
    rooms.get(roomId).getGameEventPublisher().publishEvent(event);
  }

  public void removeUser(WebSocketSession session) {
    User user = sessionUserMap.remove(session);
    if (user != null && user.getRoomId() != null) {
      rooms.get(user.getRoomId()).removeUser(user);
    }
  }

  public void broadcastMessage(WebSocketSession sender, String message) throws IOException {
    User user = sessionUserMap.get(sender);
//    if (user != null && user.getRoomId() != null) {
//      Room room = rooms.get(user.getRoomId());
//      room.getGameEventPublisher().publishEvent(GameEventType.);
//      for (User u : room.getUsers()) {
//        u.getSession().sendMessage(new TextMessage(user.getUserName() + ": " + message));
//      }
//    }
  }
}
