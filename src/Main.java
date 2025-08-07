import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.game.Card;
import model.game.Game;
import model.game.GameImpl;
import model.game.Player;
import model.network.Room;
import model.network.RoomImpl;
import model.network.User;
import model.network.UserImpl;

public class Main {
  public static void main(String[] args) {
    System.out.println("🎲 Welcome to Liar's Bar!");
    System.out.println("═══════════════════════════");
    
    // Creating users
    System.out.println("👥 Creating players...");
    User rohan = new UserImpl("Rohan");
    User alan = new UserImpl("Alan");
    User kam = new UserImpl("Kamran");

    // Creating a room
    System.out.println("\n🏠 Setting up game room...");
    Room room = new RoomImpl();

    // Adding users to room
    System.out.println("📝 Adding players to room...");
    room.addUser(rohan);
    room.addUser(alan);
    room.addUser(kam);

    System.out.println("\n🎮 Initializing game...");
    Game game = new GameImpl.Builder()
            .addPlayer(alan)
            .addPlayer(kam)
            .addPlayer(rohan)
            .build();

    game.startGame();
    System.out.println("\n🚀 Game started! Let the lying begin...\n");
    // starting game
    Scanner sc = new Scanner(System.in);
    do {
      Player current = game.getCurrentPlayer();
      System.out.println("\n" + "═".repeat(50));
      System.out.println("🎯 Current Player: " + current.getName() + " (" + current.getId() + ")");
      System.out.println("🎲 Current Round: " + game.getRank());
      printHand(current);
      System.out.println("👆 Choose your action:");
      System.out.println("1. Play Claim \t 2. Challenge \t 3. Shoot");
      System.out.print("Enter choice: ");
      int input = sc.nextInt();
      switch (input) {
        case 1:
          System.out.print("📊 Enter count of " + game.getRank() + "(s) to claim: ");
          int count = sc.nextInt();
          List<Card> discardedCards = new ArrayList<>(count);
          for (int i = 0; i < count; i++) {
            printHand(current);
            System.out.print("🃏 Enter card number to discard (" + (i + 1) + "/" + count + "): ");
            int cardIndex = sc.nextInt();
            discardedCards.add(current.getHand().getAt(cardIndex));
          }
          System.out.println("📝 Processing claim...");
          game.claim(current, count, discardedCards, game.getRank());
          game.moveToNextMove();
          break;
        case 2:
          System.out.println("⚔️ Challenging the last claim...");
          game.challengeClaim(current).shoot();
          game.moveToNextMove();
          break;
        case 3:
          System.out.println("🔫 Player chooses to shoot themselves...");
          current.shoot();
          game.moveToNextMove();
          break;
        default:
          System.out.println("❌ Invalid choice, please try again.");
          continue;
      }
    } while (!game.isGameOver());
    
    System.out.println("\n" + "═".repeat(50));
    System.out.println("🎉 GAME FINISHED! 🎉");
    System.out.println("Thanks for playing Liar's Bar!");
    sc.close();
  }

  private static void printHand(Player player) {
    System.out.println("🃏 " + player.getName() + "'s Hand:");
    System.out.println("╔════════════════════════════╗");
    System.out.println("║ " + player.getHand().toString() + " ║");
    System.out.println("╚════════════════════════════╝");
  }
}
