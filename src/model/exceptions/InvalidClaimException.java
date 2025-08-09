package model.exceptions;

public class InvalidClaimException extends RuntimeException {
  public InvalidClaimException(String message) {
    super(message);
  }
}
