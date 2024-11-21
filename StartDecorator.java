import javafx.scene.canvas.GraphicsContext;

public class StartDecorator extends TileDecorator {
    public StartDecorator(TileComponent tile) {
        super(tile);
    }

    @Override
    public void applyEffect(Player player) {
        // Call the decorated tile's effect first
        super.applyEffect(player);
    }

    @Override
    public String toString() {
        return decoratedTile.toString();
    }

    @Override
    public double getX() {
        return decoratedTile.getX();
    }

    @Override
    public double getY() {
        return decoratedTile.getY();
    }

    @Override
    public void draw(GraphicsContext gc) {
        decoratedTile.draw(gc);
    }

    @Override
    public String getTexture() {
        return decoratedTile.getTexture();
    }

    @Override
    public TileComponent clone() {
        return new StartDecorator(decoratedTile.clone());
    }

    @Override
    public void setPosition(double x, double y) {
        decoratedTile.setPosition(x, y);
        LevelPlayer.setStartCoords((int)decoratedTile.getX(),(int)decoratedTile.getY());
    }

    public int getRadius() {
        return decoratedTile.getRadius();  // Forward to decorated tile
    }

    @Override
    public TileComponent cloneWithParameters(Object... params) {
        // StartDecorator doesn't use parameters, so just call regular clone
        return clone();
    }
    @Override
    public boolean intersects(Player player) {
        return decoratedTile.intersects(player);
    }
    public boolean isCollideable() {
        return decoratedTile.isCollideable();
    }

    public void setCollideable(boolean newC){ decoratedTile.setCollideable(newC); }
}
