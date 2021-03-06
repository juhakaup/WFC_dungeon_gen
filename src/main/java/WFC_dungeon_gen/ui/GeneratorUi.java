package WFC_dungeon_gen.ui;

/**
 * Wave function collapse dungeon generator user interface.
 * @author Juha Kauppinen
 */
import WFC_dungeon_gen.dao.TileSetDao;
import WFC_dungeon_gen.dao.TileSetJsonDao;
import WFC_dungeon_gen.domain.Solver;
import WFC_dungeon_gen.domain.TileSet;
import WFC_dungeon_gen.util.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

public class GeneratorUi extends Application {
    private Stage window;
    private TextArea textArea;
    private CheckBox chBoxSplitTiles;
    private CheckBox chBoxDistances;
    private TextField tfNumRows;
    private TextField tfNumCols;
    private Label notification;

    private TileSetDao dao;
    private TileSet tileSet;
    private int mapWidth;
    private int mapDepth;
    private int[][] map;
    private int[] distances;
    private int startingTile;
    private int endingTile;
    private int numberOfRetries;
    private String[][] tiles;
    private boolean setLoaded;
    private int tileWidth;
    private Solver dungeon;
    private Validator validator;
    private boolean validDungeon;
    private String file;
    private int fontSize;
    private int minWindowWidth;

    @Override
    public void init() {
        this.notification = new Label();
        this.dao = new TileSetJsonDao();
        this.file = "./data/dungeon_5x5.JSON";
        this.setLoaded = loadTileSet(this.file);
        this.mapWidth = 12;
        this.mapDepth = 6;
        this.numberOfRetries = 100;
        this.fontSize = 10;
        this.minWindowWidth = 900;
        this.startingTile = -1;
        this.endingTile = -1;
    }

    /**
     * Initializes the ui.
     * @param stage main stage
     * @throws Exception if there is error loading data
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.window = stage;
        window.setTitle("Wave Function Collapse Dungeon Generator");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("data"));
        Button chooseFile = new Button("Load tileset");  
        chooseFile.setOnAction(e -> {
            File newFile = fileChooser.showOpenDialog(stage);
            if (newFile != null) {
                if (loadTileSet(newFile.getAbsolutePath())) {
                    this.file = newFile.getAbsolutePath();
                    this.dungeon = new Solver(mapWidth, mapDepth, this.tileSet, true);
                }
            }
        });
        
        HBox btn_pane = createControls();
        HBox ctrl_pane = createSettings();
        HBox controls = new HBox();
        controls.getChildren().addAll(chooseFile, btn_pane, ctrl_pane);
        controls.setSpacing(20);

        VBox vbox = new VBox();
        this.textArea = new TextArea();

        vbox.getChildren().add(controls);
        vbox.getChildren().add(textArea);
        vbox.getChildren().add(this.notification);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 20, 10, 20));

        if (setLoaded) {
            generateMap();
        }

        updateWindowSize();  
        Scene scene = new Scene(vbox);
        window.setScene(scene);
        window.show();
    }
    
    /**
     * Creates the buttons for the ui.
     * @return HBox ui element
     */
    private HBox createControls() {
        HBox btn_pane = new HBox();
        Button btn_generate = new Button("Generate");
        btn_generate.setOnAction(e -> {
            this.numberOfRetries = 100;
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


        btn_pane.getChildren().addAll(btn_generate, btn_clear, btn_step);
        btn_pane.setSpacing(7);
        
        return btn_pane;
    }
    
    /**
     * Creates the user configurable settings for the ui
     * @return HBox user interface element.
     */
    private HBox createSettings() {
        HBox ctrl_pane = new HBox();
        
        this.chBoxSplitTiles = new CheckBox("Split tiles");
        this.chBoxSplitTiles.setOnAction(e -> {
            displayMap();
        });
        
        this.chBoxDistances = new CheckBox("Distances");
        this.chBoxDistances.setOnAction(e -> {
            displayMap();
        });

        HBox rowInput = new HBox();
        Label labelRows = new Label("Height");
        this.tfNumRows = new TextField(Integer.toString(mapDepth));
        this.tfNumRows.setPrefWidth(40);
        rowInput.getChildren().addAll(labelRows, tfNumRows);
        rowInput.setAlignment(Pos.CENTER);

        HBox colInput = new HBox();
        Label labelCols = new Label("Width");
        this.tfNumCols = new TextField(Integer.toString(mapWidth));
        this.tfNumCols.setPrefWidth(40);
        colInput.getChildren().addAll(labelCols, tfNumCols);
        colInput.setAlignment(Pos.CENTER);
        
        HBox fontSpinner = new HBox();
        Label labelFont = new Label("Font size");
        Spinner spfontSize = new Spinner(6, 22, this.fontSize);
        spfontSize.setPrefWidth(60);
        fontSpinner.getChildren().addAll(labelFont, spfontSize);
        fontSpinner.setAlignment(Pos.CENTER);
        
        spfontSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.fontSize = (int)newValue;
            updateWindowSize();
        });
        
        ctrl_pane.getChildren().addAll(colInput, rowInput, fontSpinner, chBoxSplitTiles, chBoxDistances);
        ctrl_pane.setSpacing(10);
        ctrl_pane.setAlignment(Pos.CENTER);
        
        return ctrl_pane;
    }

