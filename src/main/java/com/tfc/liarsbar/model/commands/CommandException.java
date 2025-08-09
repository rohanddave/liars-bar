package com.tfc.liarsbar.model.commands;

/**
 * Exception thrown when command processing fails
 */
public class CommandException extends Exception {
    private final String commandInput;
    private final CommandErrorType errorType;
    
    public enum CommandErrorType {
        INVALID_FORMAT,
        INVALID_PARAMETERS,
        EXECUTION_FAILED,
        VALIDATION_FAILED,
        UNKNOWN_COMMAND
    }
    
    public CommandException(String message, String commandInput, CommandErrorType errorType) {
        super(message);
        this.commandInput = commandInput;
        this.errorType = errorType;
    }
    
    public CommandException(String message, String commandInput, CommandErrorType errorType, Throwable cause) {
        super(message, cause);
        this.commandInput = commandInput;
        this.errorType = errorType;
    }
    
    public String getCommandInput() {
        return commandInput;
    }
    
    public CommandErrorType getErrorType() {
        return errorType;
    }
    
    @Override
    public String toString() {
        return "CommandException{" +
                "message='" + getMessage() + '\'' +
                ", commandInput='" + commandInput + '\'' +
                ", errorType=" + errorType +
                '}';
    }
}