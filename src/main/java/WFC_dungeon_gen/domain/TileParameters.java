package WFC_dungeon_gen.domain;

/**
 *
 * @author Juha Kauppinen
 */
public class TileParameters implements Comparable<TileParameters> {
    private final int column;
    private final int row;
    private double entropyValue;
    
    public TileParameters(int row, int col, double entropy) {
        this.column = col;
        this.row = row;
        this.entropyValue = entropy;
    }
    
    public int getCol() {
        return this.column;
    }
    
    public int getRow() {
        return this.row;
    }
    
    public double getEntropy() {
        return this.entropyValue;
    }
    
    @Override
    public String toString() {
        return Double.toString(this.entropyValue);
    }

    @Override
    public int compareTo(TileParameters comparedTo) {
        return Double.compare(this.entropyValue, comparedTo.getEntropy());
    }
}
