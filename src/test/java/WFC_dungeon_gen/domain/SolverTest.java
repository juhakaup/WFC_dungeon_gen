package WFC_dungeon_gen.domain;

import WFC_dungeon_gen.dao.TileSetTestData;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Juha Kauppinen
 */
public class SolverTest {

    private Solver map;
    private int width;
    private int depth;
    private TileSet tileSet;

    public SolverTest() {
        this.depth = 5;
        this.width = 5;
        try {
            tileSet = new TileSetTestData().loadTileSet("hello");
        } catch (FileNotFoundException ex) {
            System.out.println("error loading data");
        }
    }

    @Before
    public void setUp() {
        this.map = new Solver(this.width, this.depth, this.tileSet, false);
    }

    @Test
    public void mapInitializesToRightDimension() {
        int[][] intMap = this.map.getMap();
        assertEquals(this.width, intMap[0].length);
        assertEquals(this.depth, intMap.length);
    }

    @Test
    public void mapIsInitializedToMinusOne() {
        this.map.initMap();
        int[][] intMap = this.map.getMap();
        for (int i = 0; i < this.depth; i++) {
            for (int j = 0; j < this.width; j++) {
                assertEquals(-1, intMap[i][j]);
            }
        }
    }

    /**
     * Initializes new map and does one step, then finds the tile that was collapsed.
     * Collapsed tile's positions in the array are summed up if they are random 
     * this sum should be close to half way.
     */
    @Test
    public void tilesAreSelectedRandomly() {
        int sum = 0;
        int cycles = 1000;

        for (int x = 0; x < cycles; x++) {
            map.initMap();
            map.step();
            int[][] intMap = map.getMap();

            for (int i = 0; i < depth; i++) {
                for (int j = 0; j < width; j++) {
                    if (intMap[i][j] != -1) {
                        int value = i * depth + j;
                        sum += value;
                    }
                }
            }
        }
        int average = sum / cycles;
        assertEquals(average, (width * depth) / 2, 2);
    }

}
