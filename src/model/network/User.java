package model.network;

import model.game.Player;

/**
 * Interface represents a User of the application.
 */
public interface User extends Player {
  String getId(); 
  
  String getUserName();
}