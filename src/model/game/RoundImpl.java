package model.game;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.InvalidClaimException;
import model.exceptions.NoActiveClaimException;
import model.exceptions.NoSuchCardException;
import model.exceptions.NotPlayerTurnException;

public class RoundImpl implements Round {
  private final Rank rank;
  private List<Player> activePlayers;
  private int currentPlayerIndex;
  private final List<Claim> claims;
  private int totalClaimedCards;
  
  public RoundImpl(Rank rank) {
    this.rank = rank;
    this.activePlayers = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.claims = new ArrayList<>();
    this.totalClaimedCards = 0;
  }
  
  @Override
  public Rank getRank() {
    return this.rank;
  }
  
  @Override
  public void startRound(List<Player> players) {
    System.out.println("🎲 Starting round with rank: " + this.rank);
    this.activePlayers = new ArrayList<>();
    for (Player player : players) {
      if (player.isAlive()) {
        this.activePlayers.add(player);
        System.out.println("  👤 Added active player: " + player.getName());
      }
    }
    this.currentPlayerIndex = 0;
    this.claims.clear();
    this.totalClaimedCards = 0;
    System.out.println("  📊 Round started with " + this.activePlayers.size() + " active players");
  }
  
  @Override
  public void claim(Player player, int count, List<Card> cards, Rank claimedRank) throws NoSuchCardException {
    if (!claimedRank.equals(this.rank)) {
      throw new InvalidClaimException("Must claim " + this.rank + " in this round");
    }
    
    if (!player.equals(getCurrentPlayer())) {
      throw new NotPlayerTurnException("Not " + player.getId() + "'s turn");
    }
    
    System.out.println("📋 Processing claim from " + player.getName() + ": " + count + " " + claimedRank + "(s)");
    Claim claim = player.claim(claimedRank, count, cards);
    this.claims.add(claim);
    this.totalClaimedCards += count;
    System.out.println("  📈 Total claimed cards in round: " + this.totalClaimedCards + "/4");
    this.moveToNextPlayer();
  }
  
  @Override
  public Player challengeClaim(Player player) {
    if (claims.isEmpty()) {
      throw new NoActiveClaimException("No claim to challenge");
    }
    
    if (!player.equals(getCurrentPlayer())) {
      throw new NotPlayerTurnException("Not " + player.getId() + "'s turn");
    }
    
    Claim lastClaim = getLastClaim();
    
    // Prevent player from challenging their own claim
    if (player.equals(lastClaim.getPlayer())) {
      throw new IllegalArgumentException("Cannot challenge your own claim");
    }
    
    System.out.println("🔍 " + player.getName() + " challenges " + lastClaim.getPlayer().getName() + "'s claim");
    boolean isChallengeSuccessful = !lastClaim.isValidClaim();
    
    if (isChallengeSuccessful) {
      System.out.println("  ✅ Challenge successful! " + lastClaim.getPlayer().getName() + " was lying");
    } else {
      System.out.println("  ❌ Challenge failed! " + lastClaim.getPlayer().getName() + " was telling the truth");
    }
    
    this.moveToNextPlayer();
    
    // Return the player who must spin the revolver
    return isChallengeSuccessful ? lastClaim.getPlayer() : player;
  }
  
  @Override
  public Claim getLastClaim() {
    if (claims.isEmpty()) {
      return null;
    }
    return this.claims.get(claims.size() - 1);
  }
  
  @Override
  public Player getCurrentPlayer() {
    if (activePlayers.isEmpty()) {
      return null;
    }
    return this.activePlayers.get(currentPlayerIndex);
  }
  
  @Override
  public List<Player> getActivePlayers() {
    List<Player> currentlyActive = new ArrayList<>();
    for (Player player : activePlayers) {
      if (player.isAlive()) {
        currentlyActive.add(player);
      }
    }
    return currentlyActive;
  }
  
  @Override
  public boolean isRoundComplete() {
    // Round is complete if:
    // 1. Only one or fewer players remain active
    // 2. All 4 cards of this rank have been claimed (there are 4 cards per rank in a deck)
    List<Player> currentlyActive = getActivePlayers();
    boolean complete = currentlyActive.size() <= 1 || totalClaimedCards >= 4;
    
    if (complete) {
      if (currentlyActive.size() <= 1) {
        System.out.println("🏁 Round complete: Only " + currentlyActive.size() + " player(s) remaining");
      } else {
        System.out.println("🏁 Round complete: All 4 " + this.rank + " cards have been claimed");
      }
    }
    
    return complete;
  }
  
  @Override
  public List<Claim> getAllClaims() {
    return new ArrayList<>(this.claims);
  }
  
  @Override
  public void moveToNextPlayer() {
    if (activePlayers.isEmpty()) {
      return;
    }
    
    // Update active players list to remove eliminated players
    this.activePlayers = getActivePlayers();
    
    if (!activePlayers.isEmpty()) {
      this.currentPlayerIndex = (this.currentPlayerIndex + 1) % activePlayers.size();
    }
  }
  
  @Override
  public int getTotalClaimedCards() {
    return this.totalClaimedCards;
  }
  
  @Override
  public void resetRound() {
    this.activePlayers.clear();
    this.currentPlayerIndex = 0;
    this.claims.clear();
    this.totalClaimedCards = 0;
  }
}