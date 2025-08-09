package util;

/**
 * Comprehensive test suite for terminal input/output infrastructure.
 * Runs all infrastructure tests and provides a summary.
 */
public class InfrastructureTestSuite {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("TERMINAL I/O INFRASTRUCTURE TEST SUITE");
        System.out.println("========================================");
        System.out.println();
        
        boolean allTestsPassed = true;
        
        // Run InputHandler and TerminalUtils tests
        System.out.println("1. Running Core Infrastructure Tests...");
        try {
            TestRunner.main(new String[]{});
        } catch (Exception e) {
            System.out.println("❌ Core infrastructure tests failed: " + e.getMessage());
            allTestsPassed = false;
        }
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Run DisplayManager tests
        System.out.println("2. Running DisplayManager Tests...");
        try {
            DisplayManagerTest.main(new String[]{});
        } catch (Exception e) {
            System.out.println("❌ DisplayManager tests failed: " + e.getMessage());
            allTestsPassed = false;
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINAL RESULTS");
        System.out.println("=".repeat(50));
        
        if (allTestsPassed) {
            System.out.println("🎉 ALL INFRASTRUCTURE TESTS PASSED! 🎉");
            System.out.println();
            System.out.println("✅ InputHandler: Input validation and user interaction");
            System.out.println("✅ TerminalUtils: Terminal formatting and utilities");
            System.out.println("✅ DisplayManager: Display formatting and output");
            System.out.println();
            System.out.println("The terminal input/output infrastructure is ready!");
        } else {
            System.out.println("❌ SOME TESTS FAILED");
            System.out.println("Please check the test output above for details.");
        }
        
        System.out.println("=".repeat(50));
    }
}