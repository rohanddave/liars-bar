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

  private GameImpl(Builder builder) {
    this.deck = builder.deck != null ? builder.deck : new DeckImpl();
    this.players = new ArrayList<>(builder.maxPlayers);
    this.rank = builder.rank;
    this.claims = new ArrayList<>();

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
    for (Player player : this.players) {
      Hand hand = new HandImpl(deck.drawNRandomCards(5));
      player.setHand(hand);
      player.setRevolver(new RevolverImpl());
      player.getRevolver().reset();
    }
  }

  @Override
  public void playCard(Player player, Card card, Card claimedCard) {

  }

  @Override
  public void claim(Player player, int count, List<Card> cards, Rank claimedRank) throws NoSuchCardException {
    Claim claim = player.claim(rank, count, cards);
    this.claims.add(claim);
    this.moveToNextMove();
  }

  @Override
  public Player challengeClaim(Player player) {
    // TODO: throw appropriate error if player == claim.getPlayer()

    Claim lastClaim = getLastClaim();
    boolean isChallengeSuccessful = !lastClaim.isValidClaim();
    this.moveToNextMove();
    return isChallengeSuccessful ? lastClaim.getPlayer() : player;
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
    return this.claims.getLast();
  }

  @Override
  public Player getCurrentPlayer() {
    return this.players.get(currentPlayingPlayerIndex);
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
    return this.getActivePlayers().size() == 1;
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
    // TODO: implement this when want to integrate rounds
    throw new UnsupportedOperationException();
  }

  @Override
  public void resetGame() {
    // TODO: implement this when want to integrate rounds
    throw new UnsupportedOperationException();
  }

  @Override
  public Rank getRank() {
    return this.rank;
  }

  @Override
  public void moveToNextMove() {
    this.currentPlayingPlayerIndex = (this.currentPlayingPlayerIndex + 1) % this.getActivePlayers().size();
  }
}