    /**
     * Changes the ui-window size according to the settings.
     */
    private void updateWindowSize() {
        this.textArea.setFont(Font.loadFont("file:data/square.ttf", this.fontSize));
        int tileSize = this.chBoxSplitTiles.isSelected() ? this.tileWidth + 1 : this.tileWidth;
        
        double actualFontSize = this.fontSize*1.055; // something like this

        textArea.setMinHeight(actualFontSize * tileSize * (mapDepth+2));
        textArea.setMinWidth(actualFontSize * tileSize * this.mapWidth);

        int textAreaWidth = (int) (actualFontSize * tileSize * mapWidth);
        this.window.setWidth((textAreaWidth < this.minWindowWidth) ? this.minWindowWidth : textAreaWidth);
        this.window.setHeight(actualFontSize * tileSize * mapDepth + 250);
    }

    /**
     * Initializes new dungeon map.
     */
    private void clear() {
        this.validDungeon = false;
        this.dungeon.initMap();
        this.map = dungeon.getMap();
        displayMap();
    }

    /**
     * Collapses single tile from the map.
     */
    private void step() {
        boolean finished = !this.dungeon.step();
        this.map = dungeon.getMap();
        if (finished) {
            this.validator = new Validator(map, tileSet);
            this.validator.validateMap();
            this.startingTile = this.validator.getStartingTile();
            this.endingTile = this.validator.getEndTile();
            this.validDungeon = this.validator.isValid();
            if (this.validDungeon) {
                this.distances = this.validator.getDistances();
            }
        }
        displayMap();
    }

    /**
     * Displays the map in the text area.
     */
    private void displayMap() {
        String output = "";
        String uninitialized = "";
        String empty = "";
        for (int i = 0; i < this.tileWidth; i++) {
            uninitialized += "/";
            empty += " ";
        }
        
        for (int row = 0; row < this.mapDepth; row++) {
            for (int j = 0; j < this.tileWidth; j++) {
                for (int col = 0; col < this.mapWidth; col++) {
                    int tileNum = this.map[row][col];
                    int tileIndex = row*this.mapWidth + col;

                    if (tileNum == -1) {
                        output += uninitialized;
                    } else if ((validDungeon && this.distances[row*this.mapWidth + col] == Integer.MAX_VALUE)) {
                        output += empty;
                    } else if (validDungeon && j == this.tileWidth / 2) {
                        char[] charArray = this.tiles[tileNum][j].toCharArray();
                        if (tileIndex == this.startingTile) {
                            charArray[this.tileWidth / 2] = '@';
                        } else if (tileIndex == this.endingTile) {
                            charArray[this.tileWidth / 2] = 'X';
                        } else if (this.chBoxDistances.isSelected()) { // distance to exit
                            int distance = this.distances[row*this.mapWidth + col];
                            String str = distance == Integer.MAX_VALUE ? "" : String.valueOf(distance);
                            int pos = this.tileWidth / 2;
                            for (int i=0; i<str.length(); i++) {
                                charArray[pos+i] = str.charAt(i);
                            }
                        }
                        output += new String(charArray);
                    } else {
                        output += this.tiles[tileNum][j];
                    }
                }
                output += "\n";
            }
        }
        if (this.chBoxSplitTiles.isSelected()) {
            this.textArea.setText(splitIntoGrid(output));
        } else {
            this.textArea.setText(output);
        }
    }
    
    /**
     * Adds spaces between tiles in the given map string.
     * @param mapString dungeon map
     * @return dungeon map as string
     */
    private String splitIntoGrid(String mapString) {
        String newOutput = "";
        int j = 0;
        int k = 0;
        for (int i = 0; i < mapString.length(); i++) {
            if (k == this.tileWidth) {
                newOutput += "\n";
                k = 0;
            }
            if (j % (this.tileWidth + 1) == 0) {
                newOutput += " ";
                j++;
            }
            newOutput += mapString.charAt(i);
            j++;
            if (mapString.charAt(i) == '\n') {
                j = 0;
                k++;
            }
        }
        return newOutput;
    }
    
    /**
     * Generates a new dungeon map and displays it in the text area.
     */
    private void generateMap() {
        int newRows = this.mapDepth;
        int newCols = this.mapWidth;
        try {
            newRows = Integer.parseInt(this.tfNumRows.getText());
            newCols = Integer.parseInt(this.tfNumCols.getText());
            if (newRows < 0 || newCols < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            this.notification.setText("Please enter a valid number");
            return;
        }

        this.mapWidth = newCols;
        this.mapDepth = newRows;
        updateWindowSize();

        if (this.setLoaded) {
            long startTime = System.nanoTime();
            this.dungeon = new Solver(mapWidth, mapDepth, this.tileSet, true);
            this.dungeon.initMap();
            this.map = this.dungeon.generateMap();
            double timeInterval = (double) (System.nanoTime() - startTime);
            this.notification.setText("Map generated in: " + timeInterval * 0.000001 + "ms.");
            this.validator = new Validator(map, tileSet);
            this.validator.validateMap();
            this.startingTile = this.validator.getStartingTile();
            this.endingTile = this.validator.getEndTile();
            this.validDungeon = this.validator.isValid();
            if (this.validDungeon) {
                this.distances = this.validator.getDistances();
            } else {
                if (this.numberOfRetries < 0) {
                    this.notification.setText("Error validating the map");
                } else {
                    this.numberOfRetries--;
                    generateMap();
                }
            }
            displayMap();
        } else {
            this.notification.setText("Error loading tileset");
        }
    }
    
    /**
     * Loads a new tileset
     * @param file file location
     * @return true if tileset was loaded false otherwise.
     */
    private boolean loadTileSet(String file) {
        try {
            TileSet newTileSet = this.dao.loadTileSet(file);
            if (newTileSet != null) {
                this.tileSet = newTileSet;
                this.tiles = tileSet.getTiles();
                this.tileWidth = this.tiles[0].length;
                this.notification.setText("New tileset loaded");
            } else {
                this.notification.setText("Error loading file");
            }
            return true;
        } catch (FileNotFoundException | NullPointerException ex) {
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
