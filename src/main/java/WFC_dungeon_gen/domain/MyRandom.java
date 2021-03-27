package WFC_dungeon_gen.domain;

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
        return this.random.nextDouble();
    }
    
    public int getNextIntInRange(int max) {
        return random.nextInt(max - 1) + 1;
    }
}
