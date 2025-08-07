# 🚀 Quick Start Guide

## Option 1: Console Mode (Single Player Demo)

```bash
# Compile the game
mkdir -p build/classes
javac -d build/classes $(find src -name "*.java" -not -path "*/server/*")

# Run console mode
java -cp build/classes Main
```

Follow the on-screen prompts to play against AI opponents.

## Option 2: Multiplayer WebSocket Mode

### Prerequisites
You need Maven or manually downloaded JAR dependencies.

#### With Maven (Recommended)
```bash
mvn compile
mvn exec:java -Dexec.mainClass="server.GameServer"
```

#### Manual Compilation (if Maven not available)
1. Download required JARs to `lib/` directory:
   - `Java-WebSocket-1.5.6.jar`
   - `jackson-databind-2.17.0.jar` (+ core & annotations)
   - `slf4j-api-2.0.12.jar` & `logback-classic-1.5.3.jar`

2. Run compilation script:
```bash
./compile.sh
```

3. Start the server:
```bash
java -cp "lib/*:build/classes" server.GameServer
```

### Playing Multiplayer

1. **Start the WebSocket server** (default port 8887)
2. **Open the web client**: Double-click `client/index.html`
3. **Create/Join a room**:
   - Enter your name
   - Leave room ID empty to create new room
   - Share room ID with friends to join existing room
4. **Wait for players** (2-4 players required)
5. **Game starts automatically** when room is full

### Web Client Features

- ✅ Real-time multiplayer gameplay
- ✅ Live connection status
- ✅ Game room management
- ✅ Player turn indicators
- ✅ Interactive game controls
- ✅ Comprehensive game log
- ✅ Mobile-responsive design

## Game Controls (Web Client)

- **Join Room**: Enter name and optional room ID
- **Make Claim**: Select rank (Aces/Kings/Queens/Jacks) and count
- **Challenge**: Challenge the previous player's claim
- **Shoot**: Pull the trigger (after losing a challenge)

## Architecture Overview

```
┌─────────────────┐    WebSocket    ┌─────────────────┐
│   Web Client    │ ←──────────────→ │  GameServer     │
│   (HTML/JS)     │                 │  (Java)         │
└─────────────────┘                 └─────────────────┘
                                            │
                                            ▼
                                    ┌─────────────────┐
                                    │ GameRoomManager │
                                    │                 │
                                    │ ┌─────────────┐ │
                                    │ │  GameRoom   │ │
                                    │ │             │ │
                                    │ │  GameImpl   │ │
                                    │ └─────────────┘ │
                                    └─────────────────┘
```

## Next Steps

- [ ] Add card visualization in web client
- [ ] Implement spectator mode
- [ ] Add game replay functionality
- [ ] Create mobile app client
- [ ] Add tournament mode

---

**Have fun playing Liar's Bar! 🎲🃏**