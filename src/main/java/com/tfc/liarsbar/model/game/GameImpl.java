package com.tfc.liarsbar.model.game;

import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.events.GameEventType;
import com.tfc.liarsbar.model.exceptions.GameFullException;
import com.tfc.liarsbar.model.exceptions.GameNotOverException;
import com.tfc.liarsbar.model.exceptions.NoSuchCardException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.Getter;

import static com.tfc.liarsbar.model.game.GameConstants.INITIAL_HAND_SIZE;
import static com.tfc.liarsbar.model.game.GameConstants.MAX_PLAYERS;
import static com.tfc.liarsbar.model.game.GameConstants.ROUND_SEQUENCE;


public class GameImpl implements Game {
  private final List<Player> players; // Keep for compatibility and ordering
  private final Set<Player> activePlayers; // Set of currently active players
  private final Set<Player> eliminatedPlayers; // Set of eliminated players
  private final Deck deck;
  private final Rank rank;
  private boolean gameStarted = false; // Track if game has been started

  private int currentPlayingPlayerIndex = 0;

  private final List<Claim> claims;
  
  // Round management
  private final List<Round> rounds;
  private int currentRoundIndex = 0;
  /**
   * -- GETTER --
   *  Gets the current round being played
   *
   * @return The current round, or null if no round is active
   */
  @Getter
  private Round currentRound;
  
  // Caching for active players - keeping for performance but now backed by Set
  private List<Player> cachedActivePlayers;
  private boolean activePlayersCacheDirty = true;

  /**
   * -- GETTER --
   *  Gets the event publisher for this game
   *
   * @return The event publisher
   */
  // Event publisher for observers
  @Getter
  private final GameEventPublisher eventPublisher;

  public GameImpl(Builder builder) {
    this.deck = builder.deck != null ? builder.deck : new DeckImpl();
    this.players = new ArrayList<>(MAX_PLAYERS);
    this.activePlayers = new CopyOnWriteArraySet<>();
    this.eliminatedPlayers = new CopyOnWriteArraySet<>();
    this.rank = builder.rank;
    this.claims = new ArrayList<>();
    this.eventPublisher = builder.eventPublisher != null ? builder.eventPublisher : new GameEventPublisher();

    // Add initial players
    this.players.addAll(builder.players);
    // Initially all players are active
    this.activePlayers.addAll(builder.players);
    
    // Initialize rounds using constants
    this.rounds = new ArrayList<>();
    for (Rank roundRank : ROUND_SEQUENCE) {
      this.rounds.add(new RoundImpl(roundRank, eventPublisher));
    }
    
    this.currentRoundIndex = 0;
    this.currentRound = null;
  }

  public static class Builder {
    private final List<Player> players = new ArrayList<>();
    private Deck deck;
    private Rank rank;
    private GameEventPublisher eventPublisher;

    public Builder setRank(Rank rank) {
      this.rank = rank;
      return this;
    }

    public Builder withDeck(Deck deck) {
      this.deck = deck;
      return this;
    }

    public Builder withEventPublisher(GameEventPublisher eventPublisher) {
      this.eventPublisher = eventPublisher;
      return this;
    }

    public Builder addPlayer(Player player) throws GameFullException {
      if (players.size() >= MAX_PLAYERS) {
        throw new GameFullException("Cannot add more than " + MAX_PLAYERS + " players.");
      }
      players.add(player);
      return this;
    }

    public Game build() {
      // Use provided deck or create default one
      if (this.deck == null) {
        this.deck = new DeckImpl();
      }

      return new GameImpl(this);
    }
  }

  @Override
  public void startGame() {
    if (gameStarted) {
      throw new IllegalStateException("Game has already been started");
    }
    
    eventPublisher.publishEvent(GameEventType.GAME_STARTED, "Starting new game with " + this.players.size() + " players");
    
    for (Player player : this.players) {
      Hand hand = new HandImpl(deck.drawNRandomCards(INITIAL_HAND_SIZE));
      player.setHand(hand);
      player.setRevolver(new RevolverImpl());
      player.getRevolver().reset();
      eventPublisher.publishEvent(GameEventType.PLAYER_INITIALIZED, 
          "Player " + player.getName() + " initialized with " + INITIAL_HAND_SIZE + " cards and revolver");
    }
    
    // Start the first round
    this.currentRoundIndex = 0;
    this.currentRound = this.rounds.get(currentRoundIndex);
    this.currentRound.startRound(this.getActivePlayers());
    
    // Mark game as started
    this.gameStarted = true;
  }

