package model.game;

public interface GameActionDto {
  Player getPlayer();

  GameAction action();

  // TODO: override the to string method to return as string for the view
}
