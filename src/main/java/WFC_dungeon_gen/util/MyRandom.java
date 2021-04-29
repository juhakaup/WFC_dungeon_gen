package WFC_dungeon_gen.util;

import java.util.Random;

/**
 *
 * @author Juha Kauppinen
 */
public class MyRandom {
    private static final boolean USE_JAVA_RANDOM = false; // this is for comparison
    private final Random random;
    private long seed;
    
    public MyRandom() {
        if (USE_JAVA_RANDOM) {
            this.random = new Random();
        } else {
            this.random = null;
        }
        this.seed = System.nanoTime();
    }
    
    /**
     * Generates a small number to randomize selection when collapsing tiles.
     * @return double, generally around +- 0.1
     */
    public double getNextEntropyNoiseValue() {
        if (USE_JAVA_RANDOM) {
            return this.random.nextDouble()/10;
        }
        
        return nextLong() % 1000 / 5000.0;
    }
    
    /**
     * Xorshift random number generator.
     * @return random long number
     */
    private long nextLong() {
        seed ^= seed << 21;
        seed ^= seed >>> 35;
        seed ^= seed << 4;    
        return seed;
    }
    
    /**
     * Generates a random integer between one and given max value.
     * @param max largest possible number to generate
     * @return random integer value in range.
     */
    public int getNextIntInRange(int max) {
        if (USE_JAVA_RANDOM) {
            return random.nextInt(max - 1) + 1;
        }
        
        int n = (int) (nextLong() % (max)); 
        n = n < 0 ? -n : n;
        
        return n + 1;
    }
}
