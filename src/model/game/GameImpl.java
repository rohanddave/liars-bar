package model.game;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.GameFullException;
import model.exceptions.GameNotOverException;
import model.exceptions.NoSuchCardException;

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

  private GameImpl(Builder builder) {
    this.deck = builder.deck != null ? builder.deck : new DeckImpl();
    this.players = new ArrayList<>(builder.maxPlayers);
    this.rank = builder.rank;
    this.claims = new ArrayList<>();

    // Add initial players
    this.players.addAll(builder.players);
    
    // Initialize rounds (Aces, Kings, Queens, Jacks)
    this.rounds = new ArrayList<>();
    this.rounds.add(new RoundImpl(Rank.ACE));
    this.rounds.add(new RoundImpl(Rank.KING));
    this.rounds.add(new RoundImpl(Rank.QUEEN));
    this.rounds.add(new RoundImpl(Rank.JACK));
    
    this.currentRoundIndex = 0;
    this.currentRound = null;
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
    System.out.println("🎮 Starting new game with " + this.players.size() + " players");
    
    for (Player player : this.players) {
      Hand hand = new HandImpl(deck.drawNRandomCards(5));
      player.setHand(hand);
      player.setRevolver(new RevolverImpl());
      player.getRevolver().reset();
      System.out.println("  ✅ Player " + player.getName() + " initialized with 5 cards and revolver");
    }
    
    // Start the first round
    this.currentRoundIndex = 0;
    this.currentRound = this.rounds.get(currentRoundIndex);
    System.out.println("🔄 Starting first round: " + this.currentRound.getRank());
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
    
    System.out.println("🃏 Player " + player.getName() + " claims " + count + " " + claimedRank + "(s)");
    currentRound.claim(player, count, cards, claimedRank);
    
    // Check if round is complete and advance to next round if needed
    if (currentRound.isRoundComplete()) {
      System.out.println("📍 Round complete, advancing to next round");
      advanceToNextRound();
    }
  }

  @Override
  public Player challengeClaim(Player player) {
    if (currentRound == null) {
      throw new IllegalStateException("No active round");
    }
    
    System.out.println("⚔️ Player " + player.getName() + " challenges the claim!");
    Player loser = currentRound.challengeClaim(player);
    System.out.println("💀 Player " + loser.getName() + " must spin the revolver");
    
    // Check if round is complete and advance to next round if needed
    if (currentRound.isRoundComplete()) {
      System.out.println("📍 Round complete, advancing to next round");
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
  public Player getCurrentPlayer() {
    if (currentRound == null) {
      return null;
    }
    return currentRound.getCurrentPlayer();
  }

  @Override
  public List<Player> getActivePlayers() {
    List<Player> activePlayers = new ArrayList<>();
    for (Player player : players) {
      if (player.isAlive()) {
        activePlayers.add(player);
      }
    }
    return activePlayers;
  }

  @Override
  public List<Player> getEliminatedPlayers() {
    List<Player> eliminatedPlayers = new ArrayList<>();
    for (Player player : players) {
      if (!player.isAlive()) {
        eliminatedPlayers.add(player);
      }
    }
    return eliminatedPlayers;
  }

  @Override
  public boolean isGameOver() {
    boolean gameOver = this.getActivePlayers().size() == 1;
    if (gameOver) {
      System.out.println("🏆 Game Over! Winner: " + getWinner().getName());
    }
    return gameOver;
  }

  @Override
  public Player getWinner() {
    if (this.getActivePlayers().size() != 1) throw new GameNotOverException("No winner yet!");

    return this.getActivePlayers().get(0);
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
        System.out.println("🔄 Turn passed to: " + nextPlayer.getName());
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
    System.out.println("🎯 Advancing to round: " + this.currentRound.getRank() + " (Round " + (currentRoundIndex + 1) + ")");
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
}
