package WFC_dungeon_gen.domain;

import WFC_dungeon_gen.util.MyRandom;
import static WFC_dungeon_gen.domain.Direction.*;
import WFC_dungeon_gen.util.TileQueue;
import java.util.Arrays;

/**
 *
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
    private int rounds;
    private boolean[][] borderTiles;
    private final boolean maintainBorders;

    public Solver(int width, int depth, TileSet tileSet, boolean maintainBorders) {
        this.numPossibleTiles = tileSet.getNumberOfTiles();
        this.tileWeights = tileSet.getTileWeights();
        this.adjacencyRules = tileSet.getAdjacencyRules();
        this.width = width;
        this.depth = depth;
        this.rounds = 0;
        this.maintainBorders = maintainBorders;
        this.random = new MyRandom(12346);
        this.dungeonMap = null;
        this.borderTiles = tileSet.getBorderTiles();
        initMap();
    }
    
    public final void initMap() {
        this.entropyQueue = new TileQueue(this.width * this.depth);
        this.propagatorQueue = new TileQueue(this.width * this.depth);
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
        this.rounds = 0;
        return convertToTileIds(this.dungeonMap);
    }
    
    public void step() {
        Tile nextTile = selectNextTile(this.entropyQueue, true);
        if (nextTile != null) {   
            collapseTile(nextTile);
            propagate(nextTile);
        }
    }
    
    public int[][] getMap() {
        return convertToTileIds(dungeonMap);
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
    
        
    public void addBorder() {
//        boolean[] boarderTileDown = new boolean[this.numPossibleTiles];
//        boarderTileDown[0] = true;
//        boarderTileDown[3] = true;
//        boarderTileDown[4] = true;
//        boarderTileDown[6] = true;
//        boarderTileDown[10] = true;
//        boarderTileDown[18] = true;
//        boarderTileDown[20] = true;
//        boarderTileDown[23] = true;
//        
//        boolean[] borderTileUp = new boolean[this.numPossibleTiles];
//        borderTileUp[0] = true;
//        borderTileUp[1] = true;
//        borderTileUp[2] = true;
//        borderTileUp[6] = true;
//        borderTileUp[8] = true;
//        borderTileUp[16] = true;
//        borderTileUp[21] = true;
//        borderTileUp[22] = true;
//        
//        boolean[] borderTileLeft = new boolean[this.numPossibleTiles];
//        borderTileLeft[0] = true;
//        borderTileLeft[1] = true;
//        borderTileLeft[4] = true;
//        borderTileLeft[5] = true;
//        borderTileLeft[7] = true;
//        borderTileLeft[15] = true;
//        borderTileLeft[20] = true;
//        borderTileLeft[21] = true;
//        
//        
//        boolean[] borderTileRight = new boolean[this.numPossibleTiles];
//        borderTileRight[0] = true;
//        borderTileRight[2] = true;
//        borderTileRight[3] = true;
//        borderTileRight[5] = true;
//        borderTileRight[9] = true;
//        borderTileRight[17] = true;
//        borderTileRight[22] = true;
//        borderTileRight[23] = true;
        
        for (int i=1; i<this.width-1; i++) {
            dungeonMap[0][i].setAvalableTiles(this.borderTiles[0]);
            dungeonMap[this.depth-1][i].setAvalableTiles(borderTiles[2]);
            propagatorQueue.add(dungeonMap[0][i]);  
            propagatorQueue.add(dungeonMap[this.depth - 1][i]);
        }
        
        for (int j=1; j<this.depth-1; j++) {
            dungeonMap[j][0].setAvalableTiles(borderTiles[3]);
            dungeonMap[j][this.width - 1].setAvalableTiles(borderTiles[1]);
            propagatorQueue.add(dungeonMap[j][0]);  
            propagatorQueue.add(dungeonMap[j][this.width - 1]);
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
     * Randomly sets the outcome of the tile to one of its possibilities based
     * on weighted distribution of tiles
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
     * Spreading the change of available tiles to neighbouring tiles.
     *
     * @param propagator The source of propagation
     */
    private void propagate(Tile propagator) {
        int row = propagator.getRow();
        int col = propagator.getCol();

        boolean[] allowed = propagator.getAvailableTiles();

        if (row - 1 >= 0) {
            reduce(row - 1, col, UP, allowed);
        }
        if (row + 1 < this.depth) {
            reduce(row + 1, col, DOWN, allowed);
        }
        if (col - 1 >= 0) {
            reduce(row, col - 1, LEFT, allowed);
        }
        if (col + 1 < this.width) {
            reduce(row, col + 1, RIGHT, allowed);
        }
    }

    /**
     * Reducec the possible outcomes of a tile based on the available tiles in the
     * propagator tiles and propagation direction.
     * @param row Tile position
     * @param col Tile position
     * @param dir Direction of propagation
     * @param propagatorTiles Tiles available to the propagator
     */
    private void reduce(int row, int col, Direction dir, boolean[] propagatorTiles) {
        Tile neighbour = dungeonMap[row][col];
        if (neighbour.isCollapsed()) {
            return;
        }

        boolean[] availableTiles = Arrays.copyOf(neighbour.getAvailableTiles(), numPossibleTiles);
        boolean[] possibleTiles = gatherAvailableTiles(propagatorTiles, dir);
        availableTiles = booleanArraysIntersection(availableTiles, possibleTiles);

        Boolean tileChanged = neighbour.setAvalableTiles(availableTiles);
        if (tileChanged == null) {
            System.out.print("Propagation error, retry round " + this.rounds++ + "\r");
            initMap();
            generateMap();
        }
        else if (tileChanged) {
            this.propagatorQueue.add(neighbour);
            this.entropyQueue.add(neighbour);
        }
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
     * Gathers all available tiles that a neighbouring tile can collapse to,
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
     * Returns next tile from queue
     *
     * @param queue Queue from where the tile is selected
     * @param rejectCollapsed If true collapsed tiles are rejected
     * @return Tile if available, null no valid tile can be found
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
