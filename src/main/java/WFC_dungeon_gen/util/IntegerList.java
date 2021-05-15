package WFC_dungeon_gen.util;

/**
 *
 * @author Juha Kauppinen
 */
public class IntegerList {
    int[] list;
    int maxSize;
    int lastEntry;
    
    public IntegerList() {
        this.list = new int[2];
        this.maxSize = 2;
        this.lastEntry = 0;
    }
    
    /**
     * Adds new value to the list.
     * @param value number to be added
     */
    public void add(int value) {
        if (this.lastEntry == this.maxSize-1) {
            increaseArrayLength();
        }
        this.list[this.lastEntry] = value; 
        this.lastEntry++;
    }
    
    /**
     * Returns the value in the given index.
     * @param index index of the value
     * @return integer, assert if out of range
     */
    public int get(int index) {
        if (index < this.lastEntry && index >= 0) {
            return this.list[index];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    /**
     * Sets the value of given index
     * @param index index of the value
     * @param value value to be assigned to the index
     */
    public void set(int index, int value) {
        if (index <= this.lastEntry && index >= 0) {
            this.list[index] = value;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    /**
     * Returns the size of the list.
     * @return size as an integer.
     */
    public int size() {
        return this.lastEntry;
    }
    
    /**
     * Doubles the length of the current array 
     * by making a new one and copying the values over
     */
    private void increaseArrayLength() {
        this.maxSize = this.maxSize*2;
        int[] newArray = new int[this.maxSize];
        
        for (int i=0; i<this.list.length; i++) {
            newArray[i] = this.list[i];
        }
        this.list = newArray;
    }
}
