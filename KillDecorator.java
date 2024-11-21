
import javafx.scene.canvas.GraphicsContext;

public class KillDecorator extends TileDecorator {
    public KillDecorator(TileComponent tile) {
        super(tile);
    }

    @Override
    public void applyEffect(Player player) {
        super.applyEffect(player);

        if(intersects(player)) {
            Client.loadLevelEditor();
            LevelPlayer.end();
        }
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
    public TileComponent cloneWithParameters(Object... params) {
        // StartDecorator doesn't use parameters, so just call regular clone
        return clone();
    }

    public int getRadius() {
        return decoratedTile.getRadius();  // Forward to decorated tile
    }

    public String getTexture(){
        return decoratedTile.getTexture();
    }

    @Override
    public boolean intersects(Player player) {
        // Check if the player's rectangle overlaps with the tile's rectangle
        return player.getX() < (getX() + getSize()) &&
                (player.getX() + player.getSize()) > getX() &&
                player.getY() < (getY() + getSize()) &&
                (player.getY() + player.getSize()) > getY();
    }

    public boolean isCollideable() {
        return decoratedTile.isCollideable();
    }

    public void setCollideable(boolean newC){ decoratedTile.setCollideable(newC); }
}

