package WFC_dungeon_gen.domain;

import static WFC_dungeon_gen.domain.Direction.*;

/**
 * TileSet contains the data used for creating dungeons.
 * @author Juha Kauppinen
 */
public class TileSet {

    private final String[][] tiles;
    private final String[] allowedTilesUp;
    private final String[] allowedTilesRight;
    private final String[] allowedTilesDown;
    private final String[] allowedTilesLeft;
    private final int[] tileWeights;
    private final int[][] borderTiles;
    private final Type[] tileTypes;
    private final boolean[][] connections;

    public TileSet() {
        this.tiles = null;
        this.allowedTilesUp = null;
        this.allowedTilesRight = null;
        this.allowedTilesDown = null;
        this.allowedTilesLeft = null;
        this.tileWeights = null;
        this.borderTiles = null;
        this.tileTypes = null;
        this.connections = null;
    }

    public String[][] getTiles() {
        return this.tiles;
    }

    public int getNumberOfTiles() {
        return this.tiles.length;
    }

    public int[] getTileWeights() {
        return this.tileWeights;
    }
    
    public Type[] getTileTypes() {
        return this.tileTypes;
    }
    
    public boolean[][] getConnections() {
        return this.connections;
    }
    
    public boolean[][] getBorderTiles() {
        boolean[] up = new boolean[this.tiles.length];
        boolean[] right = new boolean[this.tiles.length];
        boolean[] down = new boolean[this.tiles.length];
        boolean[] left = new boolean[this.tiles.length];
        boolean[][] borders = new boolean[][]{up,right,down,left};
        
        for (int i=0; i<4; i++) {
            for (int tile : this.borderTiles[i]) {
                borders[i][tile] = true;
            }
        }
        return borders;
    }

    public boolean[][][] getAdjacencyRules() {
        int size = getNumberOfTiles();
        boolean[][][] adjacencies = new boolean[4][size][size];
        for (int i = 0; i < size; i++) {
            adjacencies[UP.value][i] = strToBool(this.allowedTilesUp[i]);
            adjacencies[RIGHT.value][i] = strToBool(this.allowedTilesRight[i]);
            adjacencies[DOWN.value][i] = strToBool(this.allowedTilesDown[i]);
            adjacencies[LEFT.value][i] = strToBool(this.allowedTilesLeft[i]);
        }
        return adjacencies;
    }

    /**
     * Coverts the string representation of boolean array to actual boolean array.
     * @param str String containing zeros and ones.
     * @return boolean array.
     */
    private boolean[] strToBool(String str) {
        boolean[] bools = new boolean[str.length()];
        for (int i = 0; i < str.length(); i++) {
            bools[i] = str.charAt(i) == '1';
        }
        return bools;
    }
}
