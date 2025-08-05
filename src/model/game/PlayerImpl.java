package model.game;

public class PlayerImpl implements Player {
  private Revolver revolver;
  private boolean isAlive;
  private Hand hand;

  public PlayerImpl(Revolver revolver, Hand hand) {
    this.revolver = revolver;
    this.hand = hand;
    this.isAlive = false;
  }

  @Override
  public Claim claim() {
    return null;
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
    // TODO: return the copy of hand object.
    return this.hand;
  }

  @Override
  public void setHand(Hand hand) {
    this.hand = hand;
  }
}
