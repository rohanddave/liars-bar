package util;

import util.InputHandler;
import util.TerminalUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Simple test runner for terminal input/output infrastructure.
 * Tests basic functionality without external dependencies.
 */
public class TestRunner {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running Terminal I/O Infrastructure Tests...");
        System.out.println("==========================================");
        
        testInputHandlerBasicFunctionality();
        testTerminalUtilsBasicFunctionality();
        
        System.out.println("\n==========================================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
        }
    }
    
    private static void testInputHandlerBasicFunctionality() {
        System.out.println("\nTesting InputHandler...");
        
        // Test menu choice validation
        runTest("Menu choice validation", () -> {
            String input = "2\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            InputHandler handler = new InputHandler();
            int result = handler.getMenuChoice(3);
            
            System.setOut(originalOut);
            handler.close();
            
            assert result == 2 : "Expected 2, got " + result;
        });
        
        // Test player name validation
        runTest("Player name validation", () -> {
            String input = "Alice\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            InputHandler handler = new InputHandler();
            String result = handler.getPlayerName();
            
            System.setOut(originalOut);
            handler.close();
            
            assert "Alice".equals(result) : "Expected 'Alice', got '" + result + "'";
        });
        
        // Test player count validation
        runTest("Player count validation", () -> {
            String input = "3\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            InputHandler handler = new InputHandler();
            int result = handler.getPlayerCount();
            
            System.setOut(originalOut);
            handler.close();
            
            assert result == 3 : "Expected 3, got " + result;
        });
        
        // Test challenge input
        runTest("Challenge input validation", () -> {
            String input = "y\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            InputHandler handler = new InputHandler();
            boolean result = handler.getChallenge();
            
            System.setOut(originalOut);
            handler.close();
            
            assert result : "Expected true for 'y' input";
        });
    }
    

    
    private static void testTerminalUtilsBasicFunctionality() {
        System.out.println("\nTesting TerminalUtils...");
        
        // Test separator printing
        runTest("Separator printing", () -> {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            TerminalUtils.printSeparator(10);
            
            String result = output.toString();
            System.setOut(originalOut);
            
            assert "==========\n".equals(result) : "Expected 10 equals signs with newline";
        });
        
        // Test text formatting
        runTest("Text formatting", () -> {
            String result = TerminalUtils.formatToWidth("Hello World", 15);
            assert "Hello World".equals(result) : "Text should not be truncated when within width";
            
            String truncated = TerminalUtils.formatToWidth("This is a very long text", 10);
            assert "This is...".equals(truncated) : "Text should be truncated with ellipsis";
        });
        
        // Test padding
        runTest("Text padding", () -> {
            String result = TerminalUtils.padToWidth("Hello", 10);
            assert "Hello     ".equals(result) : "Text should be padded to specified width";
        });
        
        // Test progress bar
        runTest("Progress bar creation", () -> {
            String full = TerminalUtils.createProgressBar(10, 10, 10);
            assert full.contains("██████████") : "Full progress bar should contain filled blocks";
            
            String half = TerminalUtils.createProgressBar(5, 10, 10);
            assert half.contains("█████░░░░░") : "Half progress bar should contain mix of filled and empty blocks";
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
}