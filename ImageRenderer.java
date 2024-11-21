import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class ImageRenderer {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private final double x;
    private final double y;
    private final double size;
    private final String texture;

    public ImageRenderer(double x, double y, double size, String texture) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.texture = texture;
    }

    public void draw(GraphicsContext gc) {
        if (texture != null && !texture.isEmpty()) {
            try {

                //gc.setGlobalAlpha(0.9);
                Image image = getImageForTile(texture);
                gc.drawImage(image, x, y, size, size);
                //gc.setGlobalAlpha(1);
            } catch (Exception e) {
                // If image loading fails, draw a fallback rectangle
                gc.setFill(Color.RED);
                gc.fillRect(x, y, size, size);
                System.err.println("Failed to load texture: " + texture + " - " + e.getMessage());
            }
        }
    }

    private static Image getImageForTile(String texturePath) {
        return imageCache.computeIfAbsent(texturePath, path -> {
            String resourcePath = "assets/" + path;
            // Using Class.getResource() to get the correct URL format
            String urlPath = ImageRenderer.class.getResource(resourcePath).toExternalForm();
            return new Image(urlPath);
        });
    }

    // Method to clear the cache if needed
    public static void clearCache() {
        imageCache.clear();
    }

    // Method to preload images
    public static void preloadImages(String... texturePaths) {
        for (String texturePath : texturePaths) {
            getImageForTile(texturePath);
        }
    }
}