  @Override
  public void playCard(Player player, Card card, Card claimedCard) {

  }

  @Override
  public void claim(Player player, int count, List<Card> cards, Rank claimedRank) throws NoSuchCardException {
    if (currentRound == null) {
      throw new IllegalStateException("No active round");
    }
    
    eventPublisher.publishEvent(GameEventType.CLAIM_MADE, "Player " + player.getName() + " claims " + count + " " + claimedRank + "(s)");
    currentRound.claim(player, count, cards, claimedRank);
    
    // Check if round is complete and advance to next round if needed
    if (currentRound.isRoundComplete()) {
      advanceToNextRound();
    } else {
      this.moveToNextMove();
    }
  }

  @Override
  public Player challengeClaim(Player player) {
    if (currentRound == null) {
      throw new IllegalStateException("No active round");
    }
    
    eventPublisher.publishEvent(GameEventType.CHALLENGE_MADE, "Player " + player.getName() + " challenges the claim!");
    Player loser = currentRound.challengeClaim(player);
    loser.shoot();

    // Player status may change after challenge, invalidate cache
    invalidateActivePlayersCache();
    
    // Check if round is complete and advance to next round if needed
    if (currentRound.isRoundComplete()) {
      advanceToNextRound();
    }
    
    return loser;
  }

  @Override
  public void spinRevolver(Player player) {
    player.getRevolver().reset();
  }

  @Override
  public GameState getGameState() {
    return null;
  }

  @Override
  public Claim getLastClaim() {
    if (currentRound == null) {
      return null;
    }
    return currentRound.getLastClaim();
  }

  @Override
  public void settleLastClaim() {
    if (currentRound != null) {
      currentRound.settleLastClaim();
    }
  }

  @Override
  public Player getCurrentPlayer() {
    if (currentRound == null) {
      return null;
    }
    return currentRound.getCurrentPlayer();
  }

  @Override
  public List<Player> getActivePlayers() {
    if (activePlayersCacheDirty || cachedActivePlayers == null) {
      System.out.println("Returning active players from cache");
      System.out.println("Total players: " + this.players.size() + ", Active set size: " + this.activePlayers.size());
      
      // Sync activePlayers set with actual player state
      syncPlayerSets();
      
      cachedActivePlayers = new ArrayList<>(this.activePlayers);
      activePlayersCacheDirty = false;
    }
    return new ArrayList<>(cachedActivePlayers); // Return defensive copy
  }
  
  /**
   * Invalidates the active players cache when player status changes
   */
  private void invalidateActivePlayersCache() {
    activePlayersCacheDirty = true;
  }
  
  /**
   * Synchronizes the active and eliminated player sets with actual player states
   */
  private void syncPlayerSets() {
    // Clear and rebuild sets based on current player states
    Set<Player> currentlyActive = new CopyOnWriteArraySet<>();
    Set<Player> currentlyEliminated = new CopyOnWriteArraySet<>();
    
    for (Player player : this.players) {
      if (player.isAlive()) {
        currentlyActive.add(player);
      } else {
        currentlyEliminated.add(player);
      }
    }
    
    // Update sets atomically
    this.activePlayers.clear();
    this.activePlayers.addAll(currentlyActive);
    
    this.eliminatedPlayers.clear();
    this.eliminatedPlayers.addAll(currentlyEliminated);
  }
  
  /**
   * Eliminates a player from the game
   * @param player The player to eliminate
   */
  public void eliminatePlayer(Player player) {
    if (player == null || !this.players.contains(player)) {
      return;
    }
    
    // Move player from active to eliminated
    this.activePlayers.remove(player);
    this.eliminatedPlayers.add(player);
    
    // Invalidate cache
    invalidateActivePlayersCache();
    
    eventPublisher.publishEvent(GameEventType.PLAYER_ELIMINATED, 
        "Player " + player.getName() + " has been eliminated from the game");
  }
  
