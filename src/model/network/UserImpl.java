package model.network;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import model.game.Card;
import model.game.Claim;
import model.game.ClaimImpl;
import model.game.Hand;
import model.game.Rank;
import model.game.Revolver;
import model.events.GameEventPublisher;
import model.events.GameEventType;

public class UserImpl implements User {
  private final String username;
  private final String id;

  private boolean isAlive;

  private Hand hand;
  private Revolver revolver;
  private GameEventPublisher eventPublisher; // Optional for event publishing

  public UserImpl(String username) {
    this.username = username;
    this.id = UUID.randomUUID().toString();
    this.isAlive = true;
  }

  public UserImpl(String username, String id) { 
    this.username = username; 
    this.id = id;
    this.isAlive = true;
  }

  @Override
  public String getUserName() {
    return this.username;
  }

  @Override
  public String getName() {
    return this.username;
  }

  @Override
  public boolean equals(Object obj) { 
    if (this == obj) return true; // Same reference
    if (obj == null || getClass() != obj.getClass()) return false; // Type check

    User user = (UserImpl) obj;
    return this.id.equals(user.getId()) && this.username.equals(user.getUserName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.username); // Must be consistent with equals
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String toString() { 
    return username + " (" + id + ")";
  }

  @Override
  public Claim claim(Rank rank, int count, List<Card> droppedCards) {
    for (Card droppedCard: droppedCards) { 
      this.getHand().discard(droppedCard);
    }
    
    Claim claim = new ClaimImpl(count, this, droppedCards, rank);
    return claim;
  }

  @Override
  public void challengeClaim(Claim claim) {
    // Challenge logic handled by Round/Game classes
    // Player just initiates the challenge
  }

  @Override
  public boolean shoot() {
    if (eventPublisher != null) {
      eventPublisher.publishEvent(GameEventType.PLAYER_SHOT, this.username + " is spinning the revolver and pulling the trigger...");
    }
    
    boolean isBullet = this.revolver.shoot();
    this.isAlive = !isBullet;
    
    if (eventPublisher != null) {
      if (isBullet) {
        eventPublisher.publishEvent(GameEventType.PLAYER_ELIMINATED, "BANG! " + this.username + " is eliminated!");
      } else {
        eventPublisher.publishEvent(GameEventType.PLAYER_SHOT, "Click! " + this.username + " survives this round");
      }
    }
    
    return isBullet;
  }

  @Override
  public boolean isAlive() {
    return this.isAlive;
  }

  @Override
  public Hand getHand() {
    return this.hand;
  }

  @Override
  public void setHand(Hand hand) {
    this.hand = hand;
  }

  @Override
  public Revolver getRevolver() {
    return this.revolver;
  }

  @Override
  public void setRevolver(Revolver revolver) {
    this.revolver = revolver;
  }
  
  /**
   * Sets the event publisher for this user
   * @param eventPublisher The event publisher
   */
  public void setEventPublisher(GameEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }
}
