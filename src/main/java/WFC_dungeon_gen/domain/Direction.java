package WFC_dungeon_gen.domain;

/**
 *
 * @author Juha Kauppinen
 */
public enum Direction {
    UP   (0, -1,  0),
    RIGHT(1,  0,  1),
    DOWN (2,  1,  0),
    LEFT (3,  0, -1);
    
    public int value;
    public int vectX;
    public int vectY;
    
    private Direction(int value, int row, int col) {
        this.value = value;
        this.vectY = row;
        this.vectX = col;
    }
}
