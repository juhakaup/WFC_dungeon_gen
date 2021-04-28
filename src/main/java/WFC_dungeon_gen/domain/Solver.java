package WFC_dungeon_gen.domain;

import WFC_dungeon_gen.util.MyRandom;
import WFC_dungeon_gen.util.TileQueue;
import java.util.Arrays;

/**
 * Wave function collapse dungeon generator
 * @author Juha Kauppinen
 */
public class Solver {

    private Tile[][] dungeonMap;
    private final int numPossibleTiles;
    private final int[] tileWeights;
    private final boolean[][][] adjacencyRules;
    private final int width;
    private final int depth;
    private final MyRandom random;
    private TileQueue entropyQueue = null;
    private TileQueue propagatorQueue = null;
    private int numberOfRetries;
    private final boolean[][] borderTiles;
    private final boolean maintainBorders;

    public Solver(int width, int depth, TileSet tileSet, boolean maintainBorders) {
        this.numPossibleTiles = tileSet.getNumberOfTiles();
        this.tileWeights = tileSet.getTileWeights();
        this.adjacencyRules = tileSet.getAdjacencyRules();
        this.width = width;
        this.depth = depth;
        this.numberOfRetries = 0;
        this.maintainBorders = maintainBorders;
        this.random = new MyRandom(12346);
        this.dungeonMap = null;
        this.borderTiles = tileSet.getBorderTiles();
        initMap();
    }
    
    public final void initMap() {
//        this.entropyQueue = new TileQueue(this.width * this.depth);
        this.entropyQueue = new TileQueue(512);
        this.propagatorQueue = new TileQueue(512);
        this.dungeonMap = initializeMap();
        if (maintainBorders) {
            addBorder();
        }
    }

    public int[][] generateMap() {     
        while (true) {
            Tile nextTile = selectNextTile(this.entropyQueue, true);
            if (nextTile == null) {
                break;
            }
            collapseTile(nextTile);
            propagate(nextTile);
            
            while (true) {
                nextTile = selectNextTile(this.propagatorQueue, false);
                if (nextTile == null) {
                    break;
                }
                propagate(nextTile);
            }
        }
        this.numberOfRetries = 0;
        return convertToTileIds(this.dungeonMap);
    }
    
    public void step() {
        Tile nextTile = selectNextTile(this.entropyQueue, true);
        if (nextTile != null) {   
            collapseTile(nextTile);
            propagate(nextTile);
        }
        while (true) {
            nextTile = selectNextTile(this.propagatorQueue, false);
            if (nextTile == null) {
                break;
            }
            propagate(nextTile);
        }
    }
    
    public int[][] getMap() {
        return convertToTileIds(dungeonMap);
    }
    
    public int getNumOfRetries() {
        return  this.numberOfRetries;
    }