  /**
   * Removes a player completely from the game
   * @param player The player to remove
   */
  public void removePlayer(Player player) {
    if (player == null) {
      return;
    }
    
    this.players.remove(player);
    this.activePlayers.remove(player);
    this.eliminatedPlayers.remove(player);
    
    // Invalidate cache
    invalidateActivePlayersCache();
    
    eventPublisher.publishEvent(GameEventType.ROOM_LEFT, 
        "Player " + player.getName() + " left the game");
  }

  @Override
  public List<Player> getEliminatedPlayers() {
    // Sync player sets to ensure consistency
    syncPlayerSets();
    return new ArrayList<>(this.eliminatedPlayers);
  }

  @Override
  public boolean isGameOver() {
    long activePlayerCount = players.stream()
        .filter(Player::isAlive)
        .count();
    
    boolean gameOver = activePlayerCount <= 1;
    if (gameOver && activePlayerCount == 1) {
      Player winner = players.stream()
          .filter(Player::isAlive)
          .findFirst()
          .orElse(null);
      if (winner != null) {
        eventPublisher.publishEvent(GameEventType.GAME_ENDED, "Game Over! Winner: " + winner.getName());
      }
    }
    return gameOver;
  }

  @Override
  public Player getWinner() {
    return players.stream()
        .filter(Player::isAlive)
        .reduce((first, second) -> {
          throw new GameNotOverException("Multiple players still alive!");
        })
        .orElseThrow(() -> new GameNotOverException("No players alive!"));
  }

  @Override
  public int getRevolverChamberPosition(Player player) {
    return player.getRevolver().getCurrentIndex();
  }

  @Override
  public int getPlayerCardCount(Player player) {
    return player.getHand().getSize();
  }

  @Override
  public boolean isRoundComplete() {
    if (currentRound == null) {
      return false;
    }
    return currentRound.isRoundComplete();
  }

  @Override
  public void resetGame() {
    // Reset all rounds
    for (Round round : rounds) {
      round.resetRound();
    }
    
    // Reset game state
    this.currentRoundIndex = 0;
    this.currentRound = null;
    this.claims.clear();
    this.currentPlayingPlayerIndex = 0;
    this.gameStarted = false;
    
    // Reset player sets - move all players back to active
    this.activePlayers.clear();
    this.eliminatedPlayers.clear();
    this.activePlayers.addAll(this.players);
    
    // Reset players
    for (Player player : players) {
      player.setHand(null);
      player.setRevolver(null);
      // Note: Player.isAlive() state should be reset in player implementation
    }
    
    // Invalidate cache
    invalidateActivePlayersCache();
  }

  @Override
  public void addPlayer(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    
    this.players.add(player);
    this.activePlayers.add(player);
    
    // Remove from eliminated set if somehow present
    this.eliminatedPlayers.remove(player);
    
    // Invalidate cache since player list changed
    invalidateActivePlayersCache();
    
    eventPublisher.publishEvent(GameEventType.PLAYER_INITIALIZED, 
        "Player " + player.getName() + " joined the game");
  }

  @Override
  public Rank getRank() {
    if (currentRound == null) {
      return this.rank; // fallback to initial rank
    }
    return currentRound.getRank();
  }

  @Override
  public void moveToNextMove() {
    System.out.println("[GameImpl]: move to next move called");
    if (currentRound != null) {
      System.out.println("current round != null");
      currentRound.moveToNextPlayer();
      Player nextPlayer = getCurrentPlayer();
      if (nextPlayer != null) {
        eventPublisher.publishEvent(GameEventType.TURN_CHANGED, "Turn passed to: " + nextPlayer.getName());
      }
    }
  }
  
  /**
   * Advances to the next round in the sequence (Aces -> Kings -> Queens -> Jacks)
   * If all rounds are complete, the game continues with the same round sequence
   */
  private void advanceToNextRound() {
    if (isGameOver()) {
      return; // Don't advance if game is over
    }
    
    this.currentRoundIndex = (this.currentRoundIndex + 1) % this.rounds.size();
    this.currentRound = this.rounds.get(currentRoundIndex);
    this.currentRound.startRound(this.getActivePlayers());
  }

  @Override
  public boolean isGameStarted() {
    return this.gameStarted;
  }
  
  /**
   * Gets the current round number (0-based index)
   * @return The current round index
   */
  public int getCurrentRoundNumber() {
    return this.currentRoundIndex;
  }

}
