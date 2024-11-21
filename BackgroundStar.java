// BackgroundStar.java
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class BackgroundStar {
    private double x, y, size, brightness;
    private static final Random random = new Random();
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 450;

    public BackgroundStar(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.brightness = random.nextDouble() * 0.5 + 0.5;
    }

    public void update(double cameraVelocityX, double cameraVelocityY, double parallaxFactor) {
        // Update position based on camera movement and parallax factor
        x += cameraVelocityX * parallaxFactor;
        y += cameraVelocityY * parallaxFactor;

        // Wrap around screen with proper viewport dimensions
        if (x < 0) x += VIEWPORT_WIDTH;
        if (x > VIEWPORT_WIDTH) x -= VIEWPORT_WIDTH;
        if (y < 0) y += VIEWPORT_HEIGHT;
        if (y > VIEWPORT_HEIGHT) y -= VIEWPORT_HEIGHT;

        // Random twinkling effect
        if (random.nextDouble() < 0.01) {
            brightness = random.nextDouble() * 0.5 + 0.5;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setGlobalAlpha(brightness);
        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, size, size);
        gc.setGlobalAlpha(1.0); // Reset alpha
    }
}