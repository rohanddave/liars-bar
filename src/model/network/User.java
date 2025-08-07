package model.network;

import model.events.GameEventPublisher;
import model.game.Player;

/**
 * Interface represents a User of the application.
 */
public interface User extends Player {
  String getId(); 
  
  String getUserName();

  void setEventPublisher(GameEventPublisher eventPublisher);
}