package model.exceptions;

/**
 * Exception thrown when trying to access a card that doesn't exist.
 */
public class NoSuchCardException extends Exception {
    public NoSuchCardException(String message) {
        super(message);
    }
}