import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.event.*;
import javafx.scene.control.Button;
import java.util.*;

public class LevelPlayer {
    private static Player player;
    
    //private static List<Tile> tiles;
    private static CoordinateTileMap tiles;
    private static boolean leftPressed = false;
    private static boolean rightPressed = false;
    private static boolean jumpPressed = false;
    private static AnimationTimer gameLoop;
    private static GameCamera camera;
    private static Pane guiPane;
    private static ArrayList<ShootingStars> starslist = new ArrayList<>();
    private static int counter = 0;

    // Constants for level boundaries
    //these will be determined from level file in future
    private static final int LEVEL_WIDTH = 2000;  // Adjust based on level size
    private static final int LEVEL_HEIGHT = 1000; // Adjust based on level size

    //size of scene.
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 450;
    private static int startX = 0;
    private static int startY = 0;


    // Shooting stars system
    private static List<ShootingStars> shootingStars = new ArrayList<>();
    private static List<ShootingStars> frontStars = new ArrayList<>();
    private static List<ShootingStars> middleStars = new ArrayList<>();
    private static List<ShootingStars> backStars = new ArrayList<>();
    private static final int MAX_FRONT_STARS = 10;
    private static final int MAX_MIDDLE_STARS = 10;
    private static final int MAX_BACK_STARS = 10;
    private static final int MAX_SHOOTING_STARS = 20;
    private static final Random random = new Random();
    private static long lastUpdate = 0;
    private static int starscounter = 0;





