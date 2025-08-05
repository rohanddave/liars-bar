package model.exceptions;

public class NoCardsException extends RuntimeException {
  public NoCardsException(String message) {
    super(message);
  }
}
