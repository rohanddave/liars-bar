package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.events.GameEvent;
import model.events.GameEventListener;
import model.events.GameEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.messages.GameMessage;
import server.messages.MessageType;

public class WebSocketGameEventListener implements GameEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketGameEventListener.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final GameRoomManager roomManager;
    private final String roomId;
    
    public WebSocketGameEventListener(GameRoomManager roomManager, String roomId) {
        this.roomManager = roomManager;
        this.roomId = roomId;
    }
    
    @Override
    public void onGameEvent(GameEvent event) {
        try {
            MessageType messageType = mapEventTypeToMessageType(event.getType());
            if (messageType == null) return; // Skip unmapped events
            
            GameMessage message = new GameMessage(messageType, null, roomId);
            ObjectNode data = objectMapper.createObjectNode();
            data.put("eventType", event.getType().name());
            data.put("message", event.getMessage());
            data.put("timestamp", event.getTimestamp());
            
            // Add event-specific data
            addEventSpecificData(data, event);
            
            message.setData(data);
            roomManager.broadcastToRoom(roomId, message);
            
            logger.debug("Broadcasted event {} to room {}", event.getType(), roomId);
            
        } catch (Exception e) {
            logger.error("Failed to broadcast event to room {}: {}", roomId, e.getMessage());
        }
    }
    
    private MessageType mapEventTypeToMessageType(GameEventType eventType) {
        return switch (eventType) {
            case GAME_STARTED -> MessageType.GAME_STARTED;
            case GAME_ENDED -> MessageType.GAME_ENDED;
            case TURN_CHANGED -> MessageType.TURN_CHANGED;
            case CLAIM_MADE -> MessageType.CLAIM_MADE;
            case CHALLENGE_MADE -> MessageType.CHALLENGE_RESULT;
            case REVOLVER_SHOT -> MessageType.SHOOT_RESULT;
            case PLAYER_ELIMINATED, PLAYER_INITIALIZED, ROUND_STARTED, ROUND_ENDED -> MessageType.GAME_STATE_UPDATE;
            default -> null; // Don't broadcast unmapped events
        };
    }
    
    private void addEventSpecificData(ObjectNode data, GameEvent event) {
        GameRoom gameRoom = roomManager.getRoom(roomId);
        if (gameRoom == null || gameRoom.getGame() == null) return;
        
        // Add current game state information
        data.put("currentRound", gameRoom.getGame().getRank().name());
        data.put("gameOver", gameRoom.getGame().isGameOver());
        
        if (gameRoom.getGame().getCurrentPlayer() != null) {
            data.put("currentPlayerId", gameRoom.getGame().getCurrentPlayer().getId());
            data.put("currentPlayerName", gameRoom.getGame().getCurrentPlayer().getName());
        }
        
        // Add event-specific details based on event type
        switch (event.getType()) {
            case CLAIM_MADE:
                if (gameRoom.getGame().getLastClaim() != null) {
                    ObjectNode claimData = objectMapper.createObjectNode();
                    claimData.put("count", gameRoom.getGame().getLastClaim().getCount());
                    claimData.put("rank", gameRoom.getGame().getLastClaim().getClaimedRank().name());
                    claimData.put("playerId", gameRoom.getGame().getLastClaim().getPlayer().getId());
                    data.set("claim", claimData);
                }
                break;
            case GAME_ENDED:
                try {
                    if (gameRoom.getGame().isGameOver()) {
                        var winner = gameRoom.getGame().getWinner();
                        if (winner != null) {
                            data.put("winnerId", winner.getId());
                            data.put("winnerName", winner.getName());
                        }
                    }
                } catch (Exception e) {
                    // Game may not have a clear winner
                }
                break;
        }
    }
}