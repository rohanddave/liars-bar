class LiarsBarClient {
    constructor() {
        this.ws = null;
        this.playerId = null;
        this.currentRoom = null;
        this.gameState = {
            players: [],
            currentPlayer: null,
            currentRound: null,
            lastClaim: null,
            myHand: []
        };
        this.isReconnecting = false;
        
        this.setupEventHandlers();
        this.connect();
    }
    
    connect() {
        const wsUrl = `ws://localhost:8887`;
        this.updateConnectionStatus('connecting');
        this.addMessage('system', 'Connecting to server...');
        
        try {
            this.ws = new WebSocket(wsUrl);
            this.ws.onopen = (event) => this.onOpen(event);
            this.ws.onmessage = (event) => this.onMessage(event);
            this.ws.onclose = (event) => this.onClose(event);
            this.ws.onerror = (event) => this.onError(event);
        } catch (error) {
            this.addMessage('error', 'Failed to connect: ' + error.message);
            this.updateConnectionStatus('disconnected');
        }
    }
    
    onOpen(event) {
        this.updateConnectionStatus('connected');
        this.addMessage('system', '✅ Connected to Liar\'s Bar server!');
        this.isReconnecting = false;
        
        // Send ping to test connection
        this.sendMessage('PING', null, null, { test: 'connection' });
    }
    
    onMessage(event) {
        try {
            const message = JSON.parse(event.data);
            this.handleServerMessage(message);
        } catch (error) {
            console.error('Failed to parse message:', error);
            this.addMessage('error', 'Received invalid message from server');
        }
    }
    
    onClose(event) {
        this.updateConnectionStatus('disconnected');
        this.addMessage('system', `❌ Connection closed: ${event.reason || 'Unknown reason'}`);
        
        if (!this.isReconnecting) {
            this.isReconnecting = true;
            // Attempt to reconnect after 3 seconds
            setTimeout(() => {
                this.addMessage('system', '🔄 Attempting to reconnect...');
                this.connect();
            }, 3000);
        }
    }
    
    onError(event) {
        console.error('WebSocket error:', event);
        this.addMessage('error', '⚠️ Connection error occurred');
        this.updateConnectionStatus('disconnected');
    }
    
    sendMessage(type, playerId = null, roomId = null, data = null) {
        if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
            this.addMessage('error', 'Not connected to server');
            return false;
        }
        
        const message = {
            type: type,
            playerId: playerId || this.playerId,
            roomId: roomId || this.currentRoom,
            data: data,
            timestamp: Date.now()
        };
        
        try {
            this.ws.send(JSON.stringify(message));
            return true;
        } catch (error) {
            this.addMessage('error', 'Failed to send message: ' + error.message);
            return false;
        }
    }
    
    handleServerMessage(message) {
        console.log('Received message:', message);
        
        switch (message.type) {
            case 'PING':
                this.addMessage('system', 'Ping received');
                break;
                
            case 'ROOM_JOINED':
                this.handleRoomJoined(message);
                break;
                
            case 'ROOM_LEFT':
                this.handleRoomLeft(message);
                break;
                
            case 'PLAYER_JOINED':
                this.handlePlayerJoined(message);
                break;
                
            case 'PLAYER_LEFT':
                this.handlePlayerLeft(message);
                break;
                
            case 'GAME_STARTED':
                this.handleGameStarted(message);
                break;
                
            case 'GAME_STATE_UPDATE':
                this.handleGameStateUpdate(message);
                break;
                
            case 'TURN_CHANGED':
                this.handleTurnChanged(message);
                break;
                
            case 'CLAIM_MADE':
                this.handleClaimMade(message);
                break;
                
            case 'CHALLENGE_RESULT':
                this.handleChallengeResult(message);
                break;
                
            case 'SHOOT_RESULT':
                this.handleShootResult(message);
                break;
                
            case 'GAME_ENDED':
                this.handleGameEnded(message);
                break;
                
            case 'ERROR':
                this.handleError(message);
                break;
                
            default:
                console.warn('Unknown message type:', message.type);
        }
        
        this.updateUI();
    }
    
    handleRoomJoined(message) {
        this.playerId = message.playerId;
        this.currentRoom = message.roomId;
        const data = message.data;
        
        this.addMessage('system', `🎉 Joined room ${this.currentRoom}`);
        this.updateRoomInfo(data);
    }
    
    handleRoomLeft(message) {
        this.addMessage('system', '👋 Left the room');
        this.currentRoom = null;
        this.playerId = null;
        this.gameState = {
            players: [],
            currentPlayer: null,
            currentRound: null,
            lastClaim: null,
            myHand: []
        };
        this.updateRoomInfo();
    }
    
    handlePlayerJoined(message) {
        const data = message.data;
        this.addMessage('game', `👤 ${data.name} joined the room`);
        this.updatePlayersList();
    }
    
    handlePlayerLeft(message) {
        const data = message.data;
        this.addMessage('game', `👤 ${data.name} left the room`);
        this.updatePlayersList();
    }
    
    handleGameStarted(message) {
        this.addMessage('game', '🚀 Game has started! Let the lying begin...');
        const data = message.data;
        if (data) {
            this.gameState.currentRound = data.currentRound;
            this.gameState.currentPlayer = data.currentPlayerId;
        }
    }
    
    handleGameStateUpdate(message) {
        const data = message.data;
        if (data) {
            this.gameState.currentRound = data.currentRound || this.gameState.currentRound;
            this.gameState.currentPlayer = data.currentPlayerId || this.gameState.currentPlayer;
        }
        this.addMessage('game', data.message || 'Game state updated');
    }
    
    handleTurnChanged(message) {
        const data = message.data;
        this.gameState.currentPlayer = data.currentPlayerId;
        this.addMessage('game', `🎯 It's ${data.currentPlayerName}'s turn`);
    }
    
    handleClaimMade(message) {
        const data = message.data;
        if (data.claim) {
            const claim = data.claim;
            this.gameState.lastClaim = claim;
            this.addMessage('game', `🎲 ${claim.playerId} claims ${claim.count} ${claim.rank}(s)`);
        }
    }
    
    handleChallengeResult(message) {
        const data = message.data;
        this.addMessage('game', `⚔️ Challenge result: ${data.message}`);
    }
    
    handleShootResult(message) {
        const data = message.data;
        this.addMessage('game', `🔫 ${data.message}`);
    }
    
    handleGameEnded(message) {
        const data = message.data;
        if (data.winnerId) {
            this.addMessage('game', `🏆 Game Over! Winner: ${data.winnerName}`);
        } else {
            this.addMessage('game', `🏁 Game ended: ${data.reason || 'Unknown reason'}`);
        }
    }
    
    handleError(message) {
        const errorMsg = typeof message.data === 'string' ? message.data : 'Unknown error';
        this.addMessage('error', `❌ Error: ${errorMsg}`);
    }
    
    // UI Methods
    updateConnectionStatus(status) {
        const statusElement = document.getElementById('connectionStatus');
        statusElement.className = `connection-status ${status}`;
        statusElement.textContent = status.charAt(0).toUpperCase() + status.slice(1);
    }
    
    addMessage(type, text) {
        const messages = document.getElementById('messages');
        const messageElement = document.createElement('div');
        messageElement.className = `message ${type}`;
        messageElement.textContent = `[${new Date().toLocaleTimeString()}] ${text}`;
        messages.appendChild(messageElement);
        messages.scrollTop = messages.scrollHeight;
    }
    
    updateRoomInfo(data = null) {
        document.getElementById('currentRoom').textContent = this.currentRoom || 'None';
        if (data) {
            document.getElementById('playerCount').textContent = `${data.playerCount}/${data.maxPlayers}`;
        } else {
            document.getElementById('playerCount').textContent = '0/4';
        }
    }
    
    updatePlayersList() {
        const list = document.getElementById('playersList');
        list.innerHTML = '';
        
        this.gameState.players.forEach(player => {
            const li = document.createElement('li');
            li.innerHTML = `
                <span>${player.name}</span>
                <span>${player.id === this.gameState.currentPlayer ? '👑' : ''}</span>
            `;
            
            if (player.id === this.gameState.currentPlayer) {
                li.classList.add('current-turn');
            }
            if (!player.alive) {
                li.classList.add('eliminated');
            }
            
            list.appendChild(li);
        });
    }
    
    updateUI() {
        // Update game state display
        document.getElementById('currentRound').textContent = this.gameState.currentRound || '-';
        document.getElementById('currentPlayer').textContent = 
            this.gameState.currentPlayer === this.playerId ? 'You' : 
            (this.gameState.currentPlayer || '-');
            
        if (this.gameState.lastClaim) {
            const claim = this.gameState.lastClaim;
            document.getElementById('lastClaim').textContent = 
                `${claim.count} ${claim.rank}(s) by ${claim.playerId}`;
        } else {
            document.getElementById('lastClaim').textContent = 'None';
        }
        
        // Update player list
        this.updatePlayersList();
        
        // Update hand display (mock for now)
        const handElement = document.getElementById('handCards');
        if (this.gameState.myHand.length > 0) {
            handElement.textContent = this.gameState.myHand.join(', ');
        } else {
            handElement.textContent = 'No cards';
        }
    }
    
    setupEventHandlers() {
        // Handle page refresh/close
        window.addEventListener('beforeunload', () => {
            if (this.ws && this.ws.readyState === WebSocket.OPEN) {
                this.sendMessage('LEAVE_ROOM');
            }
        });
    }
}

