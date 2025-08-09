package model.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.exceptions.GameFullException;
import model.exceptions.NoSuchCardException;

public class GameImpl implements Game {
  private final List<Player> players;
  private final Deck deck;
  private final Rank rank;
  private final Map<Player, Revolver> playerRevolvers;

  private int currentPlayingPlayerIndex = 0;

  private GameImpl(Builder builder) {
    this.deck = builder.deck != null ? builder.deck : new DeckImpl();
    this.players = new ArrayList<>(builder.maxPlayers);
    this.rank = builder.rank;
    this.playerRevolvers = new HashMap<>();

    // Add initial players
    this.players.addAll(builder.players);
    
    // Initialize revolvers for each player
    for (Player player : this.players) {
      this.playerRevolvers.put(player, new RevolverImpl());
    }
  }

  public static class Builder {
    private final int maxPlayers = 4; // default
    private final List<Player> players = new ArrayList<>();
    private Deck deck;
    private Rank rank;

    public Builder setRank(Rank rank) {
      this.rank = rank;
      return this;
    }

    public Builder withDeck(Deck deck) {
      this.deck = deck;
      return this;
    }

    public Builder addPlayer(Player player) throws GameFullException {
      if (players.size() >= maxPlayers) {
        throw new GameFullException("Cannot add more than " + maxPlayers + " players.");
      }
      players.add(player);
      return this;
    }

    public Game build() {
      // Inject deck if provided
      if (deck != null) {
        this.deck = new DeckImpl();
      }

      return new GameImpl(this);
    }
  }

  @Override
  public void startGame() {

  }

  @Override
  public void playCard(Player player, Card card, Card claimedCard) {

  }

  @Override
  public void claim(Player player, int count, List<Card> cards, Rank claimedRank) throws NoSuchCardException {
    player.claim(rank, count, cards);
  }

  @Override
  public void challengeClaim(Player player) {

  }

  @Override
  public boolean spinRevolver(Player player) {
    if (!player.isAlive()) {
      throw new IllegalArgumentException("Player " + player.getId() + " is already eliminated");
    }
    
    Revolver revolver = playerRevolvers.get(player);
    if (revolver == null) {
      throw new IllegalArgumentException("No revolver found for player " + player.getId());
    }
    
    // Player shoots their revolver
    boolean eliminated = revolver.shoot();
    
    if (eliminated) {
      // Player is eliminated - this will be handled by the Player's shoot() method
      player.shoot();
    }
    
    return eliminated;
  }

  @Override
  public GameState getGameState() {
    return null;
  }

  @Override
  public Claim getLastClaim() {
    return null;
  }

  @Override
  public Player getCurrentPlayer() {
    return this.players.get(currentPlayingPlayerIndex);
  }

  @Override
  public List<Player> getActivePlayers() {
    return this.players;
  }

  @Override
  public List<Player> getEliminatedPlayers() {
    return List.of();
  }

  @Override
  public boolean isGameOver() {
    return false;
  }

  @Override
  public Player getWinner() {
    return null;
  }

  @Override
  public int getRevolverChamberPosition(Player player) {
    Revolver revolver = playerRevolvers.get(player);
    if (revolver == null || !player.isAlive()) {
      return -1;
    }
    
    if (revolver instanceof RevolverImpl) {
      return ((RevolverImpl) revolver).getCurrentChamber();
    }
    
    return 0;
  }

  @Override
  public int getPlayerCardCount(Player player) {
    return 0;
  }

  @Override
  public boolean isRoundComplete() {
    return false;
  }

  @Override
  public void resetGame() {

  }

  @Override
  public Rank getRank() {
    return this.rank;
  }
  
  /**
   * Gets the revolver for a specific player (for testing and display purposes).
   * @param player The player whose revolver to get
   * @return The player's revolver, or null if not found
   */
  public Revolver getPlayerRevolver(Player player) {
    return playerRevolvers.get(player);
  }
}
