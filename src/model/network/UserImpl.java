package model.network;

public class UserImpl implements User {
  private String username;

  public UserImpl(String username) {
    this.username = username;
  }

  @Override
  public String getUserName() {
    return this.username;
  }

  @Override
  public void setUserName(String username) {
    this.username = username;
  }
}
