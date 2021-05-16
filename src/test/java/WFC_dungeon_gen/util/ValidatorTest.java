package WFC_dungeon_gen.util;

import WFC_dungeon_gen.dao.TileSetJsonDao;
import WFC_dungeon_gen.domain.TileSet;
import java.io.FileNotFoundException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the Validator-class.
 *
 * @author Juha Kauppinen
 */
public class ValidatorTest {

    private int[][] validMap;
    private int[][] invalidMap;
    private TileSet tileSet;

    public ValidatorTest() {
        this.validMap = new int[][]{
            {0, 0, 1, 2},
            {0, 0, 35, 3},
            {13, 8, 26, 0},
            {16, 10, 15, 0}
        };
        
        this.invalidMap = new int[][] {
            {13,22,25,22,2},
            {16,31,19,17,3},
            {0,38,24,15,0}
        };
        
        try {
            tileSet = new TileSetJsonDao().loadTileSet("data/dungeon_5x5.JSON");
        } catch (FileNotFoundException ex) {
            System.out.println("error loading data");
        }
    }
    
    /**
     * Tests the validation with valid data.
     */
    @Test
    public void testThatValidMapReturnsTrue() {
        Validator validator = new Validator(validMap, tileSet);
        validator.validateMap();
        Assert.assertEquals(true, validator.isValid());
    }
    
    /**
     * Tests the validation with invalid data.
     */
    @Test
    public void testThatInvalidMapReturnsFalse() {
        Validator validator = new Validator(invalidMap, tileSet);
        validator.validateMap();
        Assert.assertEquals(false, validator.isValid());
    }
}
