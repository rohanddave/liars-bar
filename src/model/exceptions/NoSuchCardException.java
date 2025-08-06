package model.exceptions;

public class NoSuchCardException extends RuntimeException {
  public NoSuchCardException(String message) {
    super(message);
  }
}
