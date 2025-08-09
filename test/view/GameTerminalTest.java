package view;

import controller.GameController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test class for GameTerminal functionality.
 * Tests menu navigation, user flow, and game state display.
 */
public class GameTerminalTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running GameTerminal Tests...");
        System.out.println("=============================");
        
        testGameTerminalBasicFunctionality();
        testGameTerminalMessageDisplay();
        testGameTerminalGameStateHandling();
        
        System.out.println("\n=============================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All GameTerminal tests passed!");
        } else {
            System.out.println("❌ Some GameTerminal tests failed!");
        }
    }
    
    private static void testGameTerminalBasicFunctionality() {
        System.out.println("\nTesting GameTerminal basic functionality...");
        
        runTest("GameTerminal initialization", () -> {
            GameTerminal gameTerminal = new GameTerminal();
            assert gameTerminal != null : "GameTerminal should be initialized";
            assert !gameTerminal.isGameRunning() : "Game should not be running initially";
            gameTerminal.cleanup();
        });
        
        runTest("Controller setting", () -> {
            GameTerminal gameTerminal = new GameTerminal();
            TestGameController controller = new TestGameController();
            gameTerminal.setController(controller);
            // Controller is set internally, no direct way to verify
            // but we can test that it doesn't throw an exception
            gameTerminal.cleanup();
        });
        
        runTest("Cleanup functionality", () -> {
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.cleanup();
            assert !gameTerminal.isGameRunning() : "Game should not be running after cleanup";
        });
    }
    
    private static void testGameTerminalMessageDisplay() {
        System.out.println("\nTesting GameTerminal message display...");
        
        runTest("Show message functionality", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            String testMessage = "Test message";
            gameTerminal.showMessage(testMessage);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains(testMessage) : "Output should contain the test message";
            gameTerminal.cleanup();
        });
        
        runTest("Show error functionality", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            String testError = "Test error";
            gameTerminal.showError(testError);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains(testError) : "Output should contain the error message";
            assert output.contains("ERROR") : "Output should contain ERROR indicator";
            gameTerminal.cleanup();
        });
    }
    
    private static void testGameTerminalGameStateHandling() {
        System.out.println("\nTesting GameTerminal game state handling...");
        
        runTest("Display null game state message", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.displayGameStateMessage(null);
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("No game state available") : "Should show error for null game state";
            gameTerminal.cleanup();
        });
        
        runTest("Display valid game state message", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.displayGameStateMessage("Test game state");
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("Game State: Test game state") : "Should display game state message";
            gameTerminal.cleanup();
        });
        
        runTest("Handle player input without active game", () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            GameTerminal gameTerminal = new GameTerminal();
            gameTerminal.handlePlayerInput();
            
            String output = outputStream.toString();
            System.setOut(originalOut);
            
            assert output.contains("No active game session") : "Should show error for no active game";
            gameTerminal.cleanup();
        });
    }
    
    private static void runTest(String testName, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("  ✅ " + testName);
        } catch (Exception | AssertionError e) {
            System.out.println("  ❌ " + testName + ": " + e.getMessage());
        }
    }
    
    /**
     * Test implementation of GameController for testing purposes.
     */
    private static class TestGameController implements GameController {
        // Empty implementation for testing
    }
}