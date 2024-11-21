import javafx.scene.canvas.GraphicsContext;
public class GameCamera {
    private double x = 0;
    private double y = 0;

    // change these to adjust deadzone.
    private double deadZoneX = 220;   //size of deadzonex
    private double deadZoneY = 90;   // size of deadzoney
    private final int viewportWidth;        // Visible area width
    private final int viewportHeight;       // Visible area height
    private final int levelWidth;           // Total level width
    private final int levelHeight;          // Total level height

    public GameCamera(int viewportWidth, int viewportHeight, int levelWidth, int levelHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
    }

    public void update(double playerX, double playerY) {
        // Calculate the edges of the dead zone in world coordinates
        double leftDeadZone = x + (viewportWidth / 2) - deadZoneX;
        double rightDeadZone = x + (viewportWidth / 2) + deadZoneX;
        double topDeadZone = y + (viewportHeight / 2) - deadZoneY;
        double bottomDeadZone = y + (viewportHeight / 2) + deadZoneY;

        // Update camera X position
        if (playerX < leftDeadZone) {
            x = playerX - (viewportWidth / 2) + deadZoneX;
        }
        else if (playerX+30 > rightDeadZone) {
            x = playerX+30 - (viewportWidth / 2) - deadZoneX;
        }

        // Update camera Y position
        if (playerY < topDeadZone) {
            y = playerY - (viewportHeight / 2) + deadZoneY;
        }
        else if (playerY+30 > bottomDeadZone) {
            y = playerY+30 - (viewportHeight / 2) - deadZoneY;
        }


    }

    // Apply camera transform to GraphicsContext
    public void apply(GraphicsContext gc) {
        gc.save(); // Save the current state
        gc.translate(-x, -y); // Apply camera offset
    }

    // Restore original transform
    public void restore(GraphicsContext gc) {
        gc.restore();
    }

    // Convert screen coordinates to world coordinates
    public double screenToWorldX(double screenX) {
        return screenX + x;
    }

    public double screenToWorldY(double screenY) {
        return screenY + y;
    }

    // Convert world coordinates to screen coordinates
    public double worldToScreenX(double worldX) {
        return worldX - x;
    }

    public double worldToScreenY(double worldY) {
        return worldY - y;
    }

    // Getters for camera position
    public double getX() { return x; }
    public double getY() { return y; }

    public double getLeftDeadzone() { return x + (viewportWidth / 2) - deadZoneX; }
    public double getRightDeadzone() { return x + (viewportWidth / 2) + deadZoneX; }
    public double getTopDeadzone() { return y + (viewportHeight / 2) - deadZoneY;}
    public double getBottomDeadzone() { return y + (viewportHeight / 2) + deadZoneY; }



}