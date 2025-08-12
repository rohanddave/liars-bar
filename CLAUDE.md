# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **Liars Bar**, a multiplayer WebSocket-based card game implementing Russian Roulette mechanics. Players make claims about their cards and can challenge each other, with losers firing a revolver that may eliminate them from the game.

**Technology Stack:**
- Spring Boot 3.5.4 with Java 21
- WebSocket for real-time communication
- Maven for build management
- Lombok for reducing boilerplate
- HTML/CSS/JavaScript frontend

## Development Commands

**Build and Run:**
```bash
# Build the project
./mvnw clean compile

# Run the application (starts on port 8080)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Package the application
./mvnw package
```

**Development Setup:**
```bash
# Clean and rebuild
./mvnw clean install

# Run with development profile (if available)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Architecture Overview

### Core Game Architecture

The application follows a **layered architecture** with clear separation between networking, game logic, and event handling:

**1. Network Layer (`com.tfc.liarsbar.network`)**
- `RoomWebSocketHandler` - Main WebSocket endpoint handling client connections
- `RoomService` - Manages rooms and user sessions
- `Room/RoomImpl` - Room entity containing game and user management
- `User/UserImpl` - User representation with WebSocket session binding
- `WebSocketGameEventListener` - Bridges game events to WebSocket clients

**2. Game Logic Layer (`com.tfc.liarsbar.model.game`)**
- `Game/GameImpl` - Core game state management and rules
- `Player` - Player entities with hands, revolvers, and state
- `Round/RoundImpl` - Round-based gameplay with rank progression (Aces→Kings→Queens→Jacks)
- `Deck/DeckImpl`, `Hand/HandImpl`, `Card/CardImpl` - Card game primitives
- `Revolver/RevolverImpl` - Russian Roulette mechanics
- `Claim/ClaimImpl` - Player claim representations

**3. Command Pattern Implementation (`com.tfc.liarsbar.model.commands`)**
- `CommandInvoker` - Executes and tracks command history
- `GameCommandProcessor` - Parses text commands from WebSocket
- `AbstractGameCommand` - Base class for all game actions
- Specific commands: `StartCommand`, `ClaimCommand`, `ChallengeCommand`, `ShowHandCommand`
- `ActionCommandFactory` - Factory for creating command instances

**4. Event System (`com.tfc.liarsbar.model.events`)**
- `GameEventPublisher` - Publishes game events to registered listeners
- `GameEventListener` interface - Observer pattern for game events
- `GameEventType` enum - Defines all possible game events
- Event types: GAME_STARTED, CLAIM_MADE, CHALLENGE_MADE, PLAYER_ELIMINATED, etc.

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                   CLIENT LAYER                                      │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  index.html (WebSocket Client)                                                     │
│  ├─ Command validation & suggestions                                               │
│  ├─ Real-time game state display                                                   │
│  └─ Connection management UI                                                       │
└─────────────────┬───────────────────────────────────────────────────────────────────┘
                  │ WebSocket Connection
                  │ ws://localhost:8080/ws/room?roomId=X&username=Y
┌─────────────────▼───────────────────────────────────────────────────────────────────┐
│                              NETWORK LAYER                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  RoomWebSocketHandler ◄──────┐                                                     │
│  ├─ @OnOpen, @OnMessage      │                                                     │
│  ├─ Session management       │                                                     │
│  └─ Command parsing          │                                                     │
│                              │                                                     │
│  RoomService                 │                                                     │
│  ├─ ConcurrentHashMap<rooms> │                                                     │
│  ├─ User session mapping     │                                                     │
│  ├─ addUserToRoom()         │                                                     │
│  └─ removeUser()            │                                                     │
│                              │                                                     │
│  Room/RoomImpl               │                                                     │
│  ├─ Contains Game instance   │                                                     │
│  ├─ User management         │                                                     │
│  ├─ GameEventPublisher      │                                                     │
│  └─ WebSocketGameEventListener ◄─────────────────────────────┐                   │
└─────────────────┬───────────────────────────────────────────┼───────────────────────┘
                  │                                           │
┌─────────────────▼───────────────────────────────────────────┼───────────────────────┐
│                             COMMAND LAYER                   │                       │
├─────────────────────────────────────────────────────────────┼───────────────────────┤
│  GameCommandProcessor                                       │                       │
│  ├─ parseCommand()                                         │                       │
│  └─ CommandParser                                          │                       │
│                                                            │                       │
│  CommandInvoker ─────┬─────────────────────────────────────┤                       │
│  ├─ executeCommand() │                                     │                       │
│  ├─ Command history  │                                     │                       │
│  └─ Validation      │                                     │                       │
│                     │                                     │                       │
│  ActionCommandFactory│                                     │                       │
│  ├─ createStartCommand()                                   │                       │
│  ├─ createClaimCommand()                                   │                       │
│  ├─ createChallengeCommand()                               │                       │
│  └─ createShowHandCommand()                                │                       │
│                     │                                     │                       │
│  AbstractGameCommand│                                     │                       │
│  ├─ StartCommand    │                                     │                       │
│  ├─ ClaimCommand    │                                     │                       │
│  ├─ ChallengeCommand│                                     │                       │
│  └─ ShowHandCommand │                                     │                       │
└─────────────────────┼─────────────────────────────────────┼───────────────────────┘
                      │                                     │
┌─────────────────────▼─────────────────────────────────────┼───────────────────────┐
│                            GAME LOGIC LAYER               │                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  Game/GameImpl ◄──────────────────────────────────────────┘                       │
│  ├─ List<Player> players                                                          │
│  ├─ Set<Player> activePlayers                                                     │
│  ├─ Set<Player> eliminatedPlayers                                                 │
│  ├─ List<Round> rounds                                                            │
│  ├─ Round currentRound                                                            │
│  ├─ GameEventPublisher eventPublisher                                            │
│  ├─ startGame()                                                                   │
│  ├─ claim()                                                                       │
│  ├─ challengeClaim()                                                              │
│  └─ moveToNextMove()                                                              │
│                                                                                   │
│  Player ◄─────────────────────┐                                                  │
│  ├─ Hand hand                 │                                                  │
│  ├─ Revolver revolver         │                                                  │
│  ├─ String name               │                                                  │
│  ├─ boolean isAlive()         │                                                  │
│  └─ shoot()                   │                                                  │
│                               │                                                  │
│  Round/RoundImpl              │                                                  │
│  ├─ Rank currentRank          │                                                  │
│  ├─ List<Claim> claims        │                                                  │
│  ├─ Player currentPlayer      │                                                  │
│  ├─ startRound()              │                                                  │
│  ├─ claim()                   │                                                  │
│  ├─ challengeClaim()          │                                                  │
│  └─ moveToNextPlayer()        │                                                  │
│                               │                                                  │
│  Deck/DeckImpl                │                                                  │
│  ├─ List<Card> cards          │                                                  │
│  ├─ shuffle()                 │                                                  │
│  └─ drawNRandomCards()        │                                                  │
│                               │                                                  │
│  Hand/HandImpl ◄──────────────┤                                                  │
│  ├─ List<Card> cards          │                                                  │
│  ├─ addCard()                 │                                                  │
│  ├─ removeCard()              │                                                  │
│  └─ getSize()                 │                                                  │
│                               │                                                  │
│  Card/CardImpl                │                                                  │
│  ├─ Rank rank                 │                                                  │
│  └─ Suit suit                 │                                                  │
│                               │                                                  │
│  Revolver/RevolverImpl ◄──────┤                                                  │
│  ├─ int currentIndex          │                                                  │
│  ├─ boolean bulletPositions[] │                                                  │
│  ├─ reset()                   │                                                  │
│  └─ pullTrigger()             │                                                  │
│                               │                                                  │
│  Claim/ClaimImpl              │                                                  │
│  ├─ Player player             │                                                  │
│  ├─ int count                 │                                                  │
│  ├─ Rank rank                 │                                                  │
│  ├─ List<Card> cards          │                                                  │
│  └─ boolean settled           │                                                  │
└───────────────────────────────┼───────────────────────────────────────────────────┘
                                │
┌───────────────────────────────▼───────────────────────────────────────────────────┐
│                              EVENT LAYER                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  GameEventPublisher                                                               │
│  ├─ List<GameEventListener> listeners                                            │
│  ├─ publishEvent()                                                               │
│  └─ addListener()                                                                │
│                                                                                   │
│  GameEventListener ◄─────────────────────────┐                                   │
│  └─ onGameEvent()                           │                                   │
│                                             │                                   │
│  GameEventImpl                              │                                   │
│  ├─ GameEventType eventType                 │                                   │
│  ├─ String message                          │                                   │
│  └─ long timestamp                          │                                   │
│                                             │                                   │
│  GameEventType (enum)                       │                                   │
│  ├─ GAME_STARTED                           │                                   │
│  ├─ CLAIM_MADE                             │                                   │
│  ├─ CHALLENGE_MADE                         │                                   │
│  ├─ PLAYER_ELIMINATED                      │                                   │
│  ├─ TURN_CHANGED                           │                                   │
│  └─ GAME_ENDED                             │                                   │
│                                             │                                   │
│  WebSocketGameEventListener ◄───────────────┘                                   │
│  ├─ Map<String, WebSocketSession> playerSessions                                │
│  ├─ ObjectMapper objectMapper                                                   │
│  ├─ onGameEvent()                                                               │
│  ├─ broadcastEventToAll()                                                       │
│  └─ sendEventToSession()                                                        │
└─────────────────────────────────────────────────────────────────────────────────────┘

Flow Legend:
─────► Direct method calls/dependencies
◄───── Implements interface/extends class
```

