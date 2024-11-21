import javafx.scene.canvas.GraphicsContext;

public interface TileComponent extends Cloneable {
    double getX();
    double getY();
    double getSize();
    String getName();
    String getTexture();
    void setPosition(double x, double y);
    boolean intersects(Player player);
    void applyEffect(Player player);
    TileComponent clone();  // Keep the basic clone
    TileComponent cloneWithParameters(Object... params);  // Add new method for parameterized cloning
    String toString();
    default int getRadius() {return 0;}  // Default implementation returns 0
    default double getAmount() {return 0;}  // Default implementation returns 0
    void draw(GraphicsContext gc);
    boolean isActive();
    void setActive(boolean active);

    public float[] getColors();
     boolean isCollideable();

     void setCollideable(boolean newC);
}