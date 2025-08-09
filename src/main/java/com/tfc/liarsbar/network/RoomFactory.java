package com.tfc.liarsbar.network;

import com.tfc.liarsbar.model.events.GameEventListener;
import com.tfc.liarsbar.model.events.GameEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Factory for creating rooms with proper event handling setup
 */
@Component
public class RoomFactory {
    
    /**
     * Creates a new room with the specified ID and event handling setup
     * @param roomId The unique identifier for the room
     * @param eventListener The event listener to attach to the room
     * @return A new Room instance with proper event handling
     * @throws IllegalArgumentException if roomId is null or empty
     */
    public Room createRoom(String roomId, GameEventListener eventListener) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty");
        }
        
        if (eventListener == null) {
            throw new IllegalArgumentException("Event listener cannot be null");
        }
        
        GameEventPublisher eventPublisher = new GameEventPublisher();
        eventPublisher.addListener(eventListener);
        
        return new RoomImpl(roomId, eventPublisher);
    }
    
    /**
     * Creates a new room with default WebSocket event listener
     * @param roomId The unique identifier for the room
     * @return A new Room instance with WebSocket event handling
     */
    public Room createRoom(String roomId) {
        return createRoom(roomId, new WebSocketGameEventListener());
    }
}