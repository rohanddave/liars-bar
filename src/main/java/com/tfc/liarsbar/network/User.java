package com.tfc.liarsbar.network;

import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.game.Player;
import org.springframework.web.socket.WebSocketSession;

/**
 * Interface represents a User of the application.
 */
public interface User extends Player {
  String getId(); 
  
  String getUserName();

  void setEventPublisher(GameEventPublisher eventPublisher);

  WebSocketSession getSession();

  String getRoomId();

  void setRoomId(String roomId);
}