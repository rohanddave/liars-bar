package com.tfc.liarsbar;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class RoomWebSocketConfig implements WebSocketConfigurer {

  private final RoomWebSocketHandler roomWebSocketHandler;

  public RoomWebSocketConfig(RoomWebSocketHandler roomWebSocketHandler) {
    this.roomWebSocketHandler = roomWebSocketHandler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(roomWebSocketHandler, "/ws/room")
            .setAllowedOrigins("*");
  }
}