package WFC_dungeon_gen.util;

import WFC_dungeon_gen.domain.Direction;
import WFC_dungeon_gen.domain.TileSet;
import WFC_dungeon_gen.domain.Type;
import java.util.Arrays;

/**
 *
 * @author Juha Kauppinen
 */
public class Validator {

    private int[][] map;
    private final Type[] tileTypes;
    private final boolean[][] connections;
    private boolean[] visited;
    private final int[] roomParent;
    private final int[] roomSize;
    private final int columnLen;
    private int[] rooms;

    public Validator(int[][] map, TileSet tileSet) {
        this.map = map;
        this.tileTypes = tileSet.getTileTypes();
        this.connections = tileSet.getConnections();
        this.columnLen = this.map[0].length;
        int numberOfTiles = map.length * map[0].length;
        this.visited = new boolean[numberOfTiles + 1];
        this.roomParent = new int[numberOfTiles];
        this.roomSize = new int[numberOfTiles];
        this.rooms = new int[0];

        for (int i = 0; i < numberOfTiles; i++) {
            this.roomParent[i] = i;
            this.roomSize[i] = 1;
        }
    }

    /**
     * Recursive method for finding adjacent room tiles
     * @param row map index
     * @param col map index
     * @param roomCluster list of tiles in this room
     */
    private int[] addRoom(int row, int col, int[] roomCluster) {
        int i = row * this.columnLen + col;
        visited[i] = true;
        roomCluster = addToList(roomCluster, i);

        boolean[] tileConnections = this.connections[map[row][col]];

        // cycle throught cardinal directions, up-right-down-left
        for (Direction dir : Direction.values()) {
            int tileRow = row + dir.vectY;
            int tileCol = col + dir.vectX;

            // if tile has connection in given direction
            if (tileConnections[dir.value] && validCoordinate(tileRow, tileCol)) {
                int index = tileRow * this.columnLen + tileCol;
                if (!visited[index]) {
                    Type thisTile = tileTypes[map[tileRow][tileCol]];
                    // if a room is found, do a recursion
                    if (thisTile == Type.ROOM || thisTile == Type.INTERSECTION) {
                        connectTiles(i, (i + dir.vectY * this.columnLen + dir.vectX));
                        roomCluster = addRoom(tileRow, tileCol, roomCluster);
                    }
                }
            }
        }
        
        return roomCluster;
    }

    /**
     * Recursive method for finding the ends of corridors
     *
     * @param row index on map
     * @param col index on map
     * @param connectingRooms list of rooms containing the connections
     */
    private int[] addCorridor(int row, int col, int[] connectingRooms) {
        int i = row * this.columnLen + col;
        visited[i] = true;

        boolean[] tileConnections = this.connections[map[row][col]];

        // cycle throught cardinal directions, up-right-down-left
        for (Direction dir : Direction.values()) {
            int tileRow = row + dir.vectY;
            int tileCol = col + dir.vectX;

            // if tile has connection in given direction
            if (tileConnections[dir.value] && validCoordinate(tileRow, tileCol)) {
                int index = tileRow * this.columnLen + tileCol;
                if (!visited[index]) {
                    Type thisTile = tileTypes[map[tileRow][tileCol]];
                    // if a corridor is found, do a recursion
                    if (thisTile == Type.CORRIDOR) {
                        connectingRooms[0]++;
                        connectingRooms = addCorridor(tileRow, tileCol, connectingRooms);
                        // if a room is found, add it to the list
                    } else if (thisTile == Type.ROOM || thisTile == Type.INTERSECTION) {
                        connectingRooms = addToList(connectingRooms, findRoomRep(index));
                    }
                }
            }
        }
        return connectingRooms;
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

    private boolean connectTiles(int a, int b) {
        a = this.findRoomRep(a);
        b = this.findRoomRep(b);

        if (a != b) {
            if (roomSize[a] < roomSize[b]) {
                int tmp = a;
                a = b;
                b = tmp;
            }
            roomParent[b] = a;
            roomSize[a] += roomSize[b];
            return true;
        }
        return false;
    }

    private int findRoomRep(int i) {
        while (i != roomParent[i]) {
            i = roomParent[i];
        }
        return i;
    }

    public void setMap(int[][] newMap) {
        this.map = newMap;
    }

    public boolean canTraverse(int startX, int startY, int endX, int endY) {
        findRooms();
        findRoomConnections();
        return false;
    }

    /**
     * Cycles through the map, once a room or intersection tile is found,
     * collects all connected room tiles in a list.
     */
    private void findRooms() {
        this.visited = new boolean[map.length * map[0].length + 1];
        
        for (int x = 0; x < this.map.length; x++) {
            for (int y = 0; y < this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                if ((currentTileType == Type.ROOM || currentTileType == Type.INTERSECTION)
                        && !this.visited[x * this.map[0].length + y]) {
                    int[] roomCluster = new int[0];
                    roomCluster = addRoom(x, y, roomCluster);
                    System.out.println("room cluster " + Arrays.toString(roomCluster));
                    rooms = addToList(rooms, x * this.map[0].length + y);
                }
            }
        }
        System.out.println("number of rooms: " + this.rooms.length);
    }

    /**
     * Cycles through the map, once a corridor tile is found, traverses
     * connecting tiles in each direction until a room or intersection tile is
     * found.
     */
    private void findRoomConnections() {
        this.visited = new boolean[map.length * map[0].length + 1];
        for (int x = 0; x < this.map.length; x++) {
            for (int y = 0; y < this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                if (currentTileType == Type.CORRIDOR && !this.visited[x * this.map[0].length + y]) {
                    int[] connectingRooms = new int[]{1};
                    connectingRooms = addCorridor(x, y, connectingRooms);
                    System.out.println("room connections " + Arrays.toString(connectingRooms));
                }
            }
        }
    }
    
    private int[] addToList(int[] array, int value) {
        int[] newArray = new int[array.length + 1];
        
        for (int i=0; i<array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[newArray.length - 1] = value;
        
        return newArray;
    }

}
