package com.tfc.liarsbar.network;

import com.tfc.liarsbar.model.exceptions.RoomFullException;

import java.util.List;
import java.util.Set;


/**
 * Interface represents a room of a max of n Users playing the Game Liars Bar.
 */
public interface Room {
  String getId();
  /**
   * Getter for capacity.
   *
   * @return integer representing the max number of Users allowed in the Room.
   */
  int getCapacity();

  /**
   * Adds a User to the Room.
   * @param user User to be added to the room.
   * @throws RoomFullException if room is at capcity.
   */
  void addUser(User user) throws RoomFullException;

  void removeUser(User user);
  /**
   * Getter for the members of the Room.
   * @return a copy of the array list of Members in the Room.
   */
  Set<User> getUsers();
}