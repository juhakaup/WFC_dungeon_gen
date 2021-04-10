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
    private final int[] tileWeights;
    private final boolean[][][] adjacencyRules;
    private final int width;
    private final int depth;
    private final MyRandom random;
    private PriorityQueue<TileParameters> entropyQueue;
    private PriorityQueue<TileParameters> propagatorQueue;

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
        return convertToTileIds(this.dungeonMap);
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
     *
     * @param row Tile index row
     * @param col Tile index col
     * @param dir Direction of propagation
     * @param propagatorTiles Tiles that the propagator can turn into
     */
    private void reduce(int row, int col, Direction dir, boolean[] propagatorTiles) {
        Tile neighbour = dungeonMap[row][col];
        if (neighbour.isCollapsed()) {
            return;
        }

        boolean[] availableTiles = Arrays.copyOf(neighbour.getAvailableTiles(), numPossibleTiles);
        boolean[] possibleTiles = gatherAvailableTiles(propagatorTiles, dir);
        availableTiles = booleanArraysIntersection(availableTiles, possibleTiles);

        if (neighbour.setAvalableTiles(availableTiles)) {
            TileParameters param = new TileParameters(row, col, neighbour.getEntropy());
            this.propagatorQueue.add(param);
            this.entropyQueue.add(param);
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
    private Tile selectNextTile(PriorityQueue<TileParameters> queue, boolean rejectCollapsed) {
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
}
