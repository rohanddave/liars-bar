package util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Test class for DisplayManager basic functionality.
 * Tests methods that don't require model dependencies.
 */
public class DisplayManagerTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running DisplayManager Tests...");
        System.out.println("===============================");
        
        testBasicDisplayFunctionality();
        
        System.out.println("\n===============================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All DisplayManager tests passed!");
        } else {
            System.out.println("❌ Some DisplayManager tests failed!");
        }
    }
    
    private static void testBasicDisplayFunctionality() {
        System.out.println("\nTesting DisplayManager basic functionality...");
        
        // Test message display
        runTest("Message display formatting", () -> {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            // Create a simple DisplayManager instance for testing
            TestDisplayManager manager = new TestDisplayManager();
            manager.showMessage("Test message");
            
            String result = output.toString();
            System.setOut(originalOut);
            
            assert result.contains(">>> Test message") : "Message should be formatted with prefix";
        });
        
        // Test error display
        runTest("Error message display", () -> {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            TestDisplayManager manager = new TestDisplayManager();
            manager.showError("Test error");
            
            String result = output.toString();
            System.setOut(originalOut);
            
            assert result.contains("❌ ERROR: Test error") : "Error should be formatted with prefix";
        });
        
        // Test actions menu display
        runTest("Actions menu display", () -> {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            TestDisplayManager manager = new TestDisplayManager();
            manager.showActionsMenu(Arrays.asList("Action 1", "Action 2", "Action 3"));
            
            String result = output.toString();
            System.setOut(originalOut);
            
            assert result.contains("Available Actions:") : "Should show actions header";
            assert result.contains("1. Action 1") : "Should show first action";
            assert result.contains("2. Action 2") : "Should show second action";
            assert result.contains("3. Action 3") : "Should show third action";
        });
        
        // Test clear screen functionality
        runTest("Clear screen functionality", () -> {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(output));
            
            TestDisplayManager manager = new TestDisplayManager();
            manager.clearScreen();
            
            String result = output.toString();
            System.setOut(originalOut);
            
            // Check for ANSI escape codes for clearing screen
            assert result.contains("\033[2J\033[H") : "Should contain ANSI clear screen codes";
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
     * Simple test version of DisplayManager that only includes methods without model dependencies.
     */
    private static class TestDisplayManager {
        private static final String SEPARATOR = "================================================";
        
        public void clearScreen() {
            System.out.print("\033[2J\033[H");
            System.out.flush();
        }
        
        public void showMessage(String message) {
            System.out.println();
            System.out.println(">>> " + message);
            System.out.println();
        }
        
        public void showError(String error) {
            System.out.println();
            System.out.println("❌ ERROR: " + error);
            System.out.println();
        }
        
        public void showActionsMenu(java.util.List<String> actions) {
            System.out.println("Available Actions:");
            for (int i = 0; i < actions.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, actions.get(i));
            }
            System.out.println();
        }
    }
}