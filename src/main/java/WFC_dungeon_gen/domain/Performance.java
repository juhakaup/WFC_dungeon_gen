package WFC_dungeon_gen.domain;

import WFC_dungeon_gen.dao.TileSetDao;
import WFC_dungeon_gen.dao.TileSetJsonDao;
import WFC_dungeon_gen.dao.TileSetTestData;
import java.io.FileNotFoundException;

/**
 * This class is used for some performance tests.
 * @author Juha Kauppinen
 */
public class Performance {

    public Performance() {

        //TileSetDao dao = new TileSetTestData();
        TileSetDao dao = new TileSetJsonDao();
        TileSet tileSet = null;
        try {
            tileSet = dao.loadTileSet("data/dungeon_trivial.JSON");
        } catch (FileNotFoundException ex) {
            System.out.println("error loading tileset");
        }
        
        int[] mapSizes = new int[]{10,14,20,28,40,56,80,113,160};
        int cycles = 10000;
        
        System.out.println("Testing map initialization ");
        for (int size : mapSizes) {
            Solver map = new Solver(size, size, tileSet, false);
            long time = 0;
            
            for (int i=0; i<cycles; i++) {
                long startTime = System.nanoTime();
                map.initMap();
                time += (System.nanoTime() - startTime);
            }
            
            System.out.printf("%d %-3dx%3d maps initialized in %9.3f ms. That is %.3f "
                    + "ms per %d tiles.\n", 
                    cycles, size, size, (time*0.00001), (time/(size*size)*0.00001), cycles);
        }
        
        System.out.println("\nTesting map solving after initialization");
        for (int size : mapSizes) {
            Solver map = new Solver(size, size, tileSet, false);
            long time = 0;
            
            for (int i=0; i<cycles; i++) {
                map.initMap();
                long startTime = System.nanoTime();
                map.generateMap();
                time += (System.nanoTime() - startTime);
            }
            System.out.printf("%d %-3dx%3d maps generated in %10.3f ms. That is %.3f "
                    + "ms per %d tiles.\n", 
                    cycles, size, size, (time*0.00001), (time/(size*size)*0.00001), cycles);
        }
    }
}
