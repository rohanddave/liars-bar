package com.tfc.liarsbar.model.commands;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Data class to hold parsed command parameters
 */
public class CommandRequest {
    private final String commandName;
    private final Map<String, Object> parameters;
    private final String rawInput;
    
    public CommandRequest(String commandName, String rawInput) {
        this(commandName, new HashMap<>(), rawInput);
    }
    
    public CommandRequest(String commandName, Map<String, Object> parameters, String rawInput) {
        this.commandName = commandName;
        this.parameters = new HashMap<>(parameters);
        this.rawInput = rawInput;
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public String getRawInput() {
        return rawInput;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, Class<T> type) {
        Object value = parameters.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    public Integer getIntParameter(String key) {
        return getParameter(key, Integer.class);
    }
    
    public String getStringParameter(String key) {
        return getParameter(key, String.class);
    }
    
    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
    
    @Override
    public String toString() {
        return "CommandRequest{" +
                "commandName='" + commandName + '\'' +
                ", parameters=" + parameters +
                ", rawInput='" + rawInput + '\'' +
                '}';
    }
}