    public static void load(GraphicsContext gc) {
        camera = new GameCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, LEVEL_WIDTH, LEVEL_HEIGHT);
        tiles = FileReader.loadTiles("PlayFile.txt");
        guiPane = new GUI().load('p');
        player = new Player(startX, startY);
        starscounter = 0;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                update(deltaTime);
                draw(gc);
                lastUpdate = now;
            }
        };
        gameLoop.start();
    }


    public static void handleKeyPress(KeyCode code) {
        switch (code) {
            case A: leftPressed = true; break;
            case D: rightPressed = true; break;
            case SPACE: jumpPressed = true; break;
            default: break;
        }
    }

    public static void handleKeyRelease(KeyCode code) {
        switch (code) {
            case A: leftPressed = false; break;
            case D: rightPressed = false; break;
            case SPACE: jumpPressed = false; break;
            default: break;
        }
    }

    private static double lastCameraX = 0;
    private static double lastCameraY = 0;
    private static void update(double deltaTime) {
        // Convert key presses into a leftright value
        int leftright = 0;
        if (leftPressed) leftright = -1;
        if (rightPressed) leftright = 1;
        if (jumpPressed) player.jump();

        // Store old camera position
        double oldCameraX = camera.getX();
        double oldCameraY = camera.getY();

        // Update player with new physics parameters
        player.update(tiles, leftright, deltaTime);
        camera.update(player.getX(), player.getY());

        // Rest of the update method remains the same...
        double cameraVelocityX = camera.getX() - oldCameraX;
        double cameraVelocityY = camera.getY() - oldCameraY;


        
        // Initial stars spawn
        if (starscounter == 0) {
        int initialFrontStars = 7;
        int initialMiddleStars = 7;
        int initialBackStars = 7;
        
        for (int i = 0; i < initialFrontStars; i++) {
            frontStars.add(new ShootingStars(camera.getX(), camera.getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT, 0));
        }
        for (int i = 0; i < initialMiddleStars; i++) {
            middleStars.add(new ShootingStars(camera.getX(), camera.getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT, 0));
        }
        for (int i = 0; i < initialBackStars; i++) {
            backStars.add(new ShootingStars(camera.getX(), camera.getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT, 0));
        }
         starscounter++;
        }
        
        int sumofshootingstars = frontStars.size() + middleStars.size() + backStars.size();
        
        if(starscounter == 1 && sumofshootingstars < MAX_SHOOTING_STARS)
        {
            int choice = random.nextInt(3);
            switch(choice) {
               case 0:
                  frontStars.add(new ShootingStars(camera.getX(), camera.getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT, starscounter));
               break;
               case 1:
                  middleStars.add(new ShootingStars(camera.getX(), camera.getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT, starscounter));
               break;
               case 2:
                  backStars.add(new ShootingStars(camera.getX(), camera.getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT, starscounter));
               break;
            }
        }
        
        updateStarLayers(deltaTime, cameraVelocityX, cameraVelocityY);
        
        
        
        lastCameraX = camera.getX();
        lastCameraY = camera.getY();
    }
    

    private static void draw(GraphicsContext gc) {
        // Clear the viewport
        gc.clearRect(0, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);


        // Apply camera transform
        camera.apply(gc);

        // Draw shooting stars
        renderStarLayers(gc);

        // Draw tiles
        int[] chunk = player.getChunk();
        int xChunk = chunk[0];
        int yChunk = chunk[1];

        for (int x = xChunk - 1; x <= xChunk + 1; x++) {
            for (int y = yChunk - 1; y <= yChunk + 1; y++) {
                List<TileComponent> tilesAtSpot = tiles.get(new int[]{x, y});
                for (TileComponent tile : tilesAtSpot) {
                    tile.draw(gc);
                    tile.toString();

                    /*double distance = DistanceCalculator.getDistance(tile, player);


                    double distance = DistanceCalculator.getDistance(tile, player);
                    // Calculate centers

                    double tileCenterX = tile.getX() + 30 / 2;
                    double tileCenterY = tile.getY() + 30 / 2;
                    double playerCenterX = player.getX() + player.getSize() / 2;
                    double playerCenterY = player.getY() + player.getSize() / 2;*/


                    tile.applyEffect(player);



                }
            }
        }
        // Draw player
        player.draw(gc);

        // Restore camera transform
        camera.restore(gc);
    }








    // camera get methods
    public static double getCameraX() {
        return camera.getX();
    }

    public static double getCameraY() {
        return camera.getY();
    }

    public static GameCamera getCamera() {
        return camera;
    }




    private static void handlePlayerButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        String buttonText = button.getText();

        // button functionalities. these are just examples. no corresponding button currently available
        switch(buttonText) {
            case "PAUSE":
                // pause functionality
                break;
            case "RESTART":
                // restart functionality
                break;
            case "EXIT":
                // Handle exit functionality
                break;
        }
    }

    // get the GUI pane for adding to scene
    public static Pane getGUIPane() {
        return guiPane;
    }

    //end the animation or else it will always be drawn
    public static void end() {

        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;



        // Clear all shooting star lists
        frontStars.clear();
        middleStars.clear();
        backStars.clear();

        // Reset shooting star counter
        starscounter = 0;

        // Reset the last update time
        lastUpdate = 0;


        if(gameLoop != null){
            gameLoop.stop();
        }

    }


    //tile needs to call this before LevelPlayer is loaded
    public static void setStartCoords(int x, int y){
        startX = x;
        startY = y;
    }
    
      private static void updateStarLayers(double deltaTime, double cameraVelocityX, double cameraVelocityY) {
       // Scale velocities for parallax, ensuring all layers move in the same direction
       double adjustedVelocityXFront = cameraVelocityX * 0.25;  // Fastest layer
       double adjustedVelocityYFront = cameraVelocityY * 0.25;
   
       double adjustedVelocityXMiddle = cameraVelocityX * 0.5; // Middle layer
       double adjustedVelocityYMiddle = cameraVelocityY * 0.5;
   
       double adjustedVelocityXBack = cameraVelocityX * 0.75;   // Slowest layer
       double adjustedVelocityYBack = cameraVelocityY * 0.75;
   
   
       // Update each layer with consistent velocity scaling
       updateStarLayer(frontStars, MAX_FRONT_STARS, deltaTime, adjustedVelocityXFront, adjustedVelocityYFront);
       updateStarLayer(middleStars, MAX_MIDDLE_STARS, deltaTime, adjustedVelocityXMiddle, adjustedVelocityYMiddle);
       updateStarLayer(backStars, MAX_BACK_STARS, deltaTime, adjustedVelocityXBack, adjustedVelocityYBack);
      }

   private static void updateStarLayer(List<ShootingStars> stars, int maxStars, double deltaTime, double adjustedVelocityX, double adjustedVelocityY) {
    // Remove off-screen stars
    stars.removeIf(star -> star.isOffScreen(camera.getX(), camera.getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT));

    // Update existing stars with consistent parallax movement
    for (ShootingStars star : stars) {
        star.update(deltaTime, adjustedVelocityX, adjustedVelocityY);
     }
   }
   
   private static void renderStarLayers(GraphicsContext gc) {
    // Render back to front for proper layering
    for (ShootingStars star : backStars) {
        star.render(gc);
    }
    for (ShootingStars star : middleStars) {
        star.render(gc);
    }
    for (ShootingStars star : frontStars) {
        star.render(gc);
    }
  }
}
