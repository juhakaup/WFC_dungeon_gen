package WFC_dungeon_gen.util;

import WFC_dungeon_gen.domain.Direction;
import WFC_dungeon_gen.domain.TileSet;
import WFC_dungeon_gen.domain.Type;
import java.util.ArrayList;

/**
 *
 * @author Juha Kauppinen
 */
public class Validator {
    private int[][] map;
    private final Type[] tileTypes;
    private final boolean[][] connections;
    private boolean[] visited;
    private int[] roomParent;
    private int[] roomSize;
    private int numRooms;
    
    public Validator(int[][] map, TileSet tileSet) {
       this.map = map; 
       this.tileTypes = tileSet.getTileTypes();
       this.connections = tileSet.getConnections();
       int numberOfTiles = map.length * map[0].length;
       this.visited = new boolean[numberOfTiles + 1];
       this.roomParent = new int[numberOfTiles];
       this.roomSize = new int[numberOfTiles];
       this.numRooms = 0;
       
       for (int i=0; i<numberOfTiles; i++) {
           this.roomParent[i] = i;
           this.roomSize[i] = 1;
       }
    }
    
    private void addRoomTile(int y, int x) {
        int cols = this.map[0].length;
        int i = y * (cols)  + x;
        
        if (!visited[i]) {
            visited[i] = true;
            int diff = 0;
            
            if (visited[i+1]) {
                if (connectTiles(i, i+1)) {
                    diff++;
                }
            }
            if (visited[i-1]) {
                if (connectTiles(i, i-1)) {
                    diff++;
                }
            }
            if (visited[i+cols]) {
                if (connectTiles(i, i+cols)) {
                    diff++;
                }
            }
            if (visited[i-cols]) {
                if (connectTiles(i, i-cols)) {
                    diff++;
                }
            }
            
            switch (diff) {
                case 0:
                    this.numRooms++;
                    break;
                case 2:
                    this.numRooms--;
                    break;
                case 3:
                    this.numRooms = this.numRooms - 2;
                    break;
                case 4:
                    this.numRooms = this.numRooms - 3;
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Recursive method for finging the ends of corridors
     * @param row index on map
     * @param col index on map
     * @param connectingRooms list of rooms containing the connections
     */
    private void addCorridor(int row, int col, ArrayList<Integer> connectingRooms) {
        int colLen = this.map[0].length;
        int i = row * colLen  + col; 
        visited[i] = true;
        
        boolean[] tileConnections = this.connections[map[row][col]];

        // cycle throught directions, up-right-down-left
        for (Direction dir : Direction.values()) {
            int tileRow = row+dir.vectY;
            int tileCol = col+dir.vectX;
            
            // if tile has connection in given direction
            if (tileConnections[dir.value] && validCoordinate(tileRow, tileCol)) {              
                int index = tileRow * colLen + tileCol;
                if (!visited[index]) { 
                    Type thisTile = tileTypes[map[tileRow][tileCol]];
                    // if found corridor do recursion
                    if (thisTile == Type.CORRIDOR) {
                        addCorridor(tileRow, tileCol, connectingRooms);
                        // if found a room, add it to the list
                    } else if (thisTile == Type.ROOM || thisTile == Type.INTERSECTION) {
                        connectingRooms.add(findRoomRep(index));
                    }   
                }     
            }
        } 
    }
    
    /**
     * Check that the coordinate is within the map array
     * @param row row index
     * @param col col index
     * @return true if coordingate is within the map, false otherwise
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
        createNetwork();
        return false;
    }
    
    private void createNetwork() {
        for (int x=0; x<this.map.length; x++) {
            for (int y=0; y<this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                if (currentTileType == Type.ROOM || currentTileType == Type.INTERSECTION) {
                    //System.out.println("adding room " + x + "." + y);
                    addRoomTile(x, y);
                }
            }
        }
        System.out.println("number of rooms: " + this.numRooms);
        
        for (int i=0; i<this.roomParent.length; i++) {
            if (roomParent[i] != i) {
                //System.out.println("parent " + i);
                //System.out.println("rep " + findRoot(i));
            }
        }
        
        this.visited = new boolean[map.length * map[0].length + 1];
        for (int x=0; x<this.map.length; x++) {
            for (int y=0; y<this.map[0].length; y++) {
                Type currentTileType = tileTypes[map[x][y]];
                if (currentTileType == Type.CORRIDOR && !this.visited[x * this.map[0].length  + y]) {
                    ArrayList<Integer> connectingRooms = new ArrayList<>();
                    addCorridor(x, y, connectingRooms);
                    System.out.println("connecting rooms " + connectingRooms.toString() );
                }
            }
        }
        
    }
}

class Node {
    
}