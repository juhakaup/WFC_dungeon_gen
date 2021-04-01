package WFC_dungeon_gen.domain;

/**
 *
 * @author Juha Kauppinen
 */
public enum Direction {
    UP(0),
    RIGHT(1),
    DOWN(2),
    LEFT(3);
    
    public int value;
    
    private Direction(int value) {
        this.value = value;
    }
}
