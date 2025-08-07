package server.messages;

public enum MessageType {
    // Client to Server
    JOIN_ROOM,
    LEAVE_ROOM,
    MAKE_CLAIM,
    CHALLENGE_CLAIM,
    SHOOT_REVOLVER,
    
    // Server to Client
    ROOM_JOINED,
    ROOM_LEFT,
    GAME_STARTED,
    GAME_STATE_UPDATE,
    PLAYER_JOINED,
    PLAYER_LEFT,
    TURN_CHANGED,
    CLAIM_MADE,
    CHALLENGE_RESULT,
    SHOOT_RESULT,
    GAME_ENDED,
    
    // Bidirectional
    PING,
    ERROR
}