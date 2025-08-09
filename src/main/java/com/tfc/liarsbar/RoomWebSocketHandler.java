package com.tfc.liarsbar;

import com.tfc.liarsbar.network.UserImpl;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class RoomWebSocketHandler extends TextWebSocketHandler {

  private final RoomService roomService;

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
    roomService.broadcastMessage(session, message.getPayload());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    roomService.removeUser(session);
  }
}
