package WFC_dungeon_gen.domain;

/**
 *
 * @author Juha Kauppinen
 */
public class TileSet {
    // hard coded data for now
    // ─│┌┐└┘├┤┬┴┼■□·
    // reduced tileset 
    // allowed up
    //   ─ │ ┐ └ ·
    // ─ 0 0 0 0 1
    // │ 0 1 1 0 1
    // ┐ 0 0 0 0 1
    // └ 0 1 0 0 1
    // · 1 1 1 1 1
    //
    // allowed right
    //   ─ │ ┐ └ ·
    // ─ 1 0 1 0 1
    // │ 0 0 0 0 1
    // ┐ 0 0 0 0 1
    // └ 1 0 0 0 1
    // · 1 1 1 1 1
    //
    // allowed down
    //   ─ │ ┐ └ ·
    // ─ 0 0 0 0 1
    // │ 0 1 0 1 1
    // ┐ 0 1 0 0 1
    // └ 0 0 0 0 1
    // · 1 1 1 1 1
    //
    // allowed left
    //   ─ │ ┐ └ ·
    // ─ 1 0 0 1 1
    // │ 0 0 0 0 1
    // ┐ 1 0 0 0 1
    // └ 0 0 0 0 1
    // · 1 1 1 1 1
    
    private String allTiles;
    private boolean[][][] adjacencyRules;
    
    public TileSet() {
        this.allTiles =  "-|jL.";//"─│┐└·"; //"─│┌┐└┘├┤┬┴┼■□·";
        
        boolean[][] allowedTilesUp = new boolean[allTiles.length()][allTiles.length()];
        allowedTilesUp[0] = strToBool("00001");
        allowedTilesUp[1] = strToBool("01101");
        allowedTilesUp[2] = strToBool("00001");
        allowedTilesUp[3] = strToBool("01001");
        allowedTilesUp[4] = strToBool("11111");
        
        boolean[][] allowedTilesRight = new boolean[allTiles.length()][allTiles.length()];
        allowedTilesRight[0] = strToBool("10101");
        allowedTilesRight[1] = strToBool("00001");
        allowedTilesRight[2] = strToBool("01001");
        allowedTilesRight[3] = strToBool("10001");
        allowedTilesRight[4] = strToBool("11111");
        
        boolean[][] allowedTilesDown = new boolean[allTiles.length()][allTiles.length()];
        allowedTilesDown[0] = strToBool("00001");
        allowedTilesDown[1] = strToBool("01011");
        allowedTilesDown[2] = strToBool("01001");
        allowedTilesDown[3] = strToBool("00001");
        allowedTilesDown[4] = strToBool("11111");
        
        boolean[][] allowedTilesLeft = new boolean[allTiles.length()][allTiles.length()];
        allowedTilesLeft[0] = strToBool("10011");
        allowedTilesLeft[1] = strToBool("00001");
        allowedTilesLeft[2] = strToBool("10001");
        allowedTilesLeft[3] = strToBool("00001");
        allowedTilesLeft[4] = strToBool("11111");
        
        this.adjacencyRules = new boolean[][][]
            {allowedTilesUp, allowedTilesRight, allowedTilesDown, allowedTilesLeft};
    }
    
    private boolean[] strToBool(String str) {
        boolean[] bools = new boolean[str.length()];
        for (int i=0; i<str.length();i++) {
            bools[i] = str.charAt(i) == '1';
        }
        return bools;
    }
    
    public boolean[][][] getAdjacencyRules() {
        return this.adjacencyRules;
    }
    
    public int getNumberOfTiles() {
        return this.allTiles.length();
    }
    
    public int[] getTileWeights() {
        // hard coded for now
        return new int[]{50,50,11,11,1};
    }
}

