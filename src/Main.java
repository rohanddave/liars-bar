import model.game.Game;
import model.game.GameImpl;
import model.game.Player;
import model.network.Room;
import model.network.RoomImpl;
import model.network.User;
import model.network.UserImpl;

public class Main {
  public static void main(String [] args) {
    // Creating users
    User rohan = new UserImpl("Rohan");
    User alan = new UserImpl("Alan");
    User kam = new UserImpl("Kamran");

    // Creating a room 
    Room room = new RoomImpl();

    // Adding users to room
    room.addUser(rohan);
    room.addUser(alan);
    room.addUser(kam);

    Game game = new GameImpl.Builder()
            .addPlayer(alan)
            .addPlayer(kam)
            .addPlayer(rohan)
            .build();

    // remove rohan
    game.removePlayer(rohan);

    // re-add rohan
    game.addPlayer(rohan);
    
    // starting game
    while(!game.isGameOver()) {
      Player current = game.getCurrentPlayer();
      System.out.println("Currently playing player: " + current.getId());
      
    }
  }
}
