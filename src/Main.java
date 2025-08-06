import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.game.Card;
import model.game.Claim;
import model.game.Game;
import model.game.GameImpl;
import model.game.Hand;
import model.game.Player;
import model.network.Room;
import model.network.RoomImpl;
import model.network.User;
import model.network.UserImpl;

public class Main {
  public static void main(String[] args) {
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

    // starting game
    while (!game.isGameOver()) {
      for (Player player : game.getActivePlayers()) {
        
      }
      Player current = game.getCurrentPlayer();
      System.out.println("Currently playing player: " + current.getId());
      Scanner sc = new Scanner(System.in);
      System.out.println("1. Play Claim \t 2. Challenge \t 3. Shoot");
      int input = sc.nextInt();
      switch (input) {
        case 1:
        System.out.println("Enter count");
        int count = sc.nextInt();
        List<Card> discardedCards = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
          Hand hand = current.getHand();
          System.out.println(hand.toString());
          System.out.println("Enter card number to discard");
          int cardIndex = sc.nextInt();
          discardedCards.add(current.getHand().getAt(cardIndex));
        }
        game.claim(current, count, discardedCards, game.getRank());
          break;
        case 2:
          game.challengeClaim(current);
          break;
        case 3:
          game.spinRevolver(current);
          current.shoot();
          break;
        default:
          continue;
      }
    }
  }
}
