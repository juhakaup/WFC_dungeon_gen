package WFC_dungeon_gen.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for Tile-class.
 * @author Juha Kauppinen
 */
public class TileTest {
    
    private Tile tile;
    private double noise;
    private int[] weights;
    
    @Before
    public void setUp() {
        this.noise = 0.1;
        this.weights = new int[]{10,10,5,5,25};
        this.tile = new Tile(noise, weights, 0, 0);
        tile.setAvalableTiles(new boolean[]{true,true,true});
    }

    /**
     * Test setting available tiles sets tile to collapsed
     */
    @Test
    public void settingAvailableTilesSetsCollapsedTrue() {
        // check that the tile is not collapsed
        assertEquals(false, tile.isCollapsed());
        
        tile.setAvalableTiles(new boolean[]{true, false, true});
        assertEquals(false, tile.isCollapsed());
        
        tile.setAvalableTiles(new boolean[]{true, false, false});
        assertEquals(true, tile.isCollapsed());
    }

    /**
     * Test available tiles returns correct tiles
     */
    @Test
    public void testGetAvailableTilesReturnsCorrectTiles() {
        boolean[] available = tile.getAvailableTiles();
        assertArrayEquals(new boolean[]{true, true, true}, available);
        
        boolean[] setTiles = new boolean[]{true, false, true};
        tile.setAvalableTiles(setTiles);
        available = tile.getAvailableTiles();
        assertEquals(setTiles, available);
    }
    
    /**
     * Tests that sum of possible weights returns correct values.
     */
    @Test
    public void testsumOfPossibleWeights() {
        tile.setAvalableTiles(new boolean[]{true, true, true});
        assertEquals(25, tile.getSumOfPossibleWeights());
        
        tile.setAvalableTiles(new boolean[]{false, true, true});
        assertEquals(15, tile.getSumOfPossibleWeights());
        
        tile.setAvalableTiles(new boolean[]{false, false, true});
        assertEquals(5, tile.getSumOfPossibleWeights());
    }
    
}
