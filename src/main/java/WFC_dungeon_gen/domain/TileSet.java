package WFC_dungeon_gen.domain;

import static WFC_dungeon_gen.domain.Direction.*;

/**
 *
 * @author Juha Kauppinen
 */
public class TileSet {

    private final char[][][] tiles;
    private final String[] allowedTilesUp;
    private final String[] allowedTilesRight;
    private final String[] allowedTilesDown;
    private final String[] allowedTilesLeft;
    private final int[] tileWeights;

    public TileSet() {
        this.tiles = null;
        this.allowedTilesUp = null;
        this.allowedTilesRight = null;
        this.allowedTilesDown = null;
        this.allowedTilesLeft = null;
        this.tileWeights = null;
    }

    public char[][][] getTiles() {
        return this.tiles;
    }

    public int getNumberOfTiles() {
        return this.tiles.length;
    }

    public int[] getTileWeights() {
        return this.tileWeights;
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

    private boolean[] strToBool(String str) {
        boolean[] bools = new boolean[str.length()];
        for (int i = 0; i < str.length(); i++) {
            bools[i] = str.charAt(i) == '1';
        }
        return bools;
    }
}
