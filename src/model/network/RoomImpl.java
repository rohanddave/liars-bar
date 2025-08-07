package model.network;

import java.util.ArrayList;
import java.util.List;

import model.exceptions.RoomFullException;
import model.events.GameEventPublisher;
import model.events.GameEventType;
import static model.game.GameConstants.*;

public class RoomImpl implements Room {
  private final int CAPACITY = DEFAULT_ROOM_CAPACITY;
  private List<User> members;
  private GameEventPublisher eventPublisher; // Optional

  public RoomImpl() {
    this.members = new ArrayList<>(this.CAPACITY);
  }
  
  public RoomImpl(GameEventPublisher eventPublisher) {
    this.members = new ArrayList<>(this.CAPACITY);
    this.eventPublisher = eventPublisher;
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
    
    if (eventPublisher != null) {
      eventPublisher.publishEvent(GameEventType.ROOM_JOINED, 
          "User " + user.getName() + " joined the room (" + this.members.size() + "/" + this.CAPACITY + ")");
    }
  }

  @Override
  public List<User> getMembers() {
    return new ArrayList<User>(this.members);
  }
}
