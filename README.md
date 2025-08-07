# 🎲 Liar's Bar - Multiplayer Card Game

A WebSocket-based multiplayer implementation of the classic Liar's Bar card game built in Java.

## Features

- **Multiplayer Support**: Play with up to 4 players over WebSockets
- **Real-time Gameplay**: Live game state synchronization across all clients
- **Web Client**: Modern HTML5 client with responsive design
- **Console Mode**: Traditional single-player console interface
- **Room System**: Create or join game rooms
- **Event-Driven Architecture**: Comprehensive game event system

## Game Rules

Liar's Bar is a bluffing card game where players:
1. Take turns making claims about their cards (e.g., "I have 2 Kings")
2. Other players can challenge claims they think are false
3. If a claim is challenged and proven false, the claimant shoots the revolver
4. If a claim is challenged but was true, the challenger shoots
5. Players are eliminated when they draw the bullet
6. Last player standing wins!

## Quick Start

### Running the WebSocket Server

```bash
# Compile and run the server
mvn compile exec:java -Dexec.mainClass="server.GameServer"

# Or specify a different port
mvn compile exec:java -Dexec.mainClass="server.GameServer" -Dexec.args="9090"
```

### Running the Web Client

1. Start the WebSocket server (default port 8887)
2. Open `client/index.html` in your web browser
3. Enter your name and create/join a room
4. Wait for other players to join (2-4 players required)
5. Game starts automatically when room is full

### Running Console Mode (Single Player)

```bash
mvn compile exec:java -Dexec.mainClass="Main"
```

## Architecture

### Server Components

- **GameServer**: Main WebSocket server handling client connections
- **GameRoomManager**: Manages game rooms and player sessions
- **ClientConnection**: Represents individual WebSocket connections
- **WebSocketGameEventListener**: Broadcasts game events to clients
- **GameMessage/MessageType**: WebSocket message protocol

### Game Engine

- **GameImpl**: Core game logic and state management
- **Player/User**: Player interface with network capabilities
- **GameEventPublisher**: Observable pattern for game events
- **ActionFactory**: Handles player actions (claim, challenge, shoot)

### Client Features

- Real-time connection status
- Game room management
- Live player list with turn indicators
- Interactive game controls
- Comprehensive game log
- Responsive design for mobile/desktop

## WebSocket Message Protocol

### Client to Server Messages

```javascript
// Join a room
{
  "type": "JOIN_ROOM",
  "roomId": "optional-room-id",
  "data": {
    "playerName": "YourName"
  }
}

// Make a claim
{
  "type": "MAKE_CLAIM",
  "data": {
    "rank": "ACE",
    "count": 2
  }
}

// Challenge a claim
{
  "type": "CHALLENGE_CLAIM"
}

// Shoot revolver
{
  "type": "SHOOT_REVOLVER"
}
```

### Server to Client Messages

```javascript
// Room joined confirmation
{
  "type": "ROOM_JOINED",
  "playerId": "unique-player-id",
  "roomId": "room-id",
  "data": {
    "playerCount": 2,
    "maxPlayers": 4
  }
}

// Game state updates
{
  "type": "TURN_CHANGED",
  "data": {
    "currentPlayerId": "player-id",
    "currentPlayerName": "PlayerName"
  }
}
```

## Development

### Prerequisites

- Java 17+
- Maven 3.6+
- Modern web browser for client testing

### Building

```bash
# Install dependencies and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package
```

### Project Structure

```
liars-bar/
├── src/
│   ├── Main.java                 # Console game entry point
│   ├── server/                   # WebSocket server components
│   │   ├── GameServer.java
│   │   ├── GameRoomManager.java
│   │   └── messages/             # Protocol definitions
│   ├── model/                    # Game logic
│   │   ├── game/                 # Core game classes
│   │   ├── network/              # Player/Room management
│   │   └── events/               # Event system
│   └── controller/               # Game controllers
├── client/                       # Web client
│   ├── index.html
│   └── client.js
└── pom.xml                       # Maven configuration
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is open source and available under the MIT License.

---

**Have fun lying! 🃏🎭**