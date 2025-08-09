package com.tfc.liarsbar.network;

import com.tfc.liarsbar.model.events.GameEventPublisher;
import com.tfc.liarsbar.model.events.GameEventType;
import com.tfc.liarsbar.model.exceptions.RoomFullException;
import com.tfc.liarsbar.model.game.Game;
import com.tfc.liarsbar.model.game.GameImpl;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.tfc.liarsbar.model.game.GameConstants.DEFAULT_ROOM_CAPACITY;

public class RoomImpl implements Room {
  private final int CAPACITY = DEFAULT_ROOM_CAPACITY;
  private final String id;
  private Set<User> users;
  private GameEventPublisher eventPublisher; // Optional
  private Game game;

  public RoomImpl(String id, GameEventPublisher eventPublisher) {
    this.id = id;
    this.users = new CopyOnWriteArraySet<>();
    this.eventPublisher = eventPublisher;
    this.game = new GameImpl
            .Builder()
            .withEventPublisher(eventPublisher)
            .build();
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
    this.game.addPlayer(user);
    
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
  public GameEventPublisher getGameEventPublisher() {
    return this.eventPublisher;
  }

  @Override
  public Game getGame() {
    return this.game;
  }

  @Override
  public void removeUser(User user) {
    this.users.remove(user);
  }
}
