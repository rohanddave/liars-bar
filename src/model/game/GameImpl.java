package model.game;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.GameFullException;
import model.exceptions.NoSuchCardException;

public class GameImpl implements Game {
  private final List<Player> players;
  private final Deck deck;
  private final Rank rank;

  private int currentPlayingPlayerIndex = 0;

  private GameImpl(Builder builder) {
    this.deck = builder.deck != null ? builder.deck : new DeckImpl();
    this.players = new ArrayList<>(builder.maxPlayers);
    this.rank = builder.rank;

    // Add initial players
    this.players.addAll(builder.players);
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
    return false;
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
}
