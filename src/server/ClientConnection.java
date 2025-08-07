package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.messages.GameMessage;
import server.messages.MessageType;
import model.network.User;
import model.network.UserImpl;

public class ClientConnection {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnection.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final WebSocket webSocket;
    private final String connectionId;
    private User user;
    private String currentRoomId;
    
    public ClientConnection(WebSocket webSocket, String connectionId) {
        this.webSocket = webSocket;
        this.connectionId = connectionId;
    }
    
    public void sendMessage(GameMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            webSocket.send(json);
            logger.debug("Sent message to {}: {}", connectionId, json);
        } catch (Exception e) {
            logger.error("Failed to send message to {}: {}", connectionId, e.getMessage());
        }
    }
    
    public void sendError(String errorMessage) {
        GameMessage error = new GameMessage(MessageType.ERROR);
        error.setPlayerId(user != null ? user.getId() : null);
        try {
            error.setData(objectMapper.valueToTree(errorMessage));
        } catch (Exception e) {
            logger.error("Failed to create error message: {}", e.getMessage());
        }
        sendMessage(error);
    }
    
    public boolean isConnected() {
        return webSocket.isOpen();
    }
    
    public void close() {
        webSocket.close();
    }
    
    // Getters and setters
    public String getConnectionId() { return connectionId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getCurrentRoomId() { return currentRoomId; }
    public void setCurrentRoomId(String roomId) { this.currentRoomId = roomId; }
    
    public WebSocket getWebSocket() { return webSocket; }
}