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
    private int[] arrayHeap;
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
        this.arrayHeap = new int[0];

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
        while (arrayHeap.length > 0) {
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

//        System.out.println("node count " + nodeCount);
//        for (int i = 0; i < this.map.length; i++) {
//            for (int j = 0; j < this.map[0].length; j++) {
//                int dist = distMap[i*this.columnLen + j]; //dMap[i][j];
//                dist = dist == Integer.MAX_VALUE ? -1 : dist;
//                System.out.printf("%2d ", dist);
//            }
//            System.out.println("");
//        }
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
     * Adds given value to the end of an array.
     *
     * @param value value to be added.
     */
    private void pushToTheHeap(int value) {
        int[] newArray = new int[this.arrayHeap.length + 1];

        for (int i = 0; i < this.arrayHeap.length; i++) {
            newArray[i] = this.arrayHeap[i];
        }
        newArray[newArray.length - 1] = value;
        this.arrayHeap = newArray;
    }

    /**
     * Removes the last value from an array.
     *
     * @return the last value of an aray.
     */
    private int removeLastFromHeap() {
        int[] newArray = new int[this.arrayHeap.length - 1];
        int returnValue = this.arrayHeap[this.arrayHeap.length - 1];

        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = this.arrayHeap[i];
        }
        this.arrayHeap = newArray;
        return returnValue;
    }

}
