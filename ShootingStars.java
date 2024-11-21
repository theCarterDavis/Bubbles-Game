import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class ShootingStars {
   private double x, y;
   private double speed;
   private double angle;
   private static final int TRAIL_LENGTH = 40;
   private Color starColor;
   private final double size;
   private static final Random random = new Random();
   private final SpawnLocation spawnLocation;
   private static final double PARALLAX_FACTOR = 1.15;

   private enum SpawnLocation {
      LEFT, RIGHT, TOP, BOTTOM, MIDDLE
   }

   public ShootingStars(double cameraX, double cameraY, int viewportWidth, int viewportHeight, int startype) {

      Color[] possibleColors = {
              Color.WHITE,
              Color.rgb(135, 206, 235), // Sky blue
              Color.rgb(255, 223, 186), // Peach
              Color.rgb(255, 182, 193)  // Light pink
      };
      starColor = possibleColors[random.nextInt(possibleColors.length)];

      // Force MIDDLE for first 5 instances, then random excluding MIDDLE
      if (startype == 0) {
         spawnLocation = SpawnLocation.MIDDLE;
      } else {
         // Create array of locations excluding MIDDLE
         SpawnLocation[] locations = {SpawnLocation.LEFT, SpawnLocation.RIGHT, SpawnLocation.TOP, SpawnLocation.BOTTOM};
         spawnLocation = locations[random.nextInt(locations.length)];
      }

      // Spawn stars close to the visible area
      switch (spawnLocation) {
         case LEFT:
            x = cameraX;  // Start at screen edge
            y = cameraY + random.nextDouble() * viewportHeight;
            angle = random.nextDouble() * Math.PI / 4 + Math.PI / 6;
            break;
         case RIGHT:
            x = cameraX + viewportWidth;  // Start at screen edge
            y = cameraY + random.nextDouble() * viewportHeight;
            angle = random.nextDouble() * Math.PI / 4 + Math.PI * 5 / 6;
            break;
         case BOTTOM:
            x = cameraX + random.nextDouble() * viewportWidth;
            y = cameraY + viewportHeight;  // Start at bottom edge
            angle = random.nextDouble() * Math.PI / 4 - Math.PI / 2; // Angle pointing upward
            break;
         case MIDDLE:
            x = cameraX + random.nextDouble() * viewportWidth;  // Start in the middle of the screen
            y = cameraY + random.nextDouble() * viewportHeight;
            angle = random.nextDouble() * Math.PI / 4 + Math.PI * 5 / 6;
            int anglechoice = random.nextInt(5); // Changed to 5 to include upward direction
            switch(anglechoice) {
               case 0:
                  angle = random.nextDouble() * Math.PI / 4 + Math.PI / 6;
                  break;
               case 1:
                  angle = random.nextDouble() * Math.PI / 4 + Math.PI / 2;
                  break;
               case 2:
                  angle = random.nextDouble() * Math.PI / 4 + Math.PI * 3 / 4;
                  break;
               case 3:
                  angle = random.nextDouble() * Math.PI / 4 + Math.PI * 5 / 6;
                  break;
               case 4:
                  angle = random.nextDouble() * Math.PI / 4 - Math.PI / 2; // Upward direction
                  break;
            }
            break;
         case TOP:
         default:
            x = cameraX + random.nextDouble() * viewportWidth;
            y = cameraY;  // Start at screen edge
            if (random.nextBoolean()) {
               angle = random.nextDouble() * Math.PI / 4 + Math.PI / 2;
            } else {
               angle = random.nextDouble() * Math.PI / 4 + Math.PI * 3 / 4;
            }
            break;
      }

      speed = random.nextDouble(3) * 3 + 5;
      size = random.nextDouble(1.5) * 2 + 2;
   }

   public void update(double deltaTime, double cameraVelocityX, double cameraVelocityY) {
      double dx = Math.cos(angle) * speed * deltaTime;
      double dy = Math.sin(angle) * speed * deltaTime;

      dx += cameraVelocityX * PARALLAX_FACTOR;
      dy += cameraVelocityY * PARALLAX_FACTOR;

      x += dx;
      y += dy;
   }

   public void render(GraphicsContext gc) {
      // Draw trail for slow-moving stars
      for (int i = 0; i < TRAIL_LENGTH; i++) {

         double trailX = x - Math.cos(angle) * i * 2;
         double trailY = y - Math.sin(angle) * i * 2;

         double alpha = Math.pow(1.0 - (double)i / TRAIL_LENGTH, 2);
         double trailSize = size * (1.0 - (double)i / TRAIL_LENGTH * 0.7);

         gc.setGlobalAlpha(alpha * 0.6);
         gc.setFill(starColor);

         gc.fillOval(trailX - trailSize/2, trailY - trailSize/2,
                 trailSize * 1.2, trailSize * 1.2);
      }

      gc.setGlobalAlpha(1.0);
      gc.setFill(starColor);
      gc.fillOval(x - size/2, y - size/2, size * 1.5, size * 1.5);
      gc.setGlobalAlpha(1.0);
   }

   public boolean isOffScreen(double cameraX, double cameraY, int viewportWidth, int viewportHeight) {
      return x > cameraX + viewportWidth + 200 ||
              y > cameraY + viewportHeight + 200 ||
              x < cameraX - 200 ||
              y < cameraY - 200;
   }
}