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

public class UserImpl implements User {
  private final String username;
  private final String id;

  private boolean isAlive;

  private Hand hand;
  private Revolver revolver;

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
    
    return new ClaimImpl(count, this, droppedCards, rank);
  }

  @Override
  public void challengeClaim(Claim claim) {

  }

  @Override
  public boolean shoot() {
    boolean isBullet = this.revolver.shoot();
    this.isAlive = !isBullet;
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
}
