package com.tfc.liarsbar;

import com.tfc.liarsbar.network.Room;
import com.tfc.liarsbar.network.RoomImpl;
import com.tfc.liarsbar.network.User;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

  private final Map<String, Room> rooms = new ConcurrentHashMap<>();
  private final Map<WebSocketSession, User> sessionUserMap = new ConcurrentHashMap<>();

  public void addUserToRoom(String roomId, User user) {
    rooms.computeIfAbsent(roomId, id -> new RoomImpl(id)).addUser(user);
    sessionUserMap.put(user.getSession(), user);
  }

  public void removeUser(WebSocketSession session) {
    User user = sessionUserMap.remove(session);
    if (user != null && user.getRoomId() != null) {
      rooms.get(user.getRoomId()).removeUser(user);
    }
  }

  public void broadcastMessage(WebSocketSession sender, String message) throws IOException {
    User user = sessionUserMap.get(sender);
    if (user != null && user.getRoomId() != null) {
      Room room = rooms.get(user.getRoomId());
      for (User u : room.getUsers()) {
        u.getSession().sendMessage(new TextMessage(user.getUserName() + ": " + message));
      }
    }
  }
}
