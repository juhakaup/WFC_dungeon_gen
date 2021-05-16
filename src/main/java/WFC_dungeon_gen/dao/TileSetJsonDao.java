package WFC_dungeon_gen.dao;

import WFC_dungeon_gen.domain.TileSet;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * JSON implementation of the dao.
 * @author Juha Kauppinen
 */
public class TileSetJsonDao implements TileSetDao {
    private static final Gson GSON = new Gson();
    
    /**
     * JSON implementation of the data loading.
     * @param file file to be loaded.
     * @return TileSet object
     * @throws FileNotFoundException if the data could not be loaded.
     */
    @Override
    public TileSet loadTileSet(String file) throws FileNotFoundException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        TileSet tileSet = null;
        
        try {           
            JsonReader jsonReader = new JsonReader(reader);
            tileSet = GSON.fromJson(jsonReader, TileSet.class);
        } catch (Exception e) {
            System.out.println("error loading tile set from json");
        }
        return tileSet;
    }
}
