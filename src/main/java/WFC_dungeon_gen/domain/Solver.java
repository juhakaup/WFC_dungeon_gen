package WFC_dungeon_gen.domain;

import static WFC_dungeon_gen.domain.Direction.*;
import java.util.Arrays;
import java.util.PriorityQueue;

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
    private PriorityQueue<TileParameters> entropyQueue;
    private PriorityQueue<TileParameters> propagatorQueue;
    private final String tmpTiles = "│─┐└┘┌□█┼"; // utf-8 characters

    public Solver(int width, int depth, int numTiles, int[] weights, boolean[][][] rules) {
        this.numPossibleTiles = numTiles;
        this.tileWeights = weights;
        this.adjacencyRules = rules;
        this.width = width;
        this.depth = depth;
        this.random = new MyRandom(12346);
        this.entropyQueue = new PriorityQueue<>(width * depth);
        this.propagatorQueue = new PriorityQueue<>(width * depth);
        this.dungeonMap = initializeMap();
    }

    public int[][] solveMaze() {
        while (true) {
            Tile nextTile = chooseNextTile(this.entropyQueue, true);
            if (nextTile == null) {
                break;
            }
            collapseTile(nextTile);
            propagate(nextTile);

            while (true) {
                nextTile = chooseNextTile(this.propagatorQueue, false);
                if (nextTile == null) {
                    break;
                }
                //System.out.println("chosen entropy " + nextTile.getEntropy());
                //printMaze();
                propagate(nextTile);
                //System.out.println("propagate");
            }
            //printMaze();
            //System.out.println("collapse");
        }
        return convertToTileIds();
    }

    private int[][] convertToTileIds() {
        int[][] intMap = new int[this.depth][this.width];
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.depth; row++) {
                intMap[row][col] = dungeonMap[row][col].getFinalValue();
            }
        }
        return intMap;
    }

    /**
     * Initializes the dungeon map with tiles
     *
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
        //System.out.println("collapse " + tile.getRow() + "," + tile.getCol());

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
        this.propagatorQueue.add(
                new TileParameters(tile.getRow(), tile.getCol(), tile.getEntropy())
        );
    }

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

    private void reduce(int row, int col, Direction dir, boolean[] propagatorTiles) {
        //System.out.println("reduce " + row + ", " + col);
        Tile neighbour = dungeonMap[row][col];

        if (neighbour.isCollapsed()) {
            return;
        }
        
        boolean[] availableTiles = Arrays.copyOf(neighbour.getAvailableTiles(), numPossibleTiles);
        boolean[] possibleTiles = getAvailableTilesFromRules(propagatorTiles, dir);
        availableTiles = removeNotAvailable(availableTiles, possibleTiles);

        if (neighbour.setAvalableTiles(availableTiles)) {
            TileParameters param = new TileParameters(row, col, neighbour.getEntropy());
            this.propagatorQueue.add(param);
            this.entropyQueue.add(param);
        }
    }
    
    private String debugOutput(boolean[] tiles) {
        String str = "";
        int[] bits = new int[tiles.length];
        for (int i=0; i<tiles.length; i++) {
            if(tiles[i]) {
                str += this.tmpTiles.charAt(i);
                bits[i] = 1;
            }
        }
        return Arrays.toString(bits) + " " + str;
    }

    private boolean[] andTwoBooleanArrays(boolean[] boolA, boolean[] boolB) {
        boolean[] newBool = new boolean[boolA.length];
        for (int i = 0; i < boolA.length; i++) {
            newBool[i] = (boolA[i] && boolB[i]);
        }
        return newBool;
    }

    private boolean[] orTwoBooleanArrays(boolean[] boolA, boolean[] boolB) {
        boolean[] newBool = new boolean[boolA.length];
        for (int i = 0; i < boolA.length; i++) {
            newBool[i] = (boolA[i] || boolB[i]);
        }
        return newBool;
    }

    private boolean[] removeNotAvailable(boolean[] boolA, boolean[] boolB) {
        for (int i = 0; i < boolA.length; i++) {
            if (!boolB[i]) {
                boolA[i] = false;
            }
        }
        return boolA;
    }

    private boolean[] getAvailableTilesFromRules(boolean[] tiles, Direction dir) {
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
     * Returns the next uncollapsed tile with the lowest entropy value
     *
     * @return TileParameters containing coordinates
     */
    private Tile chooseNextTile(PriorityQueue<TileParameters> queue, boolean rejectCollapsed) {
        while (true) {
            if (!queue.isEmpty()) {
                TileParameters params = queue.poll();
                Tile tile = dungeonMap[params.getRow()][params.getCol()];
                if (tile.isCollapsed() != rejectCollapsed) {
                    return tile;
                }
            } else {
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

// more complicated reducer
//    private void reduceNew(int row, int col, Direction dir) {
//        Tile tile = dungeonMap[row][col];
//
//        if (tile.isCollapsed()) {
//            return;
//        }
//
//        boolean[] availableTiles = Arrays.copyOf(tile.getAvailableTiles(), numPossibleTiles);
//
//        if (row - 1 >= 0) {
//            boolean[] tilesUp = this.dungeonMap[row - 1][col].getAvailableTiles();
//            boolean[] possibleTiles = getAvailableTilesFromRules(tilesUp, UP);
//            availableTiles = removeNotAvailable(availableTiles, possibleTiles);
//        }
//        if (row + 1 < this.depth) {
//            boolean[] tilesDown = this.dungeonMap[row + 1][col].getAvailableTiles();
//            boolean[] possibleTiles = getAvailableTilesFromRules(tilesDown, DOWN);
//            availableTiles = removeNotAvailable(availableTiles, possibleTiles);
//        }
//        if (col - 1 >= 0) {
//            boolean[] tilesLeft = this.dungeonMap[row][col - 1].getAvailableTiles();
//            boolean[] possibleTiles = getAvailableTilesFromRules(tilesLeft, LEFT);
//            availableTiles = removeNotAvailable(availableTiles, possibleTiles);;
//        }
//        if (col + 1 < this.width) {
//            boolean[] tilesDown = this.dungeonMap[row][col + 1].getAvailableTiles();
//            boolean[] possibleTiles = getAvailableTilesFromRules(tilesDown, RIGHT);
//            availableTiles = removeNotAvailable(availableTiles, possibleTiles);
//        }
//
//        Boolean tileReduced = tile.setAvalableTiles(availableTiles);
//        if (tileReduced == null) {
//            System.out.println("propagation error");
//        }
//        if (tileReduced) {
//            TileParameters param = new TileParameters(row, col, tile.getEntropy());
//            this.propagatorQueue.add(param);
//            this.entropyQueue.add(param);
//        }
//    }

