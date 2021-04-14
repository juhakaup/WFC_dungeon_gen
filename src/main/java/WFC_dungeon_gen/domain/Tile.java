package WFC_dungeon_gen.domain;

/**
 *
 * @author Juha Kauppinen
 */
public class Tile {
    private int finalValue;
    private boolean[] availableTiles;
    private int numOfAvailableTiles;
    private final double randomNoise;
    private boolean collapsed;
    private double entropy;
    private final int col;
    private final int row;
    private final int[] tileWeights;
    private int sumOfPossibleWeights;
    
    public Tile(double noise, int[] weights, int row, int col) {
        this.finalValue = 6;
        this.randomNoise = noise;
        this.tileWeights = weights;
        this.row = row;
        this.col = col;
        this.collapsed = false;
        this.availableTiles = new boolean[0];
    }
    
    @Override
    public String toString(){
        return String.valueOf(this.finalValue);
    }
    
    public int getRow() {
        return this.row;
    }
    
    public int getCol() {
        return this.col;
    } 
    
    public boolean isCollapsed() {
        return this.collapsed;
    }
    
    public boolean[] getAvailableTiles() {
        return this.availableTiles;
    }
    
    public int getFinalValue() {
        return this.finalValue;
    }
    
    /**
     * Sets the array of possible outcomes for the tile
     * @param newTiles boolean array of possible outcomes
     * @return true if the tile changed false otherwise
     */
    public Boolean setAvalableTiles(boolean[] newTiles) {
        int newCardinality = cardinality(newTiles);
        if (this.numOfAvailableTiles != newCardinality) {
            this.numOfAvailableTiles = newCardinality;
            this.availableTiles = newTiles;
            this.entropy = calculateEntropy();
            this.sumOfPossibleWeights = sumOfPossibleWeights();
            
            if (this.numOfAvailableTiles == 0) {
                return null;
            }
            if (this.numOfAvailableTiles == 1) {
                this.collapsed = true;
                for (int i=0;i<newTiles.length;i++) {
                    if(newTiles[i]) {
                        this.finalValue = i;
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Calculates the sum of tile weights based on available tiles in given tile
     * @param tile Tile on the map
     * @return sum of weights in integer
     */
    private int sumOfPossibleWeights() {
        int sum = 0;
        for (int i=0; i<this.availableTiles.length; i++) {
            if(this.availableTiles[i]) {
                sum += this.tileWeights[i];
            }
        }
        return sum;
    }
    
    public int getSumOfPossibleWeights() {
        return this.sumOfPossibleWeights;
    }
    
    public double getEntropy() {
        return this.entropy;
    }
    
    /**
     * Calculates the entropy for the tile based on what tiles are still available
     * needs to be refined
     * @return 
     */
    private double calculateEntropy() {
        int val = 0;
        for (boolean b : this.availableTiles) {
            if (b) {
                val++;
            }
        }
        //return Math.log(val)+this.randomNoise;
        return this.numOfAvailableTiles + this.randomNoise;
    }
    
    /**
     * The number of true elements in a boolean array.
     * @param bool boolean array
     * @return the count of true elements as integer
     */
    private int cardinality(boolean[] bool) {
        int count = 0;
        for (boolean b : bool) {
            if (b) count++;
        }
        return count;
    }
}
