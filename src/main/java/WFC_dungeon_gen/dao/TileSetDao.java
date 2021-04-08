package WFC_dungeon_gen.dao;

import WFC_dungeon_gen.domain.TileSet;
import java.io.FileNotFoundException;

/**
 *
 * @author Juha Kauppinen
 */
public interface TileSetDao {

    /**
     * Loads tile set data from given file
     * @param file location and name of the json file
     * @return TileSet object
     * @throws FileNotFoundException if given file location is not valid
     */
    TileSet loadTileSet(String file) throws FileNotFoundException;
    
}
