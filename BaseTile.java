import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class BaseTile implements TileComponent {
    private double x, y;
    private double size;
    private String name;
    private float[] colors;  // Array to store the 8 color values
    private String texture;  // Texture file name
    private boolean collideable; // Whether the tile can be collided with
    private boolean active = true;
    // Keep the static list if you need it for compatibility
    private static List<TileComponent> tiles = new ArrayList<>();

    // Existing constructors remain the same...
    public BaseTile(double x, double y) {
        this.x = x;
        this.y = y;
        this.size = 30;
        this.name = ""; // Default empty name
        this.colors = new float[8]; // Default empty colors
        this.texture = ""; // Default empty texture
        this.collideable = false; // Default non-collideable
    }

    public BaseTile(String name, double x, double y, double size, float[] colors, String texture, boolean collideable) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        this.colors = new float[8];
        System.arraycopy(colors, 0, this.colors, 0, 8);  // Deep copy the colors array
        this.texture = texture;
        this.collideable = collideable;
        tiles.add(this);
    }

    public BaseTile(String name, float[] colors, String texture, boolean collideable) {
        this.name = name;  // Name can be set later
        this.x = 0;
        this.y = 0;
        this.size = 30;  // Default size
        this.colors = new float[8];
        System.arraycopy(colors, 0, this.colors, 0, 8);
        this.texture = texture;
        this.collideable = collideable;
    }

    protected BaseTile(BaseTile other) {
        this.name = other.name;
        this.x = other.x;
        this.y = other.y;
        this.size = other.size;
        this.colors = new float[8];
        System.arraycopy(other.colors, 0, this.colors, 0, 8);
        this.texture = other.texture;
        this.collideable = other.collideable;
    }

    // Static method maintained for compatibility
    public static List<TileComponent> getTiles() {
        return tiles;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getSize() {
        return size;
    }

    @Override
    public String getName() {
        return name;
    }

    public float[] getColors() {
        return colors;
    }

    public String getTexture() {
        return texture;
    }

    public boolean isCollideable() {
        return collideable;
    }

    public void setCollideable(boolean newC){ collideable = newC;}

    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean intersects(Player player) {
        if (!collideable) return false;  // If not collideable, no intersection

        return player.getX() < (x + size) &&
                (player.getX() + player.getSize()) > x &&
                player.getY() < (y + size) &&
                (player.getY() + player.getSize()) > y;
    }

    @Override
    public TileComponent clone() {
        return new BaseTile(this);
    }

    @Override
    public TileComponent cloneWithParameters(Object... params) {
        // For BaseTile, we'll just return a regular clone since it doesn't need parameters
        return clone();
    }
    @Override
    public void applyEffect(Player player) {
        // Base tile has no special effect
    }

    public int getRadius() {
        return 0;  // Forward to decorated tile
    };
    public double getAmount(){return 0;}

    @Override
    public String toString() {
        return String.format(name + " " + (int) (x / 30) + " " + (int) (y / 30) + "\n");
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (texture == null || texture.isEmpty()) {
            return;
        }

        // Save the current state
        double originalAlpha = gc.getGlobalAlpha();

        try {
            // Draw the background rectangle with fill color
            Color fillColor = new Color(colors[0], colors[1], colors[2], colors[3]);
            gc.setFill(fillColor);
            gc.fillRect(x, y, size, size);

            // Draw the border with stroke color
            Color strokeColor = new Color(colors[4], colors[5], colors[6], colors[7]);
            gc.setStroke(strokeColor);
            gc.strokeRect(x, y, size, size);

            // Set transparency based on state
            if (!collideable || !isActive()) {
                gc.setGlobalAlpha(0.5);
            }

            // Draw the texture with additional transparency
            //gc.setGlobalAlpha(gc.getGlobalAlpha() * 0.1); // 10% of current alpha

            //gc.setGlobalAlpha(0.5);
            // Draw the texture using ImageRenderer
            ImageRenderer renderer = new ImageRenderer(x, y, 30, texture);

            renderer.draw(gc);

        } catch (Exception e) {
            // Fallback drawing with error indication
            gc.setGlobalAlpha(1.0);
            gc.setFill(Color.RED);
            gc.fillRect(x, y, size, size);
            gc.setStroke(Color.DARKRED);
            gc.strokeRect(x, y, size, size);

            System.err.println("Failed to load texture: " + texture);
            e.printStackTrace();
        } finally {
            // Always restore the original alpha value
            gc.setGlobalAlpha(originalAlpha);
        }
    }
    public static void clearTiles() {
        tiles.clear();
    }

    public static void addTile(TileComponent tile) {
        tiles.add(tile);
    }


}