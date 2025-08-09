package model.game;

/**
 * Test class for RevolverImpl.
 * Tests revolver mechanics, chamber tracking, and bullet placement.
 */
public class RevolverImplTest {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running RevolverImpl Tests...");
        System.out.println("=============================");
        
        testRevolverInitialization();
        testShootAdvancesChamber();
        testShootCyclesThroughChambers();
        testShootReturnsTrueWhenBulletHit();
        testShootReturnsFalseWhenNoBullet();
        testReset();
        testSetBulletChamber();
        testSetBulletChamberInvalidValues();
        testGetShotsUntilBullet();
        testGetShotsUntilBulletWraparound();
        testMultipleResets();
        testRevolverWithDifferentSeeds();
        testFullRevolverCycle();
        testRevolverStateConsistency();
        
        System.out.println("\n=============================");
        System.out.printf("Tests completed: %d/%d passed%n", testsPassed, testsRun);
        
        if (testsPassed == testsRun) {
            System.out.println("✅ All tests passed!");
        } else {
            System.out.println("❌ Some tests failed!");
        }
    }
    
    private static void testRevolverInitialization() {
        runTest("Revolver initialization", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            
            // Bullet should be placed in a chamber between 1-6
            int bulletChamber = revolver.getBulletChamber();
            assert bulletChamber >= 1 && bulletChamber <= 6 : 
                "Bullet chamber should be between 1 and 6, was: " + bulletChamber;
            
            // Current chamber should start at 0
            assert revolver.getCurrentChamber() == 0 : "Current chamber should start at 0";
        });
    }
    
    private static void testShootAdvancesChamber() {
        runTest("Shoot advances chamber", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            int initialChamber = revolver.getCurrentChamber();
            
            revolver.shoot();
            
            assert revolver.getCurrentChamber() == initialChamber + 1 : 
                "Chamber should advance by 1";
        });
    }
    
    private static void testShootCyclesThroughChambers() {
        runTest("Shoot cycles through chambers", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            
            // Shoot 6 times to cycle through all chambers
            for (int i = 1; i <= 6; i++) {
                revolver.shoot();
                assert revolver.getCurrentChamber() == i : 
                    "Chamber should be " + i + " but was " + revolver.getCurrentChamber();
            }
            
            // Next shot should cycle back to chamber 1
            revolver.shoot();
            assert revolver.getCurrentChamber() == 1 : "Should cycle back to chamber 1";
        });
    }
    
    private static void testShootReturnsTrueWhenBulletHit() {
        runTest("Shoot returns true when bullet hit", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            int bulletChamber = revolver.getBulletChamber();
            
            // Shoot until we reach the bullet chamber
            boolean hitBullet = false;
            for (int i = 1; i <= bulletChamber; i++) {
                boolean result = revolver.shoot();
                if (i == bulletChamber) {
                    assert result : "Should return true when hitting bullet chamber";
                    hitBullet = true;
                } else {
                    assert !result : "Should return false when not hitting bullet chamber";
                }
            }
            
            assert hitBullet : "Should have hit the bullet";
        });
    }
    
    private static void testShootReturnsFalseWhenNoBullet() {
        runTest("Shoot returns false when no bullet", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            int bulletChamber = revolver.getBulletChamber();
            
            // Shoot chambers before the bullet
            for (int i = 1; i < bulletChamber; i++) {
                boolean result = revolver.shoot();
                assert !result : "Should return false when not hitting bullet chamber " + i;
            }
        });
    }
    
    private static void testReset() {
        runTest("Reset functionality", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            
            // Shoot a few times
            revolver.shoot();
            revolver.shoot();
            
            revolver.reset();
            
            // Current chamber should reset to 0
            assert revolver.getCurrentChamber() == 0 : "Current chamber should reset to 0";
            
            // Bullet should be in a valid position
            int newBulletChamber = revolver.getBulletChamber();
            assert newBulletChamber >= 1 && newBulletChamber <= 6 : 
                "Bullet chamber should be between 1 and 6";
        });
    }
    
    private static void testSetBulletChamber() {
        runTest("Set bullet chamber", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            
            revolver.setBulletChamber(3);
            assert revolver.getBulletChamber() == 3 : "Bullet chamber should be set to 3";
            
            // Test that shooting chamber 3 returns true
            revolver.shoot(); // chamber 1
            revolver.shoot(); // chamber 2
            boolean result = revolver.shoot(); // chamber 3
            assert result : "Should hit bullet in chamber 3";
        });
    }
    
    private static void testSetBulletChamberInvalidValues() {
        runTest("Set bullet chamber invalid values", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            
            try {
                revolver.setBulletChamber(0);
                assert false : "Should throw exception for chamber 0";
            } catch (IllegalArgumentException e) {
                // Expected
            }
            
            try {
                revolver.setBulletChamber(7);
                assert false : "Should throw exception for chamber 7";
            } catch (IllegalArgumentException e) {
                // Expected
            }
            
            try {
                revolver.setBulletChamber(-1);
                assert false : "Should throw exception for chamber -1";
            } catch (IllegalArgumentException e) {
                // Expected
            }
        });
    }
    
    private static void testGetShotsUntilBullet() {
        runTest("Get shots until bullet", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            revolver.setBulletChamber(4);
            
            // Initially at chamber 0, bullet in chamber 4
            assert revolver.getShotsUntilBullet() == 4 : "Should be 4 shots until bullet";
            
            revolver.shoot(); // Now at chamber 1
            assert revolver.getShotsUntilBullet() == 3 : "Should be 3 shots until bullet";
            
            revolver.shoot(); // Now at chamber 2
            assert revolver.getShotsUntilBullet() == 2 : "Should be 2 shots until bullet";
            
            revolver.shoot(); // Now at chamber 3
            assert revolver.getShotsUntilBullet() == 1 : "Should be 1 shot until bullet";
        });
    }
    
    private static void testGetShotsUntilBulletWraparound() {
        runTest("Get shots until bullet wraparound", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            revolver.setBulletChamber(2);
            
            // Shoot past the bullet chamber
            for (int i = 0; i < 5; i++) {
                revolver.shoot();
            }
            // Now at chamber 5, bullet in chamber 2
            assert revolver.getShotsUntilBullet() == 3 : "Should be 3 shots until bullet (wraparound)";
        });
    }
    
    private static void testMultipleResets() {
        runTest("Multiple resets", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            
            for (int i = 0; i < 10; i++) {
                revolver.reset();
                int bulletChamber = revolver.getBulletChamber();
                assert bulletChamber >= 1 && bulletChamber <= 6 : 
                    "Bullet chamber should be valid after reset " + i;
                assert revolver.getCurrentChamber() == 0 : 
                    "Current chamber should be 0 after reset " + i;
            }
        });
    }
    
    private static void testRevolverWithDifferentSeeds() {
        runTest("Revolver with different seeds", () -> {
            RevolverImpl revolver1 = new RevolverImpl(1L);
            RevolverImpl revolver2 = new RevolverImpl(2L);
            
            int bullet1 = revolver1.getBulletChamber();
            int bullet2 = revolver2.getBulletChamber();
            
            assert bullet1 >= 1 && bullet1 <= 6 : "Revolver1 bullet chamber should be valid";
            assert bullet2 >= 1 && bullet2 <= 6 : "Revolver2 bullet chamber should be valid";
        });
    }
    
    private static void testFullRevolverCycle() {
        runTest("Full revolver cycle", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            revolver.setBulletChamber(6);
            
            // Shoot through all chambers
            boolean[] results = new boolean[6];
            for (int i = 0; i < 6; i++) {
                results[i] = revolver.shoot();
            }
            
            // Only the 6th shot should be true
            for (int i = 0; i < 5; i++) {
                assert !results[i] : "Shot " + (i + 1) + " should be false";
            }
            assert results[5] : "Shot 6 should be true (bullet chamber)";
        });
    }
    
    private static void testRevolverStateConsistency() {
        runTest("Revolver state consistency", () -> {
            RevolverImpl revolver = new RevolverImpl(12345L);
            int bulletChamber = revolver.getBulletChamber();
            
            // Shoot until we're one chamber before the bullet
            for (int i = 1; i < bulletChamber; i++) {
                revolver.shoot();
            }
            
            assert revolver.getCurrentChamber() == bulletChamber - 1 : 
                "Should be one chamber before bullet";
            assert revolver.getShotsUntilBullet() == 1 : 
                "Should be 1 shot until bullet";
            
            // Next shot should hit the bullet
            boolean result = revolver.shoot();
            assert result : "Should hit bullet";
            assert revolver.getCurrentChamber() == bulletChamber : 
                "Should be at bullet chamber";
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