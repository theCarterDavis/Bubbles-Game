import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MatrixColumn {
    private int x;
    private int y;
    private final int width;
    private final int height;
    private final List<Character> characters;
    private static final String MATRIX_CHARS = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃ1234567890";
    private final Random random = new Random();

    public MatrixColumn(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.characters = new ArrayList<>();

        // Initialize the list with random characters
        for (int i = 0; i < height / 20; i++) {
            characters.add(MATRIX_CHARS.charAt(random.nextInt(MATRIX_CHARS.length())));
        }
    }

    public void render(GraphicsContext gc) {
        y = (y + 20) % height; // Adjust position to create the falling effect

        for (int i = 0; i < characters.size(); i++) {
            int charY = (y - i * 20) % height;
            if (charY < 0) charY += height; // Wrap-around effect

            double opacity = 1.0 - (i * 0.1); // Fade out effect
            if (opacity < 0) opacity = 0;

            gc.setFill(Color.rgb(0, 110, 3, opacity));
            gc.fillText(String.valueOf(characters.get(i)), x, charY);

            // Occasionally change the character for a "flicker" effect
            if (random.nextDouble() < 0.05) {
                characters.set(i, MATRIX_CHARS.charAt(random.nextInt(MATRIX_CHARS.length())));
            }
        }
    }
}
