package model.network;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.RoomFullException;
import model.game.GameImpl;
import model.game.Player;

public class RoomImpl implements Room {
  private final int CAPACITY = 4;
  private List<User> members;

  public RoomImpl() {
    this.members = new ArrayList<>(this.CAPACITY);
  }

  @Override
  public int getCapacity() {
    return this.CAPACITY;
  }

  @Override
  public void addUser(User user) {
    if (this.members.size() == this.CAPACITY) {
      throw new RoomFullException("Room is full!");
    }

    this.members.add(user);
  }

  @Override
  public List<User> getMembers() {
    return new ArrayList<User>(this.members);
  }
}
