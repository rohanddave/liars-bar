package model.network;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.RoomFullException;

public class RoomImpl implements Room {
  private final int CAPACITY = 4;
  private List<User> members;

  public RoomImpl() {
    this.members = new ArrayList<>(this.CAPACITY);
    System.out.println("🏠 New room created with capacity: " + this.CAPACITY);
  }

  @Override
  public int getCapacity() {
    return this.CAPACITY;
  }

  @Override
  public void addUser(User user) {
    if (this.members.size() == this.CAPACITY) {
      System.out.println("❌ Failed to add user " + user.getName() + ": Room is full!");
      throw new RoomFullException("Room is full!");
    }

    this.members.add(user);
    System.out.println("✅ User " + user.getName() + " joined the room (" + this.members.size() + "/" + this.CAPACITY + ")");
  }

  @Override
  public List<User> getMembers() {
    return new ArrayList<User>(this.members);
  }
}
