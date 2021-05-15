package WFC_dungeon_gen.util;

import WFC_dungeon_gen.domain.Direction;
import WFC_dungeon_gen.domain.TileSet;
import WFC_dungeon_gen.domain.Type;

/**
 *
 * @author Juha Kauppinen
 */
public class Validator {

    private final int[][] map;
    private final Type[] tileTypes;
    private final boolean[][] connections;
    private boolean[] visited;
    private final int columnLen;
    private int[] distances;
    private IntegerList heap;
    private int heapTop;
    private int startTile;
    private int endTile;
    private int distanceToExit;

    public Validator(int[][] map, TileSet tileSet) {
        this.map = map;
        this.tileTypes = tileSet.getTileTypes();
        this.connections = tileSet.getConnections();
        this.columnLen = this.map[0].length;
        int numberOfTiles = map.length * map[0].length;
        this.visited = new boolean[numberOfTiles + 1];
        this.distances = null;
        this.heap = new IntegerList();
        this.heapTop = -1;
    }

    public int getStartingTile() {
        return this.startTile;
    }

    public int getEndTile() {
        return this.endTile;
    }

    public int[] getDistances() {
        return this.distances;
    }
    
    public boolean isValid() {
        int startCol = this.startTile % this.columnLen;
        int startRow = (this.startTile - startCol) / this.columnLen;
        int endCol = this.endTile % this.columnLen;
        int endRow = (this.endTile - startCol) / this.columnLen;
        Type startType = this.tileTypes[this.map[startRow][startCol]];
        Type endType =  this.tileTypes[this.map[endRow][endCol]];

        if (startType == Type.EMPTY || endType == Type.EMPTY) {
            return false;
        }
        return this.distanceToExit != 0;
    }

    public void generateDistances() {
        this.startTile = findStartingTile();
        if (this.startTile != -1) {
            this.distanceToExit = 0;

            //this.distances = generateFlowField(startTile);
            //this.endTile = findLargestDistance(this.distances);
            // switches the starting point to farthest point until it no longer impoves the distance
            while (true) {
                int[] newDistanceMap = generateFlowField(startTile);
                int end = findLargestDistance(newDistanceMap);
                this.startTile = end;
                if (newDistanceMap[end] <= this.distanceToExit) {
                    break;
                }
                this.endTile = end;
                this.distances = newDistanceMap;
                this.distanceToExit = newDistanceMap[end];
            }
        } else {
            this.startTile = -1;
            this.endTile = -1;
        }
    }

    /**
     * Calculates steps it would take to reach each tile from given starting
     * point.
     *
     * @param start index of the starting tile.
     * @return integer array with distances for each tile index.
     */
    public int[] generateFlowField(int start) {
        this.visited = new boolean[map.length * map[0].length + 1];
        int nodeCount = 0;

        int[] distMap = new int[map.length * map[0].length];
        for (int i = 0; i < distMap.length; i++) {
            distMap[i] = Integer.MAX_VALUE;
        }

        pushToTheHeap(start);
        distMap[start] = 0;

        // calculate the distances
        while (this.heapTop >= 0) {
            int tile = removeLastFromHeap();
            int distance = distMap[tile];
            int row = (int) tile / this.columnLen;
            int col = tile % this.columnLen;
            boolean[] availableDirections = this.connections[map[row][col]];

            if (!this.visited[tile]) {
                nodeCount++;
                this.visited[tile] = true;
            }

            for (Direction dir : Direction.values()) {
                int adjacentRow = row + dir.vectY;
                int adjacentCol = col + dir.vectX;

                if (availableDirections[dir.value] && validCoordinate(adjacentRow, adjacentCol)) {
                    int newDistance = distance + 1;
                    int newIndex = adjacentRow * this.columnLen + adjacentCol;
                    if (distMap[newIndex] > newDistance) {
                        distMap[newIndex] = newDistance;
                        pushToTheHeap(newIndex);
                    }
                }
            }
        }
        return distMap;
    }

    /**
     * Finds the first tile that is not empty.
     *
     * @return index of the tile
     */
    private int findStartingTile() {
        this.visited = new boolean[this.map.length * this.map[0].length];
        int index = (this.visited.length/2) + (this.columnLen/2);
        while (true) {
            int col = index % this.columnLen;
            int row = (index - col) / this.columnLen;
            this.visited[index] = true;

            Type tileType = tileTypes[map[row][col]];
            if (tileType != Type.EMPTY) {
                return row * this.columnLen + col;
            }

            index++;
            if (index == this.map.length * this.columnLen) {
                index = 0;//return -1;
            }
            
            if (this.visited[index]) {
                return -1;
            }
        }
    }

    /**
     * Finds the index of the largest value of an array
     *
     * @param distanceMap integer array
     * @return the index of the largest value
     */
    private int findLargestDistance(int[] distanceMap) {
        int largest = 0;
        int index = -1;
        for (int i = 0; i < distanceMap.length; i++) {
            if (distanceMap[i] != Integer.MAX_VALUE && distanceMap[i] >= largest) {
                largest = distanceMap[i];
                index = i;
            }
        }
        return index;
    }

    /**
     * Check that the coordinate is within the map array
     *
     * @param row row index
     * @param col col index
     * @return true if coordinate is within the map, false otherwise
     */
    private boolean validCoordinate(int row, int col) {
        if (row < 0 || row >= map.length) {
            return false;
        }
        return !(col < 0 || col >= map[0].length);
    }

    /**
     * Adds given value to the list that is used as a heap
     *
     * @param value value to be added.
     */
    private void pushToTheHeap(int value) {
        this.heapTop++;
        if (this.heap.size() > this.heapTop) {
            this.heap.set(this.heapTop, value);
        } else {
            this.heap.add(value);
        }
    }

    /**
     * Retrieves the entry from the list representing the heap top
     *
     * @return integer
     */
    private int removeLastFromHeap() {
        return this.heap.get(this.heapTop--);
    }

}
