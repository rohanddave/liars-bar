package model.game;

import java.util.Random;

/**
 * Implementation of a revolver with 6 chambers and 1 bullet.
 * Tracks the current chamber position and bullet location.
 */
public class RevolverImpl implements Revolver {
    private static final int CHAMBER_COUNT = 6;
    private final Random random;
    private int bulletChamber;
    private int currentChamber;
    
    public RevolverImpl() {
        this.random = new Random();
        reset();
    }
    
    /**
     * Constructor for testing with specific random seed.
     * @param seed Random seed for predictable testing
     */
    public RevolverImpl(long seed) {
        this.random = new Random(seed);
        reset();
    }
    
    @Override
    public boolean shoot() {
        currentChamber = (currentChamber % CHAMBER_COUNT) + 1;
        return currentChamber == bulletChamber;
    }
    
    @Override
    public void reset() {
        // Place bullet in random chamber (1-6)
        bulletChamber = random.nextInt(CHAMBER_COUNT) + 1;
        currentChamber = 0; // Will be incremented to 1 on first shot
    }
    
    /**
     * Gets the current chamber position.
     * @return Current chamber position (1-6)
     */
    public int getCurrentChamber() {
        return currentChamber;
    }
    
    /**
     * Gets the bullet chamber position (for testing/display purposes).
     * @return Bullet chamber position (1-6)
     */
    public int getBulletChamber() {
        return bulletChamber;
    }
    
    /**
     * Sets the bullet chamber position (for testing purposes).
     * @param chamber Chamber position (1-6)
     */
    public void setBulletChamber(int chamber) {
        if (chamber < 1 || chamber > CHAMBER_COUNT) {
            throw new IllegalArgumentException("Chamber must be between 1 and " + CHAMBER_COUNT);
        }
        this.bulletChamber = chamber;
    }
    
    /**
     * Gets the number of chambers remaining before the bullet.
     * @return Number of safe shots remaining
     */
    public int getShotsUntilBullet() {
        if (currentChamber >= bulletChamber) {
            return (CHAMBER_COUNT - currentChamber) + bulletChamber;
        } else {
            return bulletChamber - currentChamber;
        }
    }
}
