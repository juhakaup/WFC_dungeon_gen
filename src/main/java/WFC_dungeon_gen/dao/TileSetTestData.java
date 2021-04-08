package WFC_dungeon_gen.dao;

import WFC_dungeon_gen.domain.TileSet;
import com.google.gson.Gson;
import java.io.FileNotFoundException;

/**
 *
 * @author Juha Kauppinen
 */
public class TileSetTestData implements TileSetDao {

    @Override
    public TileSet loadTileSet(String file) throws FileNotFoundException {
        String testData = "{"
                + "  \"tiles\": ["
                + "    ["
                + "      [\".\", \"·\", \".\"],"
                + "      [\".\", \"·\", \".\"],"
                + "      [\".\", \"·\", \".\"]"
                + "  ],"
                + "  ["
                + "      [\".\", \".\", \".\"],"
                + "      [\"H\", \"H\", \"H\"],"
                + "      [\".\", \".\", \".\"]"
                + "  ],"
                + "  ["
                + "      [\".\", \".\", \".\"],"
                + "      [\"H\", \"H\", \".\"],"
                + "      [\".\", \"H\", \".\"]"
                + "  ],"
                + "  ["
                + "      [\".\", \"H\", \".\"],"
                + "      [\".\", \"H\", \"H\"],"
                + "      [\".\", \".\", \".\"]"
                + "  ]"
                + "  ],"
                + "  \"allowedTilesUp\": ["
                + "    \"1010\","
                + "    \"0101\","
                + "    \"0101\","
                + "    \"1010\""
                + "  ],"
                + "  \"allowedTilesRight\": ["
                + "    \"1001\","
                + "    \"0110\","
                + "    \"1001\","
                + "    \"0110\""
                + "  ],"
                + "  \"allowedTilesDown\": ["
                + "    \"1001\","
                + "    \"0110\","
                + "    \"1001\","
                + "    \"0110\""
                + "  ],"
                + "  \"allowedTilesLeft\": ["
                + "    \"1010\","
                + "    \"0101\","
                + "    \"0101\","
                + "    \"1010\""
                + "  ],"
                + "  \"tileWeights\": ["
                + "    1,"
                + "    2,"
                + "    3,"
                + "    4"
                + "  ]"
                + "}";
        Gson GSON = new Gson();
        TileSet tileSet = GSON.fromJson(testData, TileSet.class);
        
        return tileSet;
    }

}
