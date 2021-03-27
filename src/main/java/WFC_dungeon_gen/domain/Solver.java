package WFC_dungeon_gen.domain;

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
    private final int width;
    private final int depth;
    private final MyRandom random;
    private final PriorityQueue<TileParameters> entropyQue;
    
    public Solver(int width, int depth, int numTiles, int[] tileWeights) {
        this.numPossibleTiles = numTiles;
        this.numCollapsedTiles = 0;
        this.tileWeights = tileWeights;
        this.width = width;
        this.depth = depth;
        this.random = new MyRandom(12346);
        this.entropyQue = new PriorityQueue<>(width*depth);
        this.dungeonMap = initializeMap();
        
        while(this.numCollapsedTiles < this.width*this.depth){
            TileParameters nextTile = chooseNextTile();
            collapseTile(nextTile);
        }
    }
    
    /**
     * Initializes the dungeon map with tiles
     * @return 2d array of Tiles
     */
    private Tile[][] initializeMap() {
        boolean[] initialTiles = initAvailableTiles(this.numPossibleTiles);
        Tile[][] tiles = new Tile[this.depth][this.width];
        
        for (int col = 0; col < this.width; col++) {
            for (int row = 0; row < this.depth; row++) {
                double noise = this.random.getNextEntropyNoiseValue();
                Tile tile = new Tile(noise, this.tileWeights);
                tile.setAvalableTiles(initialTiles);
                entropyQue.add(new TileParameters(row, col, tile.getEntropy()));
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
    private boolean[] initAvailableTiles(int size) {
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
    private void collapseTile(TileParameters tileParam) {
        Tile tile = dungeonMap[tileParam.getRow()][tileParam.getCol()];
        
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
        this.numCollapsedTiles += 1;
    }
    
    /**
     * Returns the next uncollapsed tile with the lowest entropy value
     * @return TileParameters containing coordinates
     */
    private TileParameters chooseNextTile() {
        while(true) {
            TileParameters params = this.entropyQue.poll();
            if (!dungeonMap[params.getRow()][params.getCol()].isCollapsed()) {
                //System.out.println(tev.getRow() + "," + tev.getCol());
                return params;
            }
        }
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
