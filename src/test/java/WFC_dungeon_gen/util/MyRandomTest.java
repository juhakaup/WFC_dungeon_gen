package WFC_dungeon_gen.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Juha Kauppinen
 */
public class MyRandomTest {
    
    /**
     * Tests that integers from rng are somewhat evenly distributed and not skewed.
     */
    @Test
    public void testIntegerRandomness() {
        int sum = 0;
        MyRandom myRandom = new MyRandom();
        
        int cycles = 100000;
        int maxValue = 100;
        int delta = 1;
        
        int[] buckets = new int[maxValue];
        
        for (int i=0; i<cycles; i++) {
            int randomNumber = myRandom.getNextIntInRange(maxValue);
            sum += randomNumber;
            buckets[randomNumber-1]++;
        }
        
        for (int count : buckets) {
            assertEquals(cycles/maxValue, count, cycles/maxValue*0.15);
        }
        
        assertEquals(maxValue/2, sum/cycles, delta);
    }
    
    /**
     * Tests that the entropy noise values are evenly distributed
     * and give about the expected value.
     */
    @Test
    public void testEntropyNoise() {
        double sum = 0;
        double absSum = 0;
        double delta = 0.01;
        MyRandom myRandom = new MyRandom();
        
        int cycles = 1000;
        for (int i=0; i<cycles; i++) {
            double noise = myRandom.getNextEntropyNoiseValue();
            sum += noise;
            absSum += Math.abs(noise);
        }
        
        assertEquals(0.0, sum/cycles, delta);
        assertEquals(0.1, absSum/cycles, delta);
    }
}
