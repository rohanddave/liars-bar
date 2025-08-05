package model.network;

import java.util.ArrayList;

import model.exceptions.RoomFullException;

public class RoomImpl implements Room {
  private int capacity;
  private ArrayList<User> members;

  public RoomImpl(int n) {
    this.capacity = n;
    this.members = new ArrayList<>();
  }

  @Override
  public int getCapacity() {
    return this.capacity;
  }

  @Override
  public void addUser(User user) {
    if (this.members.size() == this.capacity) {
      throw new RoomFullException("Room is full!");
    }

    this.members.add(user);
  }

  @Override
  public ArrayList<User> getMembers() {
    return new ArrayList<User>(this.members);
  }
}
