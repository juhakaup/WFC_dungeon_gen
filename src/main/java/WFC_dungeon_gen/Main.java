package WFC_dungeon_gen;

import WFC_dungeon_gen.dao.TileSetDao;
import WFC_dungeon_gen.dao.TileSetJsonDao;
import WFC_dungeon_gen.dao.TileSetTestData;
import WFC_dungeon_gen.domain.Solver;
import WFC_dungeon_gen.domain.TileSet;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Juha Kauppinen
 */
public class Main {

    public static void main(String[] args) {
        TileSet tileSet = null;
        TileSetDao tileSetDao = new TileSetTestData();
        //TileSetDao tileSetDao = new TileSetJsonDao();
        try {
            //tileSet = tileSetDao.loadTileSet("./data/dungeon_trivial.JSON");
            tileSet = tileSetDao.loadTileSet("trivial");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int width = 20;
        int depth = 10;
        int numberOfTiles = tileSet.getNumberOfTiles();
        int[] tileWeights = tileSet.getTileWeights();
        boolean[][][] adjacencyRules = tileSet.getAdjacencyRules();

        Solver dungeon = new Solver(width, depth, numberOfTiles, tileWeights, adjacencyRules);
        int[][] map = dungeon.solveMaze();

        // print tile indexes
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                System.out.print(map[row][col]);
            }
            System.out.println("");
        }
        
        //print tiles as characters
        //String tiles = "│─┐└┘┌ █┼"; // utf-8 characters
        String tiles = "|-++++ #+";   // characters for windows
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                System.out.print(String.valueOf(tiles.charAt(map[row][col])));
            }
            System.out.println("");
        }
    }

}
