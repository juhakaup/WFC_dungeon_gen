package WFC_dungeon_gen.util;

import java.util.Random;

/**
 *
 * @author Juha Kauppinen
 */
public class MyRandom {
    private Random random;
    
    public MyRandom(long seed) {
        this.random = new Random();
    }
    
    public double getNextEntropyNoiseValue() {
        return this.random.nextDouble()/10;
    }
    
    public int getNextIntInRange(int max) {
        return random.nextInt(max - 1) + 1;
    }
}
