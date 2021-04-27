package WFC_dungeon_gen.util;

import WFC_dungeon_gen.domain.Tile;

/**
 *
 * @author Juha Kauppinen
 */
public class TileQueue {

    private Tile[] tiles;
    private int lastEntry;
    private int maxSize;

    public TileQueue(int n) {
        this.tiles = new Tile[n];
        this.maxSize = n;
        this.lastEntry = 0;
    }

    /**
     * Adds a new tile to the queue, placing it in the right location according
     * to the entropy value of the tile.
     * @param tile Tile to be inserted into the queue
     */
    public void add(Tile tile) {
        this.lastEntry++;
        if (lastEntry == maxSize) {
            increaseSize();
        }
        double entropy = tile.getEntropy();
        int index = lastEntry;

        while (index > 1 && (entropy < this.tiles[findParent(index)].getEntropy())) {
            this.tiles[index] = this.tiles[findParent(index)];
            index = findParent(index);
        }
        this.tiles[index] = tile;
    }

    /**
     * Returns the tile with the smallest entropy value, removing it from the queue.
     * @return Tile
     */
    public Tile poll() {
        Tile tile = this.tiles[1];
        this.tiles[1] = this.tiles[this.lastEntry];
        this.lastEntry--;
        moveDown(1);
        return tile;
    }
    
    /**
     * Check if the queue is empty
     * @return true if the queue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.lastEntry == 0;
    }

    /**
     * Moves the tile from the given index further down the queue until it is 
     * in the right location.
     * @param index index of the tile to be moved.
     */
    private void moveDown(int index) {
        int left = findLeft(index);
        int smaller = 0;

        if (left == 0) {
            return;
        } else if (left == this.lastEntry) {
            smaller = left;
        } else {
            if (this.tiles[left].getEntropy() < this.tiles[left + 1].getEntropy()) {
                smaller = left;
            } else {
                smaller = left + 1;
            }
        }
        if (this.tiles[index].getEntropy() > this.tiles[smaller].getEntropy()) {
            Tile tmp = this.tiles[index];
            this.tiles[index] = this.tiles[smaller];
            this.tiles[smaller] = tmp;
            moveDown(smaller);
        }
    }

    /**
     * Finds the parent node in the binary tree form of the array.
     * @param index child who's parent needs to be found.
     * @return index of the parent.
     */
    private int findParent(int index) {
        return index / 2;
    }

    /**
     * Finds the left child of a parent in the binary tree form of the queue.
     * @param index index of the parent who's child needs to be found.
     * @return Index of the left child, 0 if there is no child.
     */
    private int findLeft(int index) {
        if (2 * index > this.lastEntry) {
            return 0;
        }
        return 2 * index;
    }
    
    /**
     * Allocates a larger array for tiles
     */
    private void increaseSize() {
        Tile[] newArray = new Tile[this.maxSize * 2];
        
        //System.arraycopy(this.tiles, 1, newArray, 1, this.maxSize - 1);
        
        for (int i=1; i<this.maxSize; i++) {
            newArray[i] = this.tiles[i];
        }
        
        this.tiles = newArray;
        this.maxSize = (this.maxSize * 2);
    }
}
