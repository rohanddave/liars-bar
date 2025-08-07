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
import model.events.GameEventPublisher;
import view.ConsoleGameEventListener;
import model.actions.ActionFactory;
import model.actions.GameAction;
import model.actions.ActionResult;

public class Main {
  public static void main(String[] args) {
    System.out.println("🎲 Welcome to Liar's Bar!");
    System.out.println("═══════════════════════════");
    System.out.println("📝 Note: For multiplayer WebSocket mode, run:");
    System.out.println("   java server.GameServer");
    System.out.println("   Then open client/index.html in your browser");
    System.out.println("═══════════════════════════");
    
    // Set up event system
    GameEventPublisher eventPublisher = new GameEventPublisher();
    ConsoleGameEventListener consoleListener = new ConsoleGameEventListener();
    eventPublisher.addListener(consoleListener);
    
    // Creating users
    System.out.println("👥 Creating players...");
    User rohan = new UserImpl("Rohan");
    User alan = new UserImpl("Alan");
    User kam = new UserImpl("Kamran");
    
    // Set event publishers for users
    rohan.setEventPublisher(eventPublisher);
    alan.setEventPublisher(eventPublisher);
    kam.setEventPublisher(eventPublisher);

    // Creating a room
    System.out.println("\n🏠 Setting up game room...");
    Room room = new RoomImpl(eventPublisher);

    // Adding users to room
    System.out.println("📝 Adding players to room...");
    room.addUser(rohan);
    room.addUser(alan);
    room.addUser(kam);

    System.out.println("\n🎮 Initializing game...");
    GameImpl game = (GameImpl) new GameImpl.Builder()
            .addPlayer(alan)
            .addPlayer(kam)
            .addPlayer(rohan)
            .withEventPublisher(eventPublisher)
            .build();

    game.startGame();
    System.out.println("\n🚀 Game started! Let the lying begin...\n");
    
    // starting game with strategy pattern
    try (Scanner sc = new Scanner(System.in)) {
      ActionFactory actionFactory = new ActionFactory(sc);
      
      do {
        Player current = game.getCurrentPlayer();
        System.out.println("\n" + "═".repeat(50));
        System.out.println("🎯 Current Player: " + current.getName() + " (" + current.getId() + ")");
        System.out.println("🎲 Current Round: " + game.getRank());
        printHand(current);
        
        List<GameAction> availableActions = actionFactory.getAvailableActions(game, current);
        
        if (availableActions.isEmpty()) {
          System.out.println("⚠️ No valid actions available for this player.");
          break;
        }
        
        System.out.println("👆 Choose your action:");
        for (int i = 0; i < availableActions.size(); i++) {
          System.out.println((i + 1) + ". " + availableActions.get(i).getActionName());
        }
        System.out.print("Enter choice (1-" + availableActions.size() + "): ");
        
        int choice = sc.nextInt();
        if (choice < 1 || choice > availableActions.size()) {
          System.out.println("❌ Invalid choice, please try again.");
          continue;
        }
        
        GameAction selectedAction = availableActions.get(choice - 1);
        ActionResult result = selectedAction.execute(game, current);
        
        if (!result.isSuccess()) {
          System.out.println("❌ " + result.getMessage());
        }
        
      } while (!game.isGameOver());
    }
    
    System.out.println("\n" + "═".repeat(50));
    System.out.println("🎉 GAME FINISHED! 🎉");
    System.out.println("Thanks for playing Liar's Bar!");
  }

  private static void printHand(Player player) {
    System.out.println("🃏 " + player.getName() + "'s Hand:");
    System.out.println("╔════════════════════════════╗");
    System.out.println("║ " + player.getHand().toString() + " ║");
    System.out.println("╚════════════════════════════╝");
  }
}
