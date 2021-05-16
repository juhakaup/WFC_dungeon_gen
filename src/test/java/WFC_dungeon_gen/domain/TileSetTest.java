package WFC_dungeon_gen.domain;

import WFC_dungeon_gen.dao.TileSetDao;
import WFC_dungeon_gen.dao.TileSetTestData;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for TileSet-class.
 * @author Juha Kauppinen
 */
public class TileSetTest {
    private TileSet tileSet;
    
    public TileSetTest() {
        TileSetDao dao = new TileSetTestData();
        try {
            tileSet = dao.loadTileSet("nontrivial");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TileSetTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Before
    public void setUp() {
    }


    /**
     * Test of getTiles method, of class TileSet.
     */
    @Test
    public void testGetTiles() {
        String[][] tiles = tileSet.getTiles();
        String[] singleTile = new String[]{
            ".H.",
            ".H.",
            ".H."
        };
        
        assertArrayEquals(singleTile, tiles[0]);
    }

    /**
     * Test of getNumberOfTiles method, of class TileSet.
     */
    @Test
    public void testGetNumberOfTiles() {
        int result = tileSet.getNumberOfTiles();
        assertEquals(4, result);
    }

    /**
     * Test of getTileWeights method, of class TileSet.
     */
    @Test
    public void testGetTileWeights() {
        int[] expWeights = new int[]{1,2,3,4};
        int[] result = tileSet.getTileWeights();
        assertArrayEquals(expWeights, result);
    }

    /**
     * Test of getAdjacencyRules method, of class TileSet.
     */
    @Test
    public void testGetAdjacencyRules() {
        boolean[][][] rules = tileSet.getAdjacencyRules();
        
        assertEquals(4, rules.length);
    }
    
}
