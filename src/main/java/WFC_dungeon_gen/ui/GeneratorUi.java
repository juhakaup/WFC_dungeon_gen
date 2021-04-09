package WFC_dungeon_gen.ui;

/**
 *
 * @author Juha Kauppinen
 */
import WFC_dungeon_gen.dao.TileSetDao;
import WFC_dungeon_gen.dao.TileSetJsonDao;
import WFC_dungeon_gen.dao.TileSetTestData;
import WFC_dungeon_gen.domain.Solver;
import WFC_dungeon_gen.domain.TileSet;
import java.io.FileNotFoundException;
import java.util.logging.Logger;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GeneratorUi extends Application {

    private int winWidth;
    private int winHeight;
    private Stage window;
    private TextArea textArea;

    private TileSetDao dao;
    private TileSet tileSet;
    private int mapWidth;
    private int mapDepth;
    private int[][] map;
    private char[][][] tiles;

    @Override
    public void init() {
        this.dao = new TileSetJsonDao();
        this.mapWidth = 15;
        this.mapDepth = 10;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.window = stage;
        window.setTitle("Wave Function Collapse Dungeon Generator");

        VBox vbox = new VBox();
        Button button = new Button("Generate");
        this.textArea = new TextArea();

        textArea.setPrefColumnCount(mapWidth * 3);
        textArea.setPrefRowCount(mapDepth * 3);
        //textArea.setFont(Font.font("Courier",FontWeight.NORMAL, 15));
        textArea.setFont(Font.loadFont("file:data/square.ttf", 16));

        //vbox.getChildren().add(button);
        vbox.getChildren().add(textArea);

        boolean setLoaded = loadTileSet("./data/dungeon_trivial.JSON");

        if (setLoaded) {
            int numberOfTiles = tileSet.getNumberOfTiles();
            int[] weights = tileSet.getTileWeights();
            boolean[][][] rules = tileSet.getAdjacencyRules();
            this.tiles = tileSet.getTiles();

            Solver dungeon = new Solver(mapWidth, mapDepth, numberOfTiles, weights, rules);
            this.map = dungeon.solveMaze();
            outputMap();
        }

        Scene scene = new Scene(vbox);
        window.setScene(scene);
        window.show();
    }

    private void outputMap() {
        String output = "";
        int tileWidth = tileSet.getTiles()[0].length;

        for (int row = 0; row < this.mapDepth; row++) {
            for (int j = 0; j < tileWidth; j++) {
                for (int col = 0; col < this.mapWidth; col++) {
                    int tileNum = map[row][col];
                    for (int i = 0; i < tileWidth; i++) {
                        output += (tiles[tileNum][j][i]);
                    }
                }
                output += "\n";
            }
        }
        this.textArea.setText(output);
    }

    private boolean loadTileSet(String file) {
        try {
            this.tileSet = this.dao.loadTileSet(file);
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        }
    }

    public static void main() {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
