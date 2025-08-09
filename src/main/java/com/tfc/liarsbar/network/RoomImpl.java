package com.tfc.liarsbar.network;

import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.events.GameEventType;
import com.tfc.liarsbar.model.exceptions.RoomFullException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.tfc.liarsbar.model.game.GameConstants.DEFAULT_ROOM_CAPACITY;

public class RoomImpl implements Room {
  private final int CAPACITY = DEFAULT_ROOM_CAPACITY;
  private final String id;
  private Set<User> users;
  private GameEventPublisher eventPublisher; // Optional

  public RoomImpl(String id) {
    this.id = id;
    this.users = new CopyOnWriteArraySet<>();
  }
  
  public RoomImpl(String id, GameEventPublisher eventPublisher) {
    this.id = id;
    this.users = new CopyOnWriteArraySet<>();
    this.eventPublisher = eventPublisher;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public int getCapacity() {
    return this.CAPACITY;
  }

  @Override
  public void addUser(User user) {
    if (this.users.size() == this.CAPACITY) {
      throw new RoomFullException("Room is full!");
    }

    this.users.add(user);
    
    if (eventPublisher != null) {
      eventPublisher.publishEvent(GameEventType.ROOM_JOINED,
          "User " + user.getName() + " joined the room (" + this.users.size() + "/" + this.CAPACITY + ")");
    }
  }

  @Override
  public Set<User> getUsers() {
    return this.users;
  }

  @Override
  public void removeUser(User user) {
    this.users.remove(user);
  }
}
