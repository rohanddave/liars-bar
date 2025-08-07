package server;

import model.events.GameEventPublisher;
import model.game.GameImpl;
import model.network.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRoom {
    private final String roomId;
    private final Room room;
    private final GameEventPublisher eventPublisher;
    private final List<ClientConnection> connections;
    private GameImpl game;
    
    public GameRoom(String roomId, Room room, GameEventPublisher eventPublisher) {
        this.roomId = roomId;
        this.room = room;
        this.eventPublisher = eventPublisher;
        this.connections = new CopyOnWriteArrayList<>();
    }
    
    public void addConnection(ClientConnection connection) {
        connections.add(connection);
    }
    
    public void removeConnection(ClientConnection connection) {
        connections.remove(connection);
    }
    
    public List<ClientConnection> getConnections() {
        return new ArrayList<>(connections);
    }
    
    // Getters and setters
    public String getRoomId() { return roomId; }
    public Room getRoom() { return room; }
    public GameEventPublisher getEventPublisher() { return eventPublisher; }
    public GameImpl getGame() { return game; }
    public void setGame(GameImpl game) { this.game = game; }
}