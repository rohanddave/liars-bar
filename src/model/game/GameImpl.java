package model.game;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.GameFullException;
import model.exceptions.GameNotOverException;
import model.exceptions.NoSuchCardException;
import model.events.GameEventPublisher;
import model.events.GameEventType;
import static model.game.GameConstants.*;

public class GameImpl implements Game {
  private final List<Player> players;
  private final Deck deck;
  private final Rank rank;

  private int currentPlayingPlayerIndex = 0;

  private final List<Claim> claims;
  
  // Round management
  private final List<Round> rounds;
  private int currentRoundIndex = 0;
  private Round currentRound;
  
  // Caching for active players
  private List<Player> cachedActivePlayers;
  private boolean activePlayersCacheDirty = true;
  
  // Event publisher for observers
  private final GameEventPublisher eventPublisher;

  private GameImpl(Builder builder) {
    this.deck = builder.deck != null ? builder.deck : new DeckImpl();
    this.players = new ArrayList<>(MAX_PLAYERS);
    this.rank = builder.rank;
    this.claims = new ArrayList<>();
    this.eventPublisher = builder.eventPublisher != null ? builder.eventPublisher : new GameEventPublisher();

    // Add initial players
    this.players.addAll(builder.players);
    
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
    }
  }

  @Override
  public Player challengeClaim(Player player) {
    if (currentRound == null) {
      throw new IllegalStateException("No active round");
    }
    
    eventPublisher.publishEvent(GameEventType.CHALLENGE_MADE, "Player " + player.getName() + " challenges the claim!");
    Player loser = currentRound.challengeClaim(player);
    
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
      cachedActivePlayers = players.stream()
          .filter(Player::isAlive)
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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

  @Override
  public List<Player> getEliminatedPlayers() {
    return players.stream()
        .filter(player -> !player.isAlive())
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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
    
    // Reset players
    for (Player player : players) {
      player.setHand(null);
      player.setRevolver(null);
      // Note: Player.isAlive() state should be reset in player implementation
    }
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
    if (currentRound != null) {
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
  
  /**
   * Gets the current round being played
   * @return The current round, or null if no round is active
   */
  public Round getCurrentRound() {
    return this.currentRound;
  }
  
  /**
   * Gets the current round number (0-based index)
   * @return The current round index
   */
  public int getCurrentRoundNumber() {
    return this.currentRoundIndex;
  }
  
  /**
   * Gets the event publisher for this game
   * @return The event publisher
   */
  public GameEventPublisher getEventPublisher() {
    return this.eventPublisher;
  }
}
