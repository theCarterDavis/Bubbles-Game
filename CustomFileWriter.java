import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CustomFileWriter {  // Changed class name from FileWriter to CustomFileWriter
    private String filePath, content;
    private boolean append;

    public void writeToFile(String filePath, TileComponent tile, boolean append) {  // Changed to accept Tile
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
            writer.write(tile.toString());  // Use tile's toString() method
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void writeLines(String filePath, List<TileComponent> tiles, boolean append) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, append))) {
            for (TileComponent tile : tiles) {  // Changed to iterate over Tile objects
                writer.print(tile.toString());  // Use toString() method of Tile
            }
        } catch (IOException e) {
            System.err.println("Error writing lines to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearFile(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            // Opening with false for append will truncate/clear the file
            // No need to write anything - just opening and closing it will clear it
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
