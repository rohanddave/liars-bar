package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.events.GameEventPublisher;
import model.game.GameImpl;
import model.game.Player;
import model.network.Room;
import model.network.RoomImpl;
import model.network.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.messages.GameMessage;
import server.messages.MessageType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoomManager {
    private static final Logger logger = LoggerFactory.getLogger(GameRoomManager.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final Map<String, ClientConnection> connections = new ConcurrentHashMap<>();
    
    public synchronized String createRoom() {
        String roomId = UUID.randomUUID().toString().substring(0, 8);
        GameEventPublisher eventPublisher = new GameEventPublisher();
        Room room = new RoomImpl(eventPublisher);
        GameRoom gameRoom = new GameRoom(roomId, room, eventPublisher);
        rooms.put(roomId, gameRoom);
        logger.info("Created room: {}", roomId);
        return roomId;
    }
    
    public synchronized boolean joinRoom(String roomId, ClientConnection connection, String playerName) {
        GameRoom gameRoom = rooms.get(roomId);
        if (gameRoom == null) {
            connection.sendError("Room not found: " + roomId);
            return false;
        }
        
        if (gameRoom.getRoom().getMembers().size() >= gameRoom.getRoom().getCapacity()) {
            connection.sendError("Room is full");
            return false;
        }
        
        try {
            // Create user for the connection
            User user = new model.network.UserImpl(playerName);
            user.setEventPublisher(gameRoom.getEventPublisher());
            connection.setUser(user);
            connection.setCurrentRoomId(roomId);
            
            // Add user to room
            gameRoom.getRoom().addUser(user);
            gameRoom.addConnection(connection);
            connections.put(connection.getConnectionId(), connection);
            
            // Notify room members
            broadcastToRoom(roomId, new GameMessage(MessageType.PLAYER_JOINED, user.getId(), roomId, createPlayerData(user)));
            
            // Send room joined confirmation
            GameMessage joinedMessage = new GameMessage(MessageType.ROOM_JOINED, user.getId(), roomId);
            joinedMessage.setData(createRoomStateData(gameRoom));
            connection.sendMessage(joinedMessage);
            
            // Start game if room is full
            if (gameRoom.getRoom().getMembers().size() >= gameRoom.getRoom().getCapacity()) {
                startGame(roomId);
            }
            
            logger.info("Player {} joined room {}", playerName, roomId);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to join room {}: {}", roomId, e.getMessage());
            connection.sendError("Failed to join room: " + e.getMessage());
            return false;
        }
    }
    
    public synchronized void leaveRoom(ClientConnection connection) {
        String roomId = connection.getCurrentRoomId();
        if (roomId == null) return;
        
        GameRoom gameRoom = rooms.get(roomId);
        if (gameRoom == null) return;
        
        try {
            User user = connection.getUser();
            if (user != null) {
                // Remove from game if active
                if (gameRoom.getGame() != null && !gameRoom.getGame().isGameOver()) {
                    // Handle player leaving mid-game
                    handlePlayerLeaving(gameRoom, user);
                }
                
                // Notify other players
                broadcastToRoomExcept(roomId, connection, 
                    new GameMessage(MessageType.PLAYER_LEFT, user.getId(), roomId, createPlayerData(user)));
            }
            
            gameRoom.removeConnection(connection);
            connections.remove(connection.getConnectionId());
            connection.setCurrentRoomId(null);
            connection.setUser(null);
            
            // Remove empty rooms
            if (gameRoom.getConnections().isEmpty()) {
                rooms.remove(roomId);
                logger.info("Removed empty room: {}", roomId);
            }
            
        } catch (Exception e) {
            logger.error("Failed to leave room {}: {}", roomId, e.getMessage());
        }
    }
    
    private void startGame(String roomId) {
        GameRoom gameRoom = rooms.get(roomId);
        if (gameRoom == null) return;
        
        try {
            // Create game with all room members
            GameImpl.Builder gameBuilder = new GameImpl.Builder()
                    .withEventPublisher(gameRoom.getEventPublisher());
            
            for (User user : gameRoom.getRoom().getMembers()) {
                gameBuilder.addPlayer((Player) user); // User implements Player interface
            }
            
            GameImpl game = (GameImpl) gameBuilder.build();
            gameRoom.setGame(game);
            
            // Set up event listener for WebSocket broadcasting
            game.getEventPublisher().addListener(new WebSocketGameEventListener(this, roomId));
            
            // Start the game
            game.startGame();
            
            // Notify all players
            GameMessage gameStarted = new GameMessage(MessageType.GAME_STARTED, null, roomId);
            gameStarted.setData(createGameStateData(game));
            broadcastToRoom(roomId, gameStarted);
            
            logger.info("Started game in room: {}", roomId);
            
        } catch (Exception e) {
            logger.error("Failed to start game in room {}: {}", roomId, e.getMessage());
            broadcastToRoom(roomId, new GameMessage(MessageType.ERROR, null, roomId, 
                objectMapper.valueToTree("Failed to start game: " + e.getMessage())));
        }
    }
    
    private void handlePlayerLeaving(GameRoom gameRoom, User user) {
        // Mark player as eliminated if game is active
        if (gameRoom.getGame() != null && user instanceof Player) {
            Player player = (Player) user;
            // Force player elimination - they lose their turn and are marked as not alive
            player.getRevolver().forceElimination();
            
            // Check if game should end
            if (gameRoom.getGame().isGameOver()) {
                broadcastToRoom(gameRoom.getRoomId(), 
                    new GameMessage(MessageType.GAME_ENDED, null, gameRoom.getRoomId(), 
                        createGameEndData(gameRoom.getGame())));
            }
        }
    }
    
    public void broadcastToRoom(String roomId, GameMessage message) {
        GameRoom gameRoom = rooms.get(roomId);
        if (gameRoom != null) {
            for (ClientConnection connection : gameRoom.getConnections()) {
                if (connection.isConnected()) {
                    connection.sendMessage(message);
                }
            }
        }
    }
    
    public void broadcastToRoomExcept(String roomId, ClientConnection except, GameMessage message) {
        GameRoom gameRoom = rooms.get(roomId);
        if (gameRoom != null) {
            for (ClientConnection connection : gameRoom.getConnections()) {
                if (connection.isConnected() && !connection.equals(except)) {
                    connection.sendMessage(message);
                }
            }
        }
    }
    
    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }
    
    public ClientConnection getConnection(String connectionId) {
        return connections.get(connectionId);
    }
    
    // Helper methods to create JSON data
    private ObjectNode createPlayerData(User user) {
        ObjectNode data = objectMapper.createObjectNode();
        data.put("id", user.getId());
        data.put("name", user.getName());
        return data;
    }
    
    private ObjectNode createRoomStateData(GameRoom gameRoom) {
        ObjectNode data = objectMapper.createObjectNode();
        data.put("roomId", gameRoom.getRoomId());
        data.put("playerCount", gameRoom.getRoom().getMembers().size());
        data.put("maxPlayers", gameRoom.getRoom().getCapacity());
        data.put("gameStarted", gameRoom.getGame() != null);
        return data;
    }
    
    private ObjectNode createGameStateData(GameImpl game) {
        ObjectNode data = objectMapper.createObjectNode();
        data.put("currentRound", game.getRank().name());
        data.put("gameOver", game.isGameOver());
        
        if (game.getCurrentPlayer() != null) {
            data.put("currentPlayerId", game.getCurrentPlayer().getId());
        }
        
        // Add game state details as needed
        return data;
    }
    
    private ObjectNode createGameEndData(GameImpl game) {
        ObjectNode data = objectMapper.createObjectNode();
        data.put("gameOver", true);
        
        try {
            if (!game.isGameOver()) {
                data.put("reason", "Player disconnected");
            } else {
                Player winner = game.getWinner();
                if (winner != null) {
                    data.put("winnerId", winner.getId());
                    data.put("winnerName", winner.getName());
                }
            }
        } catch (Exception e) {
            data.put("reason", "Game ended unexpectedly");
        }
        
        return data;
    }
}