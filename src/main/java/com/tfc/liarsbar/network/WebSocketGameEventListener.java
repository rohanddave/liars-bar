package com.tfc.liarsbar.network;

import com.tfc.liarsbar.model.events.GameEvent;
import com.tfc.liarsbar.model.events.GameEventListener;

public class WebSocketGameEventListener implements GameEventListener {

  @Override
  public void onGameEvent(GameEvent event) {
    System.out.println("Received game event: " + event);
  }
}
