package model.exceptions;

public class GameNotOverException extends RuntimeException {
  public GameNotOverException(String message) {
    super(message);
  }
}
