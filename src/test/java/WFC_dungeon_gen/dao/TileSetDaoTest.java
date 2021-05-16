package WFC_dungeon_gen.dao;

import WFC_dungeon_gen.domain.TileSet;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for DAO classes.
 * @author Juha Kauppinen
 */
public class TileSetDaoTest {

    /**
     * Test loading tile set from json file.
     */
    @Test
    public void testLoadTileSet() {
        String file = "nothing";
        TileSetJsonDao dao = new TileSetJsonDao();
        TileSet set = null;
        try {
            set = dao.loadTileSet(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TileSetDaoTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(null, set);
        
        file = "./data/dungeon_basic.JSON";
        try {
            set = dao.loadTileSet(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TileSetDaoTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals(TileSet.class, set.getClass());
    }
    
    /**
     * Test loading test data
     */
    @Test
    public void testLoadingTestData() {
        TileSetTestData testDao = new TileSetTestData();
        TileSet set = null;
        try {
            set = testDao.loadTileSet("hello");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TileSetDaoTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(TileSet.class, set.getClass());
    }
    
}
