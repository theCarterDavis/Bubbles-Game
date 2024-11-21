import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.event.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.nio.file.Paths;
import java.util.*;


public class LevelEditor {
    private static Pane tileGUIPane;
    private static Pane ssGUIPane;
    private static AnimationTimer gameLoop;
    private static AnimationTimer matrixTimer;
    private static String tileType = "NULL";
    private static Pane mainContainer;
    private static GraphicsContext gc;
    private static final String saveFile = "BuildFile.txt";
    private static final String playFile = "PlayFile.txt";
    private static final String atributeFile = "atributes.txt";


    // Window and cell constants
    private static final int CELL_SIZE = 30;
    private static final int WINDOW_WIDTH = 810;
    private static final int WINDOW_HEIGHT = 450;
    private static final int GRID_ROWS = (WINDOW_HEIGHT / CELL_SIZE) + 3;
    private static final int GRID_COLS = (WINDOW_WIDTH / CELL_SIZE) + 3;

    // Chunk constants
    private static final int CHUNK_SIZE = 16;

    // Matrix effect fields
    private static final List<MatrixColumn> matrixColumns = new ArrayList<>();
    private static final Random random = new Random();

    // Viewport and movement
    private static double viewportX = 0;
    private static double viewportY = 0;
    private static final double SCROLL_SPEED = 15.0;
    private static final Set<KeyCode> activeKeys = new HashSet<>();

    // Track current tile under mouse
    private static Rectangle currentTile = null;
    // Track placed tiles in world coordinates

    private static final Map<String, TileComponent> tilePrototypes = FileReader.initializeTilePrototypes(atributeFile);
    private static final CoordinateTileMap worldTiles = FileReader.loadTiles(saveFile);

    private static CustomFileWriter write = new CustomFileWriter();

    private static boolean deleteMode = true;
    private static String tileName;
    private static String tilePNG = "brick1.png";


    private static class TileData {
        int type;

        TileData(int type) {
            this.type = type;
        }
    }

    private static class WorldPosition {
        int worldX, worldY;

        WorldPosition(int worldX, int worldY) {
            this.worldX = worldX;
            this.worldY = worldY;
        }

        @Override
        public String toString() {
            return worldX + "," + worldY;
        }

        static WorldPosition fromViewport(double viewX, double viewY) {
            int worldX = (int) Math.floor(viewX / CELL_SIZE);
            int worldY = (int) Math.floor(viewY / CELL_SIZE);
            return new WorldPosition(worldX, worldY);
        }
    }


    private static class GridPosition {
        int col, row;

        GridPosition(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }

    public static void load(GraphicsContext graphicsContext) {
        gc = graphicsContext;
        mainContainer = new Pane();
        mainContainer.setPickOnBounds(false);

        initializeMatrix();
        createGrid();
        loadGUIs();
        initializeGameLoop();
    }

