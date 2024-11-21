import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.effect.DropShadow;

public class LoadingScreen {
    private static StackPane loadingPane;
    private static Scene currentScene;
    private static StackPane originalRoot;

    public static void setScene(Scene scene) {
        currentScene = scene;
        if (scene.getRoot() instanceof StackPane) {
            originalRoot = (StackPane) scene.getRoot();
        } else {
            // Wrap the original root in a StackPane if it isnt one
            originalRoot = new StackPane(scene.getRoot());
            scene.setRoot(originalRoot);
        }
    }

    public static void start() {
        if (currentScene == null) {
            throw new IllegalStateException("Scene must be set using setScene() before calling start()");
        }

        loadingPane = new StackPane();
        loadingPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        loadingPane.setPrefWidth(800);
        loadingPane.setPrefHeight(450);

        Label loadingLabel = new Label("Saving...");
        loadingLabel.setFont(Font.font("Agency FB", 34));
        loadingLabel.setTextFill(Color.WHITE);
        loadingLabel.setTranslateY(-20);

        // Create spinning animation
        Group spinnerGroup = new Group();
        for (int i = 0; i < 8; i++) {
            Arc arc = new Arc(0, 0, 15, 15, i * 45, 30);
            arc.setType(ArcType.ROUND);
            arc.setFill(Color.WHITE);
            arc.setOpacity(1.0 - (i * 0.1));
            spinnerGroup.getChildren().add(arc);
        }
        spinnerGroup.setTranslateY(20);

        // Add rotation animation
        RotateTransition rotation = new RotateTransition(Duration.seconds(1), spinnerGroup);
        rotation.setByAngle(360);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.play();

        loadingPane.getChildren().addAll(loadingLabel, spinnerGroup);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        dropShadow.setRadius(10);
        spinnerGroup.setEffect(dropShadow);

        // Prevent clicks from passing through
        loadingPane.setMouseTransparent(false);
        loadingPane.setPickOnBounds(true);

        originalRoot.getChildren().add(loadingPane);
    }

    public static void stop() {
        if (loadingPane != null && originalRoot != null) {
            originalRoot.getChildren().remove(loadingPane);
            loadingPane = null;
        }
    }
}