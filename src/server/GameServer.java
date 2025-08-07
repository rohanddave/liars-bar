package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.messages.GameMessage;
import server.messages.MessageType;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer extends WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final GameRoomManager roomManager;
    private final ConcurrentHashMap<WebSocket, ClientConnection> connections;
    
    public GameServer(int port) {
        super(new InetSocketAddress(port));
        this.roomManager = new GameRoomManager();
        this.connections = new ConcurrentHashMap<>();
        logger.info("Game server initialized on port {}", port);
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String connectionId = conn.getRemoteSocketAddress().toString() + "-" + System.currentTimeMillis();
        ClientConnection clientConnection = new ClientConnection(conn, connectionId);
        connections.put(conn, clientConnection);
        logger.info("New connection: {}", connectionId);
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        ClientConnection connection = connections.remove(conn);
        if (connection != null) {
            logger.info("Connection closed: {} - {}", connection.getConnectionId(), reason);
            roomManager.leaveRoom(connection);
        }
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        ClientConnection connection = connections.get(conn);
        if (connection == null) {
            logger.warn("Received message from unknown connection");
            return;
        }
        
        try {
            GameMessage gameMessage = objectMapper.readValue(message, GameMessage.class);
            handleMessage(connection, gameMessage);
        } catch (Exception e) {
            logger.error("Failed to parse message from {}: {}", connection.getConnectionId(), e.getMessage());
            connection.sendError("Invalid message format");
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        ClientConnection connection = connections.get(conn);
        String connectionId = connection != null ? connection.getConnectionId() : "unknown";
        logger.error("WebSocket error for connection {}: {}", connectionId, ex.getMessage());
        
        if (connection != null) {
            roomManager.leaveRoom(connection);
        }
    }
    
    private void handleMessage(ClientConnection connection, GameMessage message) {
        logger.debug("Handling message {} from {}", message.getType(), connection.getConnectionId());
        
        switch (message.getType()) {
            case JOIN_ROOM -> handleJoinRoom(connection, message);
            case LEAVE_ROOM -> handleLeaveRoom(connection, message);
            case MAKE_CLAIM -> handleMakeClaim(connection, message);
            case CHALLENGE_CLAIM -> handleChallengeClaim(connection, message);
            case SHOOT_REVOLVER -> handleShootRevolver(connection, message);
            case PING -> handlePing(connection, message);
            default -> {
                logger.warn("Unhandled message type: {} from {}", message.getType(), connection.getConnectionId());
                connection.sendError("Unsupported message type: " + message.getType());
            }
        }
    }
    
    private void handleJoinRoom(ClientConnection connection, GameMessage message) {
        try {
            JsonNode data = message.getData();
            String playerName = data.get("playerName").asText();
            String roomId = message.getRoomId();
            
            if (roomId == null || roomId.trim().isEmpty()) {
                // Create a new room
                roomId = roomManager.createRoom();
            }
            
            boolean success = roomManager.joinRoom(roomId, connection, playerName);
            if (!success) {
                // Error already sent by roomManager
                return;
            }
            
        } catch (Exception e) {
            logger.error("Failed to handle join room: {}", e.getMessage());
            connection.sendError("Failed to join room: " + e.getMessage());
        }
    }
    
    private void handleLeaveRoom(ClientConnection connection, GameMessage message) {
        roomManager.leaveRoom(connection);
        
        GameMessage response = new GameMessage(MessageType.ROOM_LEFT, 
            connection.getUser() != null ? connection.getUser().getId() : null, 
            null);
        connection.sendMessage(response);
    }
    
    private void handleMakeClaim(ClientConnection connection, GameMessage message) {
        try {
            if (connection.getUser() == null || connection.getCurrentRoomId() == null) {
                connection.sendError("Not in a room");
                return;
            }
            
            GameRoom gameRoom = roomManager.getRoom(connection.getCurrentRoomId());
            if (gameRoom == null || gameRoom.getGame() == null) {
                connection.sendError("Game not found");
                return;
            }
            
            JsonNode data = message.getData();
            int count = data.get("count").asInt();
            String rankName = data.get("rank").asText();
            
            // Convert cards data - for now assume player selects cards from their hand
            // In a real implementation, you'd validate the selected cards
            var player = connection.getUser();
            var rank = model.game.Rank.valueOf(rankName);
            var cards = player.getHand().getCards().subList(0, Math.min(count, player.getHand().getSize()));
            
            gameRoom.getGame().claim(player, count, cards, rank);
            
        } catch (Exception e) {
            logger.error("Failed to handle claim: {}", e.getMessage());
            connection.sendError("Failed to make claim: " + e.getMessage());
        }
    }
    
    private void handleChallengeClaim(ClientConnection connection, GameMessage message) {
        try {
            if (connection.getUser() == null || connection.getCurrentRoomId() == null) {
                connection.sendError("Not in a room");
                return;
            }
            
            GameRoom gameRoom = roomManager.getRoom(connection.getCurrentRoomId());
            if (gameRoom == null || gameRoom.getGame() == null) {
                connection.sendError("Game not found");
                return;
            }
            
            var player = connection.getUser();
            gameRoom.getGame().challengeClaim(player);
            
        } catch (Exception e) {
            logger.error("Failed to handle challenge: {}", e.getMessage());
            connection.sendError("Failed to challenge: " + e.getMessage());
        }
    }
    
    private void handleShootRevolver(ClientConnection connection, GameMessage message) {
        try {
            if (connection.getUser() == null || connection.getCurrentRoomId() == null) {
                connection.sendError("Not in a room");
                return;
            }
            
            GameRoom gameRoom = roomManager.getRoom(connection.getCurrentRoomId());
            if (gameRoom == null || gameRoom.getGame() == null) {
                connection.sendError("Game not found");
                return;
            }
            
            var player = connection.getUser();
            boolean eliminated = player.shoot();
            
            // The shoot result will be broadcast via the event system
            
        } catch (Exception e) {
            logger.error("Failed to handle shoot: {}", e.getMessage());
            connection.sendError("Failed to shoot: " + e.getMessage());
        }
    }
    
    private void handlePing(ClientConnection connection, GameMessage message) {
        GameMessage pong = new GameMessage(MessageType.PING);
        pong.setData(objectMapper.valueToTree("pong"));
        connection.sendMessage(pong);
    }
    
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8887;
        
        GameServer server = new GameServer(port);
        server.start();
        
        logger.info("🎲 Liar's Bar WebSocket Server started on port {}", port);
        logger.info("Connect clients to ws://localhost:{}", port);
        
        // Keep the server running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.info("Server interrupted, shutting down...");
            server.stop();
        }
    }
}