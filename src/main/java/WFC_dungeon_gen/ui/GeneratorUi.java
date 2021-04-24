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
import WFC_dungeon_gen.util.Validator;
import java.io.FileNotFoundException;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Insets;
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
    private String[][] tiles;
    private boolean setLoaded;
    private int tileWidth;
    private Solver dungeon;
    private Validator validator;
    private String file;

    @Override
    public void init() {
        this.dao = new TileSetJsonDao();
        this.file = "./data/dungeon_trivial_2.JSON";
        this.setLoaded = loadTileSet(this.file);
        this.mapWidth = 15;
        this.mapDepth = 10;
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
        textArea.setPrefRowCount((int)(mapDepth*1.2) * this.tileWidth);
        textArea.setFont(Font.loadFont("file:data/square.ttf", 12));

        vbox.getChildren().add(btn_pane);
        vbox.getChildren().add(textArea);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 20, 10, 20)); 

        if (setLoaded) {
            this.dungeon = new Solver(mapWidth, mapDepth, this.tileSet, true);
            generateMap();
        }

        Scene scene = new Scene(vbox);
        window.setScene(scene);
        window.show();
    }

    private void generateMap() {
        this.setLoaded = loadTileSet(this.file);
        this.dungeon = new Solver(mapWidth, mapDepth, this.tileSet, true);
        this.dungeon.initMap();
        this.map = this.dungeon.generateMap();
        this.validator = new Validator(this.map, this.tileSet);
        validateMap();
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
    
    private void validateMap() {
        this.validator.canTraverse(0, 0, 1, 1);   
    }

    private void displayMap() {
        String output = "";
        for (int row = 0; row < this.mapDepth; row++) {
            for (int j = 0; j < this.tileWidth; j++) {
                for (int col = 0; col < this.mapWidth; col++) {
                    int tileNum = this.map[row][col];

                    if (tileNum == -1) {
                        output += "***";//this.tiles[0][0];
                    } else {
                        output += this.tiles[tileNum][j];
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
