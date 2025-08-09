package com.tfc.liarsbar.model.game;

import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.events.GameEventType;
import com.tfc.liarsbar.model.exceptions.InvalidClaimException;
import com.tfc.liarsbar.model.exceptions.NoActiveClaimException;
import com.tfc.liarsbar.model.exceptions.NoSuchCardException;
import com.tfc.liarsbar.model.exceptions.NotPlayerTurnException;

import java.util.ArrayList;
import java.util.List;

import static com.tfc.liarsbar.model.game.GameConstants.CARDS_PER_RANK;


public class RoundImpl implements Round {
  private final Rank rank;
  private List<Player> activePlayers;
  private int currentPlayerIndex;
  private final List<Claim> claims;
  private int totalClaimedCards;
  private final GameEventPublisher eventPublisher;
  
  public RoundImpl(Rank rank, GameEventPublisher eventPublisher) {
    this.rank = rank;
    this.activePlayers = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.claims = new ArrayList<>();
    this.totalClaimedCards = 0;
    this.eventPublisher = eventPublisher;
  }
  
  @Override
  public Rank getRank() {
    return this.rank;
  }
  
  @Override
  public void startRound(List<Player> players) {
    this.activePlayers = players.stream()
        .filter(Player::isAlive)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    
    this.currentPlayerIndex = 0;
    this.claims.clear();
    this.totalClaimedCards = 0;
    
    eventPublisher.publishEvent(GameEventType.ROUND_STARTED,
        "Starting round with rank: " + this.rank + " (" + this.activePlayers.size() + " active players)");
  }
  
  @Override
  public void claim(Player player, int count, List<Card> cards, Rank claimedRank) throws NoSuchCardException {
    if (!claimedRank.equals(this.rank)) {
      throw new InvalidClaimException("Must claim " + this.rank + " in this round");
    }
    
    if (!player.equals(getCurrentPlayer())) {
      throw new NotPlayerTurnException("Not " + player.getId() + "'s turn");
    }
    
    Claim claim = player.claim(claimedRank, count, cards);
    this.claims.add(claim);
    this.totalClaimedCards += count;
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
    
    boolean isChallengeSuccessful = !lastClaim.isValidClaim();
    
    String resultMessage;
    if (isChallengeSuccessful) {
      resultMessage = "Challenge successful! " + lastClaim.getPlayer().getName() + " was lying";
    } else {
      resultMessage = "Challenge failed! " + lastClaim.getPlayer().getName() + " was telling the truth";
    }
    eventPublisher.publishEvent(GameEventType.CHALLENGE_RESULT, resultMessage);
    
    this.moveToNextPlayer();
    
    // Return the player who must spin the revolver
    return isChallengeSuccessful ? lastClaim.getPlayer() : player;
  }
  
  @Override
  public Claim getLastClaim() {
    if (claims.isEmpty()) {
      return null;
    }

    Claim lastClaim = this.claims.get(this.claims.size() - 1);

    return lastClaim.isSettled() ? null : lastClaim;
  }

  @Override
  public void settleLastClaim() {
    if (claims.isEmpty()) {
      return;
    }
    
    // Find the last unsettled claim and settle it
    for (int i = claims.size() - 1; i >= 0; i--) {
      Claim claim = claims.get(i);
      if (!claim.isSettled()) {
        claim.settle();
        return;
      }
    }
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
    return activePlayers.stream()
        .filter(Player::isAlive)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
  }
  
  @Override
  public boolean isRoundComplete() {
    // Round is complete if:
    // 1. Only one or fewer players remain active
    // 2. All cards of this rank have been claimed (based on standard deck)
    List<Player> currentlyActive = getActivePlayers();
    boolean complete = currentlyActive.size() <= 1 || totalClaimedCards >= CARDS_PER_RANK;
    
    if (complete) {
      String reason;
      if (currentlyActive.size() <= 1) {
        reason = "Only " + currentlyActive.size() + " player(s) remaining";
      } else {
        reason = "All " + CARDS_PER_RANK + " " + this.rank + " cards have been claimed";
      }
      eventPublisher.publishEvent(GameEventType.ROUND_ENDED, "Round complete: " + reason);
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
      
      // Skip players who have run out of cards
      Player currentPlayer = getCurrentPlayer();
      int attempts = 0;
      while (currentPlayer != null && 
             (currentPlayer.getHand() == null || currentPlayer.getHand().getSize() == 0) && 
             attempts < activePlayers.size()) {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % activePlayers.size();
        currentPlayer = getCurrentPlayer();
        attempts++;
      }
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