// Game Actions
function joinRoom() {
    const playerName = document.getElementById('playerName').value.trim();
    const roomId = document.getElementById('roomId').value.trim();
    
    if (!playerName) {
        client.addMessage('error', 'Please enter your name');
        return;
    }
    
    const success = client.sendMessage('JOIN_ROOM', null, roomId || null, {
        playerName: playerName
    });
    
    if (success) {
        client.addMessage('system', roomId ? `Joining room ${roomId}...` : 'Creating new room...');
    }
}

function leaveRoom() {
    const success = client.sendMessage('LEAVE_ROOM');
    if (success) {
        client.addMessage('system', 'Leaving room...');
    }
}

function makeClaim() {
    const rank = document.getElementById('claimRank').value;
    const count = parseInt(document.getElementById('claimCount').value);
    
    if (!rank || !count || count < 1) {
        client.addMessage('error', 'Please select a valid rank and count');
        return;
    }
    
    const success = client.sendMessage('MAKE_CLAIM', null, null, {
        rank: rank,
        count: count
    });
    
    if (success) {
        client.addMessage('system', `Making claim: ${count} ${rank}(s)`);
    }
}

function challengeClaim() {
    const success = client.sendMessage('CHALLENGE_CLAIM');
    if (success) {
        client.addMessage('system', 'Challenging the claim...');
    }
}

function shootRevolver() {
    const success = client.sendMessage('SHOOT_REVOLVER');
    if (success) {
        client.addMessage('system', 'Pulling the trigger...');
    }
}

function clearMessages() {
    document.getElementById('messages').innerHTML = '';
}

// Initialize client when page loads
let client;
document.addEventListener('DOMContentLoaded', () => {
    client = new LiarsBarClient();
});