    //LOAD THE MATRIX BACKGROUND
    private static void initializeMatrix() {
        matrixColumns.clear();
        int columnWidth = 20;
        for (int i = 0; i < WINDOW_WIDTH / columnWidth; i++) {
            matrixColumns.add(new MatrixColumn(
                    i * columnWidth,
                    random.nextInt(WINDOW_HEIGHT),
                    columnWidth,
                    WINDOW_HEIGHT
            ));
        }
        if (matrixTimer == null) {
            matrixTimer = new AnimationTimer() {
                private long lastUpdate = 0;
                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= 50_000_000) {
                        renderMatrix();
                        lastUpdate = now;
                    }
                }
            };
            matrixTimer.start();
        }
    }
    private static void renderMatrix() {
        gc.setFill(Color.color(0.1, 0.1, 0.1, 0.95));
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        for (MatrixColumn column : matrixColumns) {
            column.render(gc);
        }
    }

    // DRAW GRID + CELLS
    private static void createGrid() {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.TRANSPARENT);
                cell.setStroke(Color.rgb(6, 180, 80, 0.5));
                cell.setStrokeWidth(1);

                cell.setUserData(new GridPosition(col, row));

                cell.setOnMouseEntered(e -> mouseEnter(cell));
                cell.setOnMouseExited(e -> mouseExit(cell));
                cell.setOnMouseClicked(e -> mouseAction(cell));

                mainContainer.getChildren().add(cell);
                updateCellPosition(cell);
            }
        }
    }

    // main animation for moving + clicking ect
    private static void initializeGameLoop() {

        if (gameLoop == null ) {
            gameLoop = new AnimationTimer() {
                private long lastUpdate = 0;

                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= 16_666_666) {
                        updateViewport();
                        updateGrid();
                        lastUpdate = now;
                    }
                }
            };
            gameLoop.start();
        }
    }

    private static void loadGUIs() {
        GUI sgui = new GUI();
        ssGUIPane = sgui.load('s');

        GUI egui = new GUI();
        tileGUIPane = egui.load('e');
    }


    private static void updateCellPosition(Rectangle cell) {
        GridPosition pos = (GridPosition) cell.getUserData();
        double offsetX = (viewportX % CELL_SIZE + CELL_SIZE) % CELL_SIZE;
        double offsetY = (viewportY % CELL_SIZE + CELL_SIZE) % CELL_SIZE;

        double newX = (pos.col * CELL_SIZE) - offsetX;
        double newY = (pos.row * CELL_SIZE) - offsetY;

        // Only wrap if beyond the first chunk
        if (viewportX >= CHUNK_SIZE * CELL_SIZE) {
            if (newX < -CELL_SIZE) {
                newX += (GRID_COLS * CELL_SIZE);
                pos.col += GRID_COLS;
            } else if (newX > WINDOW_WIDTH + CELL_SIZE) {
                newX -= (GRID_COLS * CELL_SIZE);
                pos.col -= GRID_COLS;
            }
        }

        if (viewportY >= CHUNK_SIZE * CELL_SIZE) {
            if (newY < -CELL_SIZE) {
                newY += (GRID_ROWS * CELL_SIZE);
                pos.row += GRID_ROWS;
            } else if (newY > WINDOW_HEIGHT + CELL_SIZE) {
                newY -= (GRID_ROWS * CELL_SIZE);
                pos.row -= GRID_ROWS;
            }
        }

        cell.setX(newX);
        cell.setY(newY);

        int worldX = (int) (Math.floor(viewportX / CELL_SIZE) + pos.col);
        int worldY = (int) (Math.floor(viewportY / CELL_SIZE) + pos.row);
        WorldPosition worldPos = new WorldPosition(worldX, worldY);
        //System.out.println(worldX+ " " + worldY);
        int testX = (int) viewportX/ 27 / CELL_SIZE;
        int testY = (int) viewportY/15 / CELL_SIZE;

        List<TileComponent> allChunkTiles = new ArrayList<>();
        for (int x = testX - 1; x <= testX + 1; x++) {
            for (int y = testY - 1; y <= testY + 1; y++) {
                List<TileComponent> chunk = worldTiles.get(new int[] {x, y});
                if (chunk != null) {
                    allChunkTiles.addAll(chunk);
                }
            }
        }


// Then use allChunkTiles for your coordinate check
        /*
        boolean matchFound = false;
        for (Tile tile : allChunkTiles) {

            if (tile.getX()/30 == worldX && tile.getY()/30 == worldY) {
                matchFound = true;
                break;
            }
        }

         */

        TileComponent matchedTile = null;
        for (TileComponent tile : allChunkTiles) {
            // System.out.println((int)tile.getX()/30 + " " + (int)tile.getY()/30);
            if ((int)tile.getX()/30 == worldX && (int)tile.getY()/30 == worldY) {
                matchedTile = tile;
                break;
            }
        }

        // Set cell appearance based on state
        if (matchedTile != null) {
            float[] colors = matchedTile.getColors();

            // Set the background color
            cell.setFill(Color.color(colors[0], colors[1], colors[2], colors[3]));

            // Apply texture if it exists
            String texture = matchedTile.getTexture();
            if (texture != null && !texture.isEmpty()) {
                Image tileImage = getImageForTile(texture);
                if (tileImage != null) {
                    cell.setFill(new ImagePattern(tileImage));
                }
            }

            // Set border color - yellow if hovered, otherwise use tile's border color
            if (cell == currentTile) {
                cell.setStroke(Color.YELLOW);
                cell.setStrokeWidth(2);

                // If in delete mode, make the existing tile semi-transparent
                if (deleteMode) {
                    cell.setOpacity(0.5);
                } else {
                    cell.setOpacity(1.0);
                }
            } else {
                cell.setStroke(Color.color(colors[4], colors[5], colors[6], colors[7]));
                cell.setStrokeWidth(1);
                cell.setOpacity(1.0);
            }
        } else {
            // Empty cell
            cell.setFill(Color.TRANSPARENT);
            cell.setOpacity(1.0);

            // Show preview of tile to be placed when hovering over empty cell
            if (cell == currentTile && !deleteMode && tilePNG != null) {
                Image previewImage = getImageForTile(tilePNG);
                if (previewImage != null) {
                    ImagePattern pattern = new ImagePattern(previewImage);
                    cell.setFill(pattern);
                    cell.setOpacity(0.5);
                }
            }

            // Set border color - yellow if hovered, otherwise default green
            if (cell == currentTile) {
                cell.setStroke(Color.YELLOW);
                cell.setStrokeWidth(2);
            } else {
                cell.setStroke(Color.rgb(6, 180, 80, 0.5));
                cell.setStrokeWidth(0.5);
            }
        }
    }
    private static final Map<String, Image> imageCache = new HashMap<>();

    // Caching method for loading and retrieving images
    private static Image getImageForTile(String tilePNG) {
        // Use computeIfAbsent to load the image only once and cache it
        return imageCache.computeIfAbsent(tilePNG, png -> {
            // Construct the file path safely
            String filePath = Paths.get("assets", png).toString();
            return new Image(filePath); // Load and cache the image
        });
    }



    private static void updateGrid() {
        mainContainer.getChildren().forEach(node -> {
            if (node instanceof Rectangle cell) {


                updateCellPosition(cell);


            }
        });
    }


    // update from wasd movement
    private static void updateViewport() {
        double deltaX = 0;
        double deltaY = 0;

        if (activeKeys.contains(KeyCode.W))deltaY -= SCROLL_SPEED;
        if (activeKeys.contains(KeyCode.S)) deltaY += SCROLL_SPEED;
        if (activeKeys.contains(KeyCode.A)) deltaX -= SCROLL_SPEED;
        if (activeKeys.contains(KeyCode.D)) deltaX += SCROLL_SPEED;

        if (deltaX != 0 && deltaY != 0) {
            deltaX *= 0.707;
            deltaY *= 0.707;
        }

        viewportX += deltaX;
        viewportY += deltaY;
    }



    // HANDLEING CELLS WITH MOUSE ACTIVITY
    public static void mouseEnter(Rectangle cell) {
        GridPosition pos = (GridPosition) cell.getUserData();
        int worldX = (int)(Math.floor(viewportX / CELL_SIZE) + pos.col);
        int worldY = (int)(Math.floor(viewportY / CELL_SIZE) + pos.row);

        currentTile = cell;
        cell.setStroke(Color.YELLOW);
        cell.setStrokeWidth(2);

        // Show preview of tile to be placed when not in delete mode
        if (!deleteMode && tilePNG != null) {
            Image previewImage = getImageForTile(tilePNG);
            if (previewImage != null) {
                ImagePattern pattern = new ImagePattern(previewImage);
                // Create a semi-transparent preview
                cell.setFill(pattern);
                cell.setOpacity(0.5);
            }
        }
    }

    public static void mouseAction(Rectangle cell) {
        GridPosition pos = (GridPosition) cell.getUserData();

        int worldX = (int)(Math.floor(viewportX / CELL_SIZE) + pos.col);
        int worldY = (int)(Math.floor(viewportY / CELL_SIZE) + pos.row);
        //WorldPosition worldPos = new WorldPosition(worldX, worldY);

        if(deleteMode) { //delete tile

            int testX = (int) viewportX / 27 / CELL_SIZE;
            int testY = (int) viewportY / 15 / CELL_SIZE;

            for (int x = testX - 1; x <= testX + 1; x++) {
                for (int y = testY - 1; y <= testY + 1; y++) {

                    if (worldTiles.containsCoordinate(new int[]{testX, testY})) {
                        worldTiles.deleteTile(new int[]{testX, testY}, new BaseTile(worldX * 30, worldY * 30));
                        //System.out.println("DELETE RAN");
                        cell.setFill(Color.TRANSPARENT);
                        cell.setOpacity(1);

                        if (!worldTiles.containsCoordinate(new int[]{worldX, worldY})) {
                            //worldTiles.deleteTile(new int[]{(int) viewportX / 27 / CELL_SIZE, (int) viewportY / 15 / CELL_SIZE}, new BaseTile(worldX * 30, worldY * 30));
                            //System.out.println("412\n"+cell.getFill());
                        }

                    }
                }
            }
        } else { //add tile

            int testX = (int)viewportX/27/CELL_SIZE;
            int testY = (int)viewportY/15/CELL_SIZE;

            for (int x = testX - 1; x <= testX + 1; x++) {
                for (int y = testY - 1; y <= testY + 1; y++) {

                    if (worldTiles.containsCoordinate(new int[]{testX, testY})) {
                        worldTiles.deleteTile(new int[]{testX, testY}, new BaseTile(worldX * 30, worldY * 30));
                        //System.out.println("DELETE RAN");
                        cell.setFill(Color.TRANSPARENT);
                        cell.setOpacity(1);

                        if (!worldTiles.containsCoordinate(new int[]{worldX, worldY})) {
                            //worldTiles.deleteTile(new int[]{(int) viewportX / 27 / CELL_SIZE, (int) viewportY / 15 / CELL_SIZE}, new BaseTile(worldX * 30, worldY * 30));
                            //System.out.println("412\n"+cell.getFill());
                        }
                    }
                }
            }
            //cell.setFill(Color.GREY);
            cell.setOpacity(1);
            // Load the PNG image


            Image image = new Image("assets/"+tilePNG);

            // Set the image as the fill for the rectangle
            cell.setFill(new ImagePattern(image));

            //System.out.println(tileName);

            TileComponent toAdd =  tilePrototypes.get(tileName).clone();

            toAdd.setPosition(worldX*30,worldY*30);

            worldTiles.addTile(new int[]{(int) viewportX / 27 / CELL_SIZE, (int) viewportY / 15 / CELL_SIZE}, toAdd);
        }
    }

    public static void mouseExit(Rectangle cell) {
        if (currentTile == cell) {
            currentTile = null;
        }
        cell.setOpacity(1.0); // Reset opacity
        updateCellPosition(cell);
    }




    public static void handleKeyPress(KeyCode code) {
        activeKeys.add(code);
    }

    public static void handleKeyRelease(KeyCode code) {
        activeKeys.remove(code);
    }


    public static void handleEditorButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        tileType = button.getText();

        switch (button.getText()) {
            case "SAVE":

                if (worldTiles.size() >=2000){
                    //throw a loading screen that turns off input
                    LoadingScreen.start();

                    new Thread(() -> {
                        //save the map state
                        write.clearFile(saveFile);
                        for (int[] coordinate : worldTiles.getCoordinates()) {
                            List<TileComponent> tilesAtThisSpot = worldTiles.get(coordinate);

                            // Print or process each coordinate and its tiles
                            //System.out.println("At coordinate [" + coordinate[0] + "," + coordinate[1] + "]:");
                            for (TileComponent tile : tilesAtThisSpot) {
                                // Do something with each tile
                                // System.out.println(tile);
                                write.writeToFile(saveFile,tile,true);
                            }
                        }

                        // When done, update UI on JavaFX thread
                        Platform.runLater(() -> {
                            LoadingScreen.stop();
                        });
                    }).start();
                } else {
                    write.clearFile(saveFile);
                    for (int[] coordinate : worldTiles.getCoordinates()) {
                        List<TileComponent> tilesAtThisSpot = worldTiles.get(coordinate);

                        // Print or process each coordinate and its tiles
                        //System.out.println("At coordinate [" + coordinate[0] + "," + coordinate[1] + "]:");
                        for (TileComponent tile : tilesAtThisSpot) {
                            // Do something with each tile
                            // System.out.println(tile);
                            write.writeToFile(saveFile,tile,true);
                        }
                    }
                }
                break;
            case "PLAY":
                System.out.println("PLAY is called");
                if (worldTiles.size() >=2000) {
                    //throw a loading screen that turns off input
                    LoadingScreen.start();

                    new Thread(() -> {
                        //save the map state
                        write.clearFile(playFile);
                        for (int[] coordinate : worldTiles.getCoordinates()) {
                            List<TileComponent> tilesAtThisSpot = worldTiles.get(coordinate);

                            // Print or process each coordinate and its tiles
                            //System.out.println("At coordinate [" + coordinate[0] + "," + coordinate[1] + "]:");
                            for (TileComponent tile : tilesAtThisSpot) {
                                // Do something with each tile
                                // System.out.println(tile);
                                write.writeToFile(playFile, tile, true);
                            }
                        }

                        // When done, update UI on JavaFX thread
                        Platform.runLater(() -> {
                            LoadingScreen.stop();
                            Client.loadLevelPlayer();
                            LevelEditor.stopAnimations();
                        });
                    }).start();
                }else {
                    write.clearFile(playFile);
                    for (int[] coordinate : worldTiles.getCoordinates()) {
                        List<TileComponent> tilesAtThisSpot = worldTiles.get(coordinate);

                        // Print or process each coordinate and its tiles
                        //System.out.println("At coordinate [" + coordinate[0] + "," + coordinate[1] + "]:");
                        for (TileComponent tile : tilesAtThisSpot) {
                            // Do something with each tile
                            // System.out.println(tile);
                            write.writeToFile(playFile,tile,true);
                        }
                    }
                    Client.loadLevelPlayer();
                    LevelEditor.stopAnimations();
                }
                break;
        }
    }


    public static void toggleDel(){
        deleteMode = !deleteMode;
    }


    public static Pane getMainContainer() {
        return mainContainer;
    }

    public static Pane getSSGUIPane() {
        return ssGUIPane;
    }

    public static Pane getTileGUIPane() {
        return tileGUIPane;
    }

    public static void stopAnimations(){
        gameLoop.stop();
        matrixTimer.stop();
        gameLoop = null;
        matrixTimer = null;
        deleteMode = true;
    }

    public static void setDrawTile(String name, String PNG){
        tileName = name;
        tilePNG = PNG;
    }

}