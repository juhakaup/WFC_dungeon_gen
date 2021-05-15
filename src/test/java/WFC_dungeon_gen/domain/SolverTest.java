package WFC_dungeon_gen.domain;

import WFC_dungeon_gen.dao.TileSetJsonDao;
import WFC_dungeon_gen.dao.TileSetTestData;
import com.sun.prism.shader.Solid_Color_AlphaTest_Loader;
import java.io.FileNotFoundException;
import java.util.Random;
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
    private Random random;

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
        this.random = new Random();
    }

    /**
     * Check that the map is initialized to correct size and all tiles are
     * initialized to -1
     */
    @Test
    public void mapInitializesToRightDimensionAndValue() {
        int maxSize = 20;
        int cycles = 100;
        int w = this.random.nextInt(maxSize);
        int d = this.random.nextInt(maxSize);
        
        for (int i=0; i<cycles; i++) {
            Solver solver = new Solver(d, w, tileSet, false);
            int[][] intMap = solver.getMap();
            assertEquals(w, intMap.length);
            assertEquals(d, intMap[0].length);
            
            for (int k = 0; k < d; k++) {
            for (int j = 0; j < w; j++) {
                assertEquals(-1, intMap[j][k]);
            }
        }
        }
    }

    /**
     * Initializes new map and does one step, then finds the tile that was
     * collapsed. Collapsed tile's positions in the array are summed up if they
     * are random this sum should be close to half way.
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

    @Test
    public void tilesAreCollapsedAccordingToWeights() {
        int cycles = 100000;
        int numTiles = tileSet.getNumberOfTiles();
        int[] weights = tileSet.getTileWeights();
        int[] buckets = new int[numTiles];

        Solver solver = new Solver(1, 1, tileSet, false);

        for (int i = 0; i < cycles; i++) {
            solver.initMap();
            solver.generateMap();
            buckets[solver.getMap()[0][0]]++;
        }

        int sumWeights = 0;
        for (int w : weights) {
            sumWeights += w;
        }

        for (int i = 0; i < numTiles; i++) {
            double actual = cycles / (double) buckets[i];
            double expected = sumWeights / (double) weights[i];
            System.out.println("expected " + expected + " actual " + actual);
            assertEquals(expected, actual, 3);
        }
    }

    @Test
    public void createBorderdWorksAsExpected() {
        TileSet set = null;
        try {
            set = new TileSetTestData().loadTileSet("trivial");
        } catch (FileNotFoundException ex) {
            System.out.println("error loading data");
        }

        Solver solver = new Solver(2, 2, this.tileSet, true);
        int[][] tiles = solver.getMap();

        // Tiles should be collapsed
        for (int[] ints : tiles) {
            for (int i : ints) {
                assertEquals(6, i);
            }
        }

        // On a bigger map, tile inside the border is not collapsed
        solver = new Solver(3, 3, this.tileSet, true);
        tiles = solver.getMap();
        assertEquals(-1, tiles[1][1]);
        assertEquals(6, tiles[0][1]);
        assertEquals(6, tiles[1][0]);
    }

    /** 
     * Test integrity of generated map.
     */
    @Test
    public void generatedMazeIntegrityTest() {
        TileSet tiles = null;
        try {
            tiles = new TileSetJsonDao().loadTileSet("data/dungeon_no_rooms.JSON");
        } catch (FileNotFoundException ex) {
            System.out.println("error loading data");
        }

        int cycles = 1000;
        int maxSize = 15;

        for (int i = 0; i < cycles; i++) {
            int len = this.random.nextInt(maxSize - 2) + 2;
            int dep = this.random.nextInt(maxSize - 2) + 2;

            Solver solver = new Solver(len, dep, tiles, true);
            solver.initMap();
            int[][] newMap = solver.generateMap();

            boolean[][] connections = tiles.getConnections();
            Type[] types = tiles.getTileTypes();
            
            // checks each tile, if a direction that is available, ie. not a wall,
            // leads to a empty tile, then we have an integrity problem.
            for (int row = 0; row < newMap.length; row++) {
                for (int col = 0; col < newMap[0].length; col++) {
                    boolean[] availableDirections = connections[newMap[row][col]];
                    Type tileType = types[newMap[row][col]];
                    
                    for (Direction dir : Direction.values()) {
                        int adjacentRow = row + dir.vectY;
                        int adjacentCol = col + dir.vectX;
                        
                        if (tileType != Type.EMPTY && availableDirections[dir.value] && (adjacentRow >= 0 && adjacentRow < dep) && (adjacentCol >= 0 && adjacentCol < len)) {
                            Type adjacentType = types[newMap[adjacentRow][adjacentCol]];
                            assertNotEquals(tileType, Type.EMPTY);
                        }
                    }
                }
            }
        }
    }

}
