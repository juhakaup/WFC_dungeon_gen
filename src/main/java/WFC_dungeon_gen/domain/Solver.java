package WFC_dungeon_gen.domain;

import static WFC_dungeon_gen.domain.Direction.*;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 *
 * @author Juha Kauppinen
 */
public class Solver {
    private final Tile[][] dungeonMap;
    private final int numPossibleTiles;
    private int numCollapsedTiles;
    private final int[] tileWeights;
    private final boolean[][][] adjacencyRules;
    private final int width;
    private final int depth;
    private final MyRandom random;
    private final PriorityQueue<TileParameters> entropyQueue;
    private PriorityQueue<TileParameters> propagatorQueue;
    
    public Solver(int width, int depth, int numTiles, int[] tileWeights, boolean[][][] adjacencyRules) {
        this.numPossibleTiles = numTiles;
        this.numCollapsedTiles = 0;
        this.tileWeights = tileWeights;
        this.adjacencyRules = adjacencyRules;
        this.width = width;
        this.depth = depth;
        this.random = new MyRandom(12346);
        this.entropyQueue = new PriorityQueue<>(width*depth);
        this.propagatorQueue = new PriorityQueue<>(width*depth);
        this.dungeonMap = initializeMap();
        
        while (true) {
            Tile nextTile = chooseNextTile(this.entropyQueue);
            if (nextTile == null) {
                break;
            }
            collapseTile(nextTile);
            propagate(nextTile);
            
            while(true) {
                nextTile = chooseNextTile(this.propagatorQueue);
                if (nextTile == null) {
                    break;
                }
                propagate(nextTile);
            }
        }
        printMaze();
    }
    
