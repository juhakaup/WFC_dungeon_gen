package WFC_dungeon_gen.util;

import WFC_dungeon_gen.domain.Tile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for priority queue for tiles.
 * @author Juha Kauppinen
 */
public class TileQueueTest {
    
    private TileQueue tileQueue;
    
    @Before
    public void setUp() {
        this.tileQueue = new TileQueue(100);
    }

    /**
     * Test of add method, of class TileQueue.
     */
    @Test
    public void testAdd() {
        assertEquals(tileQueue.isEmpty(), true);
        
        int[] weights = new int[]{1,2,3};
        boolean[] available1 = new boolean[]{true, true, true};      
        Tile tile = new Tile(0.0, weights, 1, 2);     
        tile.setAvalableTiles(available1);
        this.tileQueue.add(tile);

        assertEquals(this.tileQueue.isEmpty(), false);
        
        Tile polledTile = this.tileQueue.poll();
        assertEquals(polledTile, tile);
    }

    /**
     * Test of poll method, of class TileQueue.
     */
    @Test
    public void testPoll() {
        int[] weights = new int[]{1,2,3};
        boolean[] available1 = new boolean[]{true, false, true};
        boolean[] available2 = new boolean[]{true, true, true};
        boolean[] available3 = new boolean[]{true, false, false};
        
        Tile tile1 = new Tile(0.0, weights, 1, 2);
        Tile tile2 = new Tile(0.0, weights, 1, 2);
        Tile tile3 = new Tile(0.0, weights, 1, 2);
        
        tile1.setAvalableTiles(available1);
        tile2.setAvalableTiles(available2);
        tile3.setAvalableTiles(available3);
        
        tileQueue.add(tile1);
        tileQueue.add(tile2);
        tileQueue.add(tile3);
        
        assertEquals(tileQueue.poll(), tile3);
        assertEquals(tileQueue.poll(), tile1);
        assertEquals(tileQueue.poll(), tile2);
    }

    /**
     * Test of isEmpty method, of class TileQueue.
     */
    @Test
    public void testIsEmpty() {
        assertEquals(this.tileQueue.isEmpty(), true);
        int[] weights = new int[]{1,2};
        boolean[] avail = new boolean[]{true, true};
        Tile tile = new Tile(0.0, weights, 1, 1);
        tile.setAvalableTiles(avail);
        this.tileQueue.add(tile);
        assertEquals(this.tileQueue.isEmpty(), false);
    }
    
}