### Key Design Patterns

1. **Command Pattern** - All player actions are commands for undo/redo and validation
2. **Observer Pattern** - Game events are published to WebSocket listeners
3. **Factory Pattern** - Room creation and command instantiation
4. **Builder Pattern** - Game construction with players and configuration
5. **Strategy Pattern** - Different action types with shared interfaces

### WebSocket Communication Flow

1. Client connects to `/ws/room?roomId=X&username=Y`
2. `RoomWebSocketHandler` creates/retrieves room via `RoomService`
3. Text commands are parsed by `GameCommandProcessor`
4. Commands execute against `Game` instance via `CommandInvoker`
5. Game events are published and sent to all room participants via `WebSocketGameEventListener`

### Game State Management

- **Thread-Safe Collections**: Uses `ConcurrentHashMap` and `CopyOnWriteArraySet`
- **Player Lifecycle**: Active players can become eliminated but remain in game for spectating
- **Round Progression**: Automatic advancement through card ranks (Aces→Kings→Queens→Jacks)
- **Game Reset**: Full state reset while preserving player connections

## Frontend Integration

The `index.html` provides a complete WebSocket client with:
- Real-time command validation and suggestions
- Quick-action buttons for common commands
- Connection management UI
- Game state display

**WebSocket URL Format:** `ws://localhost:8080/ws/room?roomId={roomId}&username={username}`

## Exception Handling

Custom exceptions in `com.tfc.liarsbar.model.exceptions`:
- `GameFullException`, `RoomFullException` - Capacity limits
- `InvalidClaimException`, `NoActiveClaimException` - Game rule violations
- `HandEmptyException`, `NoSuchCardException` - Card management errors
- `NotPlayerTurnException` - Turn validation

## Configuration

**Application Properties:**
- `spring.application.name=liarsbar`
- Default port: 8080
- WebSocket endpoint: `/ws/room`
- CORS: Allows all origins (`*`)

## Testing Strategy

- Unit tests should focus on game logic in `GameImpl`
- Integration tests for WebSocket communication flow
- Command pattern allows easy testing of player actions
- Event system can be mocked for isolated testing

When working with this codebase:
1. Game logic changes should go through the command pattern
2. New features should publish appropriate events
3. WebSocket responses should be JSON formatted via `WebSocketGameEventListener`
4. Thread safety is critical - use provided concurrent collections
5. Player state changes must invalidate caches in `GameImpl`