    /**
     * Initializes the dungeon map with tiles
     * @return 2d array of Tiles
     */
    private Tile[][] initializeMap() {
        boolean[] initialTiles = createPosBooleanArray(this.numPossibleTiles);
        Tile[][] tiles = new Tile[this.depth][this.width];
        
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.depth; row++) {
                double noise = this.random.getNextEntropyNoiseValue();
                Tile tile = new Tile(noise, this.tileWeights, row, col);
                tile.setAvalableTiles(initialTiles);
                entropyQueue.add(new TileParameters(row, col, tile.getEntropy()));
                tiles[row][col] = tile;
            }
        }
        return tiles;
    }
    
    /**
     * Creates a boolean array with all values set to true
     * @param size size of the array
     * @return boolean array
     */
    private boolean[] createPosBooleanArray(int size) {
        boolean[] tiles = new boolean[size];
        for (int i=0; i<size; i++){
            tiles[i] = true;
        }
        return tiles;
    }
    
    /**
     * Randomly sets the outcome of the tile to one of its possibilities
     * based on weighted distribution of tiles
     * @param tileParam parameters containing coordinates for the tile
     */
    private void collapseTile(Tile tile) {
        //Tile tile = dungeonMap[tileParam.getRow()][tileParam.getCol()];
        
        int sumWeight = tile.getSumOfPossibleWeights();
        int randomInt = random.getNextIntInRange(sumWeight);
        
        boolean[] availableTiles = tile.getAvailableTiles();
        int chosenTile = 0;
        while(randomInt > 0) {
            for (; chosenTile<availableTiles.length; chosenTile++) {
                if(availableTiles[chosenTile]) {
                    randomInt -= this.tileWeights[chosenTile];
                }
                if(randomInt <= 0) {
                    break;
                }
            }
        }
        boolean[] availableAfterCollapse = new boolean[this.numPossibleTiles];
        availableAfterCollapse[chosenTile] = true;
        
        tile.setAvalableTiles(availableAfterCollapse);
        this.propagatorQueue.add(new TileParameters(tile.getRow(), tile.getCol(), tile.getEntropy()));
        this.numCollapsedTiles += 1;
    }
    
    private void propagate(Tile propagator) {
        int row = propagator.getRow();
        int col = propagator.getCol();
        boolean[] propagatorTiles = propagator.getAvailableTiles();

        //System.out.println("propagating " + row + ", " + col);
        
        if (row-1 > 0) {
            Tile up = dungeonMap[row-1][col];
            
            boolean[] availableTiles = Arrays.copyOf(up.getAvailableTiles(), numPossibleTiles);
            boolean[] possibleTiles = getAvailableTilesFromRules(propagatorTiles, UP);
            
            availableTiles = removeNotAvailable(availableTiles, possibleTiles);
            
            if (up.setAvalableTiles(availableTiles)) {
                if (!up.isCollapsed()) {
                    TileParameters param = new TileParameters(row-1, col, up.getEntropy());
                    this.propagatorQueue.add(param);
                    this.entropyQueue.add(param);
                }
            }
        }
        if (row+1 < this.depth) {
            Tile down = dungeonMap[row+1][col];
            
            boolean[] availableTiles = Arrays.copyOf(down.getAvailableTiles(), numPossibleTiles);
            boolean[] possibleTiles = getAvailableTilesFromRules(propagatorTiles, DOWN);
            
            availableTiles = removeNotAvailable(availableTiles, possibleTiles);
            
            if (down.setAvalableTiles(availableTiles)) {
                TileParameters param = new TileParameters(row+1, col, down.getEntropy());
                this.propagatorQueue.add(param);
                this.entropyQueue.add(param);
            }
        }
        if (col-1 > 0) {
            Tile left = dungeonMap[row][col-1];
            
            boolean[] availableTiles = Arrays.copyOf(left.getAvailableTiles(), numPossibleTiles);
            boolean[] possibleTiles = getAvailableTilesFromRules(propagatorTiles, LEFT);
            
            availableTiles = removeNotAvailable(availableTiles, possibleTiles);
            
            if (left.setAvalableTiles(availableTiles)) {
                TileParameters param = new TileParameters(row, col-1, left.getEntropy());
                this.propagatorQueue.add(param);
                this.entropyQueue.add(param);
            }
        }
        if (col+1 < this.width) {
            Tile right = dungeonMap[row][col+1];
            
            boolean[] availableTiles = Arrays.copyOf(right.getAvailableTiles(), numPossibleTiles);
            boolean[] possibleTiles = getAvailableTilesFromRules(propagatorTiles, RIGHT);
            
            availableTiles = removeNotAvailable(availableTiles, possibleTiles);
            
            if (right.setAvalableTiles(availableTiles)) {
                TileParameters param = new TileParameters(row, col+1, right.getEntropy());
                this.propagatorQueue.add(param);
                this.entropyQueue.add(param);
            }
        }
       
    }
    
    private boolean[] andTwoBooleanArrays(boolean[] boolA, boolean[] boolB) {
        boolean[] newBool = new boolean[boolA.length];
        for (int i=0; i<boolA.length; i++) {
            newBool[i] = (boolA[i] && boolB[i]);
        }
        return newBool;
    }
    
    private boolean[] orTwoBooleanArrays(boolean[] boolA, boolean[] boolB) {
        boolean[] newBool = new boolean[boolA.length]; 
        for (int i=0; i<boolA.length; i++) {
            newBool[i] = (boolA[i] || boolB[i]);
        }
        return newBool;
    }
    
    private boolean[] removeNotAvailable(boolean[] boolA, boolean[] boolB) {
        for (int i=0; i<boolA.length; i++) {
            if (!boolB[i]) {
                boolA[i] = false;
            }
        }
        return boolA;
    }
    
    private boolean[] getAvailableTilesFromRules(boolean[] tiles, Direction dir) {
        boolean[] newBool = new boolean[numPossibleTiles];
        for (int i=0; i<numPossibleTiles; i++) {
            if (tiles[i]) {
                boolean[] possibleTiles = this.adjacencyRules[dir.value][i];
                newBool = orTwoBooleanArrays(newBool, possibleTiles);
            }
        }
        return newBool;
    }
    
    
    /**
     * Returns the next uncollapsed tile with the lowest entropy value
     * @return TileParameters containing coordinates
     */
    private Tile chooseNextTile(PriorityQueue<TileParameters> queue) {
        while(true) {
            if (!queue.isEmpty()) {
                TileParameters params = queue.poll();
                Tile tile = dungeonMap[params.getRow()][params.getCol()];
                if (!tile.isCollapsed()) {
                    return tile;
                }
            }
            else {
                break;
            }
        }
        return null;
    }
    
    public void printMaze() {
        for (int y = 0; y < this.depth; y++) {
            for (int x = 0; x < this.width; x++) {
                System.out.print(dungeonMap[y][x]);
            }
            System.out.println("");
        }
    }
}