    /**
     * Converts given Tile array to integer array based on the final value of tile.
     * @return 2d integer array
     */
    private int[][] convertToTileIds(Tile[][] map) {
        int[][] intMap = new int[this.depth][this.width];
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.depth; row++) {
                intMap[row][col] = map[row][col].getFinalValue();
            }
        }
        return intMap;
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
                entropyQueue.add(tile);
                tiles[row][col] = tile;
            }
        }
        return tiles;
    }
    
    /**
     * Adds a border around the map, this is to contain the dungeon within the map
     */
    public void addBorder() {    
        for (int i=1; i<this.width-1; i++) {
            dungeonMap[0][i].setAvalableTiles(this.borderTiles[Direction.UP.value]);
            dungeonMap[this.depth-1][i].setAvalableTiles(borderTiles[Direction.DOWN.value]);
            propagatorQueue.add(dungeonMap[0][i]);  
            propagatorQueue.add(dungeonMap[this.depth - 1][i]);
        }
        
        for (int j=1; j<this.depth-1; j++) {
            dungeonMap[j][0].setAvalableTiles(borderTiles[Direction.LEFT.value]);
            dungeonMap[j][this.width - 1].setAvalableTiles(borderTiles[Direction.RIGHT.value]);
            propagatorQueue.add(dungeonMap[j][0]);  
            propagatorQueue.add(dungeonMap[j][this.width - 1]);
        }
        
        boolean[] topLeft = booleanArraysIntersection(this.borderTiles[0], this.borderTiles[3]);
        boolean[] topRight = booleanArraysIntersection(this.borderTiles[0], this.borderTiles[1]);
        boolean[] bottomLeft = booleanArraysIntersection(this.borderTiles[2], this.borderTiles[3]);
        boolean[] bottomRight = booleanArraysIntersection(this.borderTiles[2], this.borderTiles[1]);
        
        dungeonMap[0][0].setAvalableTiles(topLeft);
        dungeonMap[0][this.width-1].setAvalableTiles(topRight);
        dungeonMap[this.depth-1][0].setAvalableTiles(bottomLeft);
        dungeonMap[this.depth-1][this.width-1].setAvalableTiles(bottomRight);
        
        propagatorQueue.add(dungeonMap[0][0]);  
        propagatorQueue.add(dungeonMap[0][this.width - 1]);
        propagatorQueue.add(dungeonMap[this.depth - 1][0]);  
        propagatorQueue.add(dungeonMap[this.depth - 1][this.width - 1]);
        
        while (true) {
            Tile nextTile = selectNextTile(this.propagatorQueue, false);
            if (nextTile == null) {
                break;
            }
            propagate(nextTile);
        }
    }

    /**
     * Create a boolean array with all values set to true
     *
     * @param size size of the array
     * @return return boolean array
     */
    private boolean[] createPosBooleanArray(int size) {
        boolean[] tiles = new boolean[size];
        for (int i = 0; i < size; i++) {
            tiles[i] = true;
        }
        return tiles;
    }

    /**
     * Randomly sets the outcome of the tile to one of its possible outcomes,
     * based on weighted distribution of tiles
     *
     * @param tileParam parameters containing coordinates for the tile
     */
    private void collapseTile(Tile tile) {
        int sumWeight = tile.getSumOfPossibleWeights();
        if (sumWeight < 1) {
            sumWeight = 1;
        }
        int randomInt = random.getNextIntInRange(sumWeight);

        boolean[] availableTiles = tile.getAvailableTiles();
        int chosenTile = 0;
        while (randomInt > 0) {
            for (; chosenTile < availableTiles.length; chosenTile++) {
                if (availableTiles[chosenTile]) {
                    randomInt -= this.tileWeights[chosenTile];
                }
                if (randomInt <= 0) {
                    break;
                }
            }
        }
        boolean[] availableAfterCollapse = new boolean[this.numPossibleTiles];
        availableAfterCollapse[chosenTile] = true;

        tile.setAvalableTiles(availableAfterCollapse);
        this.propagatorQueue.add(tile);
    }

    /**
     * Propagates the change in the propagator tile to its adjacent tiles.
     * @param propagator the tile that was changed, source of propagation
     */
    private void propagate(Tile propagator) {
        int row = propagator.getRow();
        int col = propagator.getCol();

        boolean[] propagatorTiles = propagator.getAvailableTiles();
        
        // cycle through cardinal directions 
        for (Direction dir: Direction.values()) {
            // coordinates for neighbouring tile
            int neighbourRow = row + dir.vectY;
            int neighbourCol = col + dir.vectX;
            
            // if the tile is valid, adjust its available tiles
            if (validCoordinate(neighbourRow, neighbourCol)) {
                Tile neighbour = dungeonMap[neighbourRow][neighbourCol];
                if (!neighbour.isCollapsed()) {

                    // tiles that the neighboring tile could turn into
                    boolean[] availableTiles = Arrays.copyOf(neighbour.getAvailableTiles(), numPossibleTiles);
                    boolean[] possibleTiles = gatherAvailableTiles(propagatorTiles, dir);
                    availableTiles = booleanArraysIntersection(availableTiles, possibleTiles);

                    // if the new tiles are different than what the tile already had, return true
                    Boolean tileChanged = neighbour.setAvalableTiles(availableTiles);
                    // if we get back null, there has been an error and map is reset
                    if (tileChanged == null) {
                        this.numberOfRetries++;
                        initMap();
                    }
                    // if tile is changed, add it to queues
                    else if (tileChanged) {
                        this.propagatorQueue.add(neighbour);
                        this.entropyQueue.add(neighbour);
                    }
                }
            }   
        }      
    }
    
    /**
     * Check if the given coordinates are within the map array
     * @param row array index
     * @param col array index
     * @return true if the coordinates are within the map, false otherwise
     */
    private boolean validCoordinate(int row, int col) {
        if (row < 0 || row >= this.depth) {
            return false;
        }
        return !(col < 0 || col >= this.width);
    }

    /**
     * Return a new boolean array with elements set to true if either element in
     * A or B are true.
     *
     * @param boolA Boolean array A
     * @param boolB Boolean array B
     * @return New boolean array
     */
    private boolean[] orTwoBooleanArrays(boolean[] boolA, boolean[] boolB) {
        boolean[] newBool = new boolean[boolA.length];
        for (int i = 0; i < boolA.length; i++) {
            newBool[i] = (boolA[i] || boolB[i]);
        }
        return newBool;
    }

    /**
     * Constructs a new boolean array with elements that are true in both
     * arrays. This is done by copying false elements from B to A, so not a true
     * intersection.
     *
     * @param boolA Array to be trimmed.
     * @param boolB Trimming array.
     * @return Boolean array
     */
    private boolean[] booleanArraysIntersection(boolean[] boolA, boolean[] boolB) {
        for (int i = 0; i < boolA.length; i++) {
            if (!boolB[i]) {
                boolA[i] = false;
            }
        }
        return boolA;
    }

    /**
     * Gathers all available tiles that a tile can collapse to,
     * based on adjacency rules.
     *
     * @param tiles Tiles available to the source of propagation.
     * @param dir Direction of propagation.
     * @return Boolean array of possible tiles.
     */
    private boolean[] gatherAvailableTiles(boolean[] tiles, Direction dir) {
        boolean[] newBool = new boolean[numPossibleTiles];
        for (int i = 0; i < numPossibleTiles; i++) {
            if (tiles[i]) {
                boolean[] possibleTiles = this.adjacencyRules[dir.value][i];
                newBool = orTwoBooleanArrays(newBool, possibleTiles);
            }
        }
        return newBool;
    }

    /**
     * Returns next tile from queue, according to tiles entropy values.
     *
     * @param queue Queue from where the tile is selected
     * @param rejectCollapsed If true collapsed tiles are rejected
     * @return Tile if available, null if no valid tile can be found
     */
    private Tile selectNextTile(TileQueue queue, boolean rejectCollapsed) {
        while (true) {
            if (!queue.isEmpty()) {
                Tile tile = queue.poll();
                if (tile.isCollapsed() != rejectCollapsed) {
                    return tile;
                }
            } else {
                break;
            }
        }
        return null;
    }
}
