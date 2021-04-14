package WFC_dungeon_gen.ui;

/**
 *
 * @author Juha Kauppinen
 */
import WFC_dungeon_gen.dao.TileSetDao;
import WFC_dungeon_gen.dao.TileSetJsonDao;
import WFC_dungeon_gen.domain.Solver;
import WFC_dungeon_gen.domain.TileSet;
import java.io.FileNotFoundException;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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
    private int[] tileWeights;
    private boolean setLoaded;
    private int tileWidth;
    private Solver dungeon;

    @Override
    public void init() {
        this.dao = new TileSetJsonDao();
        this.setLoaded = loadTileSet("./data/dungeon_trivial.JSON");
        this.mapWidth = 20;
        this.mapDepth = 15;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.window = stage;
        window.setTitle("Wave Function Collapse Dungeon Generator");

        HBox btn_pane = new HBox();
        Button btn_generate = new Button("Generate");
        btn_generate.setOnAction(e -> {
            generateMap();
        });

        Button btn_clear = new Button("Clear");
        btn_clear.setOnAction(e -> {
            clear();
        });
        
        Button btn_step = new Button("Step");
        btn_step.setOnAction(e -> {
            step();
        });
        
        btn_pane.getChildren().add(btn_generate);
        btn_pane.getChildren().add(btn_clear);
        btn_pane.getChildren().add(btn_step);

        VBox vbox = new VBox();
        this.textArea = new TextArea();

        textArea.setPrefColumnCount(mapWidth * this.tileWidth);
        textArea.setPrefRowCount(mapDepth * this.tileWidth);
        textArea.setFont(Font.loadFont("file:data/square.ttf", 16));

        vbox.getChildren().add(btn_pane);
        vbox.getChildren().add(textArea);

        if (setLoaded) {
            int numberOfTiles = this.tileSet.getNumberOfTiles();
            boolean[][][] rules = this.tileSet.getAdjacencyRules();

            this.dungeon = new Solver(mapWidth, mapDepth, numberOfTiles, this.tileWeights, rules);
            generateMap();
            displayMap();
        }

        Scene scene = new Scene(vbox);
        window.setScene(scene);
        window.show();
    }

    private void generateMap() {
        this.dungeon.initMap();
        this.map = this.dungeon.generateMap();
        displayMap();
    }
    
    private void clear() {
        this.dungeon.initMap();
        this.map = dungeon.getMap();
        displayMap();
    }
    
    private void step() {
        this.dungeon.step();
        this.map = dungeon.getMap();
        displayMap();
    }

    private void displayMap() {
        String output = "";

        for (int row = 0; row < this.mapDepth; row++) {
            for (int j = 0; j < this.tileWidth; j++) {
                for (int col = 0; col < this.mapWidth; col++) {
                    int tileNum = this.map[row][col];
                    for (int i = 0; i < this.tileWidth; i++) {
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
            this.tiles = tileSet.getTiles();
            this.tileWeights = tileSet.getTileWeights();
            this.tileWidth = this.tiles[0].length;
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
