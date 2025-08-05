package model.exceptions;

public class NotPlayerTurnException extends RuntimeException {
  public NotPlayerTurnException(String message) {
    super(message);
  }
}
