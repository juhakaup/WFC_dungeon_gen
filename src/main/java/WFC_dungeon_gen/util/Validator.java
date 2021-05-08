package WFC_dungeon_gen.util;

import WFC_dungeon_gen.domain.Direction;
import WFC_dungeon_gen.domain.TileSet;
import WFC_dungeon_gen.domain.Type;
import java.util.ArrayDeque;
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
    private int[] intersections;
    private int numberOfRooms;
    private int[][] distances;

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
        this.intersections = new int[0];
        this.numberOfRooms = 0;
        this.distances = null;

        for (int i = 0; i < numberOfTiles; i++) {
            this.roomParent[i] = i;
            this.roomSize[i] = 1;
        }
    }

    public boolean canTraverse(int startX, int startY, int endX, int endY) {
        generateFlowField();
        //findRooms();
        //findIntersections();
        //findNodeConnections();
        //generateAllPaths();
        return false;
    }

    private void generateFlowField() {
        this.visited = new boolean[map.length * map[0].length + 1];
        int[][] dMap = new int[this.map.length][this.map[0].length];

        // generate distance map
        for (int x = 0; x < this.map.length; x++) {
            for (int y = 0; y < this.map[0].length; y++) {
                dMap[x][y] = Integer.MAX_VALUE;
            }
        }

        ArrayDeque<int[]> heap = new ArrayDeque<>();

        // find first suitable starting tile
        int index = 0;
        while (true) {
            int col = index % this.columnLen;
            int row = (index - col) / this.columnLen;
            this.visited[index] = true;

            Type tileType = tileTypes[map[row][col]];
            if (tileType != Type.EMPTY) {
                heap.push(new int[]{0, row, col});
                dMap[row][col] = 0;
                break;
            }

            index++;
            if (index == this.map.length * this.columnLen) {
                break;
            }
        }

        // calculate the distances
        while (!heap.isEmpty()) {
            int[] tile = heap.removeLast();
            int distance = tile[0];
            int row = tile[1];
            int col = tile[2];
            boolean[] availableDirections = this.connections[map[row][col]];
            
            this.visited[row * this.columnLen + col] = true;

            for (Direction dir : Direction.values()) {
                int adjacentRow = row + dir.vectY;
                int adjacentCol = col + dir.vectX;

                if (availableDirections[dir.value] && validCoordinate(adjacentRow, adjacentCol)) {
                    int newDistance = distance + 1;
                    int oldDistance = dMap[adjacentRow][adjacentCol];
                    if (oldDistance > newDistance) {
                        dMap[adjacentRow][adjacentCol] = newDistance;
                        heap.push(new int[]{distance + 1, adjacentRow, adjacentCol});
                    }
                }
            }
        }
        
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[0].length; j++) {
                int dist = dMap[i][j];
                dist = dist == Integer.MAX_VALUE ? -1 : dist;
                System.out.printf("%2d ", dist);
            }
            System.out.println("");
        }
    }

    /**
     * Recursive method for finding adjacent room tiles
     *
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
                    if (thisTile == Type.ROOM || thisTile == Type.ENTRANCE) {
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
     * @param nodeConnections list of rooms containing the connections
     */
    private int[] addCorridor(int row, int col, int[] nodeConnections) {
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
                        nodeConnections[0]++;
                        nodeConnections = addCorridor(tileRow, tileCol, nodeConnections);
                        // if a room is found, add it to the list
                    } else if (thisTile == Type.ENTRANCE) {
                        nodeConnections = addToList(nodeConnections, findRoomRep(index));
                    } else if (thisTile == Type.INTERSECTION) {
                        nodeConnections = addToList(nodeConnections, index);
                    }
                }
            }
        }
        return nodeConnections;
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

    /**
     * Cycles through the map, once a room or intersection tile is found,
     * collects all connected room tiles in a list.
     */
    private void findRoomsOld() {
        this.visited = new boolean[map.length * map[0].length + 1];

        for (int x = 0; x < this.map.length; x++) {
            for (int y = 0; y < this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                if ((currentTileType == Type.ROOM || currentTileType == Type.ENTRANCE)
                        && !this.visited[x * this.map[0].length + y]) {
                    int[] roomCluster = new int[0];
                    roomCluster = addRoom(x, y, roomCluster);
                    System.out.println("room cluster " + Arrays.toString(roomCluster));
                    rooms = addToList(rooms, x * this.map[0].length + y);
                }
            }
        }
        this.numberOfRooms = this.rooms.length;
        System.out.println("rooms" + Arrays.toString(rooms));
        System.out.println("number of rooms: " + this.rooms.length);
    }

    private void findRooms() {
        this.visited = new boolean[map.length * map[0].length + 1];

        for (int x = 0; x < this.map.length; x++) {
            for (int y = 0; y < this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                if ((currentTileType == Type.ROOM || currentTileType == Type.ENTRANCE)
                        && !this.visited[x * this.map[0].length + y]) {
                    int[] roomCluster = new int[0];
                    roomCluster = addRoom(x, y, roomCluster);
                    System.out.println("room cluster " + Arrays.toString(roomCluster));
                    rooms = addToList(rooms, x * this.map[0].length + y);
                }
            }
        }
        this.numberOfRooms = this.rooms.length;
        System.out.println("number of rooms: " + this.rooms.length);
        System.out.println("rooms" + Arrays.toString(rooms));
    }

    private void findIntersections() {
        for (int x = 0; x < this.map.length; x++) {
            for (int y = 0; y < this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                if ((currentTileType == Type.INTERSECTION)
                        && !this.visited[x * this.map[0].length + y]) {
                    this.visited[x * this.map[0].length + y] = true;
                    this.rooms = addToList(rooms, x * this.map[0].length + y);
                    intersections = addToList(intersections, x * this.map[0].length + y);
                }
            }
        }
        System.out.println("number of intersections " + this.intersections.length);
        System.out.println("intersections " + Arrays.toString(intersections));
        System.out.println("rooms" + Arrays.toString(rooms));
    }

    private int roomId(int roomNumber) {
        int i = 0;
        for (int r : this.rooms) {
            if (r == roomNumber) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    /**
     * Cycles through the map, once a corridor tile is found, traverses
     * connecting tiles in each direction until a room or intersection tile is
     * found.
     */
    private void findNodeConnections() {
        int numberOfNodes = this.rooms.length + this.intersections.length;
        this.distances = new int[numberOfNodes][numberOfNodes];

        for (int i = 0; i < numberOfNodes; i++) {
            for (int j = 0; j < numberOfNodes; j++) {
                this.distances[i][j] = i == j ? 0 : Integer.MAX_VALUE;
            }
        }

        this.visited = new boolean[map.length * map[0].length + 1];

        int idOne = 0;
        int idTwo = 0;
        int corridorLength = 0;

        for (int x = 0; x < this.map.length; x++) {
            for (int y = 0; y < this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                int tileId = x * this.map[0].length + y;
                if (currentTileType == Type.CORRIDOR && !this.visited[tileId]) {
                    int[] connectedNodes = new int[]{1};
                    connectedNodes = addCorridor(x, y, connectedNodes);
                    System.out.println("corridor " + Arrays.toString(connectedNodes));

                    idOne = roomId(connectedNodes[1]);
                    idTwo = roomId(connectedNodes[2]);
                    corridorLength = connectedNodes[0];
                } else if (currentTileType == Type.ENTRANCE && !this.visited[tileId]) {
                    int tileParent = findRoomRep(tileId);
                    this.visited[tileId] = true;
                    boolean[] tileConnections = this.connections[map[x][y]];
                    for (Direction dir : Direction.values()) {
                        int tileRow = x + dir.vectY;
                        int tileCol = y + dir.vectX;
                        int otherId = tileRow * this.map[0].length + tileCol;
                        int otherParent = findRoomRep(otherId);
                        if (!tileConnections[dir.value] && validCoordinate(tileRow, tileCol)) {
                            Type adjacentTileType = tileTypes[map[tileRow][tileCol]];
                            if (adjacentTileType == Type.ENTRANCE && tileParent != otherParent) {
                                //this.visited[otherId] = true;
                                System.out.println("connected rooms " + findRoomRep(tileId) + " and " + findRoomRep(otherId));
                                corridorLength = 1; //distance between adjacent rooms
                            }
                        }
                    }
                }
                int oldLen = this.distances[idOne][idTwo];
//                    //System.out.println("room1:" + idOne + " room2:" + idTwo + " oldLen:" + oldLen + " newLen:" + newLen);
                if (corridorLength < oldLen) {
                    this.distances[idOne][idTwo] = corridorLength;
                    this.distances[idTwo][idOne] = corridorLength;
                }
//                    for (int i=0; i<this.numberOfRooms; i++) {
//                        for (int j=0; j<this.numberOfRooms; j++) {
//                            System.out.print("["+this.distances[i][j]+"],");
//                        }
//                        System.out.println("\n");
//                    }
            }
        }
        for (int i = 0; i < this.numberOfRooms; i++) {
            for (int j = 0; j < this.numberOfRooms; j++) {
                int dist = this.distances[i][j];
                dist = dist == Integer.MAX_VALUE ? 0 : dist;
                System.out.print("[" + dist + "],");
            }
            System.out.println("");
        }
    }

    private void generateAllPaths() {
        int[][] allPathLengths = new int[this.numberOfRooms][this.numberOfRooms];

        // initialize array
        for (int i = 0; i < this.numberOfRooms; i++) {
            for (int j = 0; j < this.numberOfRooms; j++) {
                allPathLengths[i][j] = i == j ? 0 : Integer.MAX_VALUE;
            }
        }

        // find all the shortest paths
        for (int k = 0; k < this.numberOfRooms; k++) {
            for (int i = 0; i < this.numberOfRooms; i++) {
                for (int j = 0; j < this.numberOfRooms; j++) {
                    int currentDist = findDistance(i, j);
                    int newDist = findDistance(i, k);
                    if (newDist < currentDist) {
                        this.distances[i][j] = newDist;
                    }
                }
            }
        }

        System.out.println("all paths");
        for (int i = 0; i < this.numberOfRooms; i++) {
            for (int j = 0; j < this.numberOfRooms; j++) {
                int dist = allPathLengths[i][j] == Integer.MAX_VALUE ? 0 : allPathLengths[i][j];
                //System.out.print("["+allPathLengths[i][j]+"],");
                System.out.print("[" + dist + "],");
            }
            System.out.println("");
        }
        System.out.println("\n all dist");

        for (int i = 0; i < this.numberOfRooms; i++) {
            for (int j = 0; j < this.numberOfRooms; j++) {
                int dist = this.distances[i][j];
                dist = dist == Integer.MAX_VALUE ? 0 : dist;
                System.out.print("[" + dist + "],");
            }
            System.out.println("");
        }
    }

    private int findDistance(int i, int j) {
        return this.distances[i][j];
    }

    private int[] addToList(int[] array, int value) {
        int[] newArray = new int[array.length + 1];

        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[newArray.length - 1] = value;

        return newArray;
    }

}
