package model.exceptions;

public class NoActiveClaimException extends RuntimeException {
  public NoActiveClaimException(String message) {
    super(message);
  }
}
