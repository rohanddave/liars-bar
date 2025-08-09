package model.network;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import model.game.Card;
import model.game.Claim;
import model.game.ClaimImpl;
import model.game.Hand;
import model.game.Rank;

public class UserImpl implements User {
  private final String username;
  private final String id;

  private Hand hand;

  public UserImpl(String username) {
    this.username = username;
    this.id = UUID.randomUUID().toString();
  }

  public UserImpl(String username, String id) { 
    this.username = username; 
    this.id = id;
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
    
    return new ClaimImpl(count, this);
  }

  @Override
  public void challengeClaim(Claim claim) {

  }

  @Override
  public boolean shoot() {
    return false;
  }

  @Override
  public boolean isAlive() {
    return false;
  }

  @Override
  public Hand getHand() {
    return this.hand;
  }

  @Override
  public void setHand(Hand hand) {
    this.hand = hand;
  }
}
