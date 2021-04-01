package WFC_dungeon_gen;

import WFC_dungeon_gen.domain.Solver;
import WFC_dungeon_gen.domain.TileSet;

/**
 *
 * @author Juha Kauppinen
 */
public class Main {
    public static void main(String[] args) {
        // tile frequencies  
        TileSet ts = new TileSet();
        int width = 24;
        int depth = 8;
        int numberOfTiles = ts.getNumberOfTiles();
        int[] tileWeights = ts.getTileWeights();
        boolean[][][] adjacencyRules = ts.getAdjacencyRules();
        
        Solver dungeon = new Solver(width, depth, numberOfTiles, tileWeights, adjacencyRules);
    }
    
}
