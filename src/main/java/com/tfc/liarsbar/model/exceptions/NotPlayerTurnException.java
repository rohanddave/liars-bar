package com.tfc.liarsbar.model.exceptions;

public class NotPlayerTurnException extends RuntimeException {
  public NotPlayerTurnException(String message) {
    super(message);
  }
}
