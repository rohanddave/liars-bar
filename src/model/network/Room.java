package model.network;

import java.util.ArrayList;

import model.exceptions.RoomFullException;

/**
 * Interface represents a room of a max of n Users playing the Game Liars Bar.
 */
public interface Room {
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

  /**
   * Getter for the members of the Room.
   * @return a copy of the array list of Members in the Room.
   */
  ArrayList<User> getMembers();
}
