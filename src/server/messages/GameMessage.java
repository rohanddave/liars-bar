package server.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameMessage {
    private MessageType type;
    private String playerId;
    private String roomId;
    private JsonNode data;
    private long timestamp;
    
    public GameMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public GameMessage(MessageType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    public GameMessage(MessageType type, String playerId, String roomId) {
        this.type = type;
        this.playerId = playerId;
        this.roomId = roomId;
        this.timestamp = System.currentTimeMillis();
    }
    
    public GameMessage(MessageType type, String playerId, String roomId, JsonNode data) {
        this.type = type;
        this.playerId = playerId;
        this.roomId = roomId;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public JsonNode getData() { return data; }
    public void setData(JsonNode data) { this.data = data; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}