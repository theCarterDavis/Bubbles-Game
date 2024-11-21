import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;


public class Client extends Application {
    private static GraphicsContext gc;
    private static Pane root;
    private static Scene scene;
    private static Canvas gameCanvas;

    public static int viewport_width = 800;
    public static int viewport_height = 450;
    public static int scene_width = 800;
    public static int scene_height = 450;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BUBBLES");

        root = new Pane();
        root.setStyle("-fx-background-color: black;");

        gameCanvas = new Canvas(scene_width, scene_height);
        gc = gameCanvas.getGraphicsContext2D();

        gameCanvas.setOnMousePressed(new MouseClickListener());



        scene = new Scene(root, scene_width, scene_height);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //give scene to loading screen
        LoadingScreen.setScene(scene);

        Client.loadLevelEditor();
    }


    public static class MouseClickListener implements EventHandler<MouseEvent>
    {
        public void handle(MouseEvent me)
        {
            //LevelEditor.mouseAction(me.getX(), me.getY(), gc);
        }
    }



    public static void loadLevelPlayer() {
        Client.clearScene();

        // Add canvas first
        root.getChildren().add(gameCanvas);

        // Initialize level player
        LevelPlayer.load(gc);

        // Add GUI pane
        Pane guiPane = LevelPlayer.getGUIPane();
        if (guiPane != null) {
            guiPane.setLayoutX(330); // X position
            guiPane.setLayoutY(1); // Y position
            root.getChildren().add(guiPane);
            System.out.println("GUI Pane added - Children count: " + root.getChildren().size());

            // Make buttons not focusable
            guiPane.lookupAll(".button").forEach(node -> {
                ((Button) node).setFocusTraversable(false);
            });
        }

        // Add key handlers to the root pane instead of scene
        root.setOnKeyPressed(e -> LevelPlayer.handleKeyPress(e.getCode()));
        root.setOnKeyReleased(e -> LevelPlayer.handleKeyRelease(e.getCode()));

        // Make root focusable and request focus
        root.setFocusTraversable(true);
        root.requestFocus();
    }

    public static void clearScene() {
        LevelPlayer.end();
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        root.getChildren().clear();
        // Remove any existing event handlers
        root.setOnKeyPressed(null);
        root.setOnKeyReleased(null);
        //gc = gameCanvas.getGraphicsContext2D();

    }


    // LEVEL EDITOR.
    public static void loadLevelEditor() {
        Client.clearScene();
        root.getChildren().add(gameCanvas);

        // Initialize the level editor
        LevelEditor.load(gc);

        // Add the main container with grid
        Pane mainContainer = LevelEditor.getMainContainer();
        root.getChildren().add(mainContainer);

        // Add tile selection GUI
        Pane tileEditorGui = LevelEditor.getTileGUIPane();
        if (tileEditorGui != null) {
            tileEditorGui.setLayoutX(721);
            tileEditorGui.setLayoutY(-8.5);
            root.getChildren().add(tileEditorGui);

            tileEditorGui.lookupAll(".button").forEach(node -> {
                ((Button) node).setFocusTraversable(false);
            });
        }

        // Add start/save GUI
        Pane ssEditorGui = LevelEditor.getSSGUIPane();
        if (ssEditorGui != null) {
            ssEditorGui.setLayoutX(285);
            ssEditorGui.setLayoutY(-9);
            root.getChildren().add(ssEditorGui);

            ssEditorGui.lookupAll(".button").forEach(node -> {
                ((Button) node).setFocusTraversable(false);
            });
        }

        // Set up key handling
        scene.setOnKeyPressed(e -> {
            LevelEditor.handleKeyPress(e.getCode());
            root.requestFocus();
        });

        scene.setOnKeyReleased(e -> {
            LevelEditor.handleKeyRelease(e.getCode());
        });

        root.setFocusTraversable(true);
        root.requestFocus();
    }

    public static Canvas getGameCanvas() {
        return gameCanvas;
    }

    public static GraphicsContext getGraphicsContext() {
        return gc;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
