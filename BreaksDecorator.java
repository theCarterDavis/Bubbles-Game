import javafx.scene.canvas.GraphicsContext;

public class BreaksDecorator extends TileDecorator {
    private double breakAfterTime; // Time in seconds before the tile breaks
    private double timeStoodOn; // Time the player has been standing on the tile
    private boolean playerIsOnTile;
    private boolean isBroken;
    private long lastUpdateTime;

    public BreaksDecorator(TileComponent tile, double breakAfterTime) {
        super(tile);
        this.breakAfterTime = breakAfterTime;
        this.timeStoodOn = 0;
        this.playerIsOnTile = false;
        this.isBroken = false;
        this.lastUpdateTime = System.nanoTime();
    }

    @Override
    public void applyEffect(Player player) {
        if (isBroken) {
            return;
        }

        // More lenient intersection check
        boolean currentlyIntersecting = intersects(player) && isPlayerOnTop(player);

        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convert to seconds
        lastUpdateTime = currentTime;

        if (currentlyIntersecting) {
            if (!playerIsOnTile) {
                // Player just started standing on the tile
                playerIsOnTile = true;
            }

            timeStoodOn += deltaTime;

            if (timeStoodOn >= breakAfterTime) {
                isBroken = true;
                setActive(false);
                return;
            }
        } else {
            playerIsOnTile = false;
            // Optional: Uncomment the following line if you want the timer to reset when player steps off
            // timeStoodOn = 0;
        }

        if (!isBroken) {
            super.applyEffect(player);
        }
    }

    private boolean isPlayerOnTop(Player player) {
        double playerBottom = player.getY() + player.getSize();
        double tileTop = getY();
        double tolerance = 5.0; // Slightly more forgiving tolerance

        // Check if player is above the tile and close enough to be considered "on" it
        return playerBottom >= tileTop - tolerance &&
                playerBottom <= tileTop + tolerance &&
                player.getY() < tileTop;
    }

    @Override
    public boolean intersects(Player player) {
        if (isBroken) {
            return false;
        }
        return super.intersects(player);
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (!isBroken) {
            decoratedTile.draw(gc);
        }
    }

    @Override
    public TileComponent clone() {
        BreaksDecorator clone = (BreaksDecorator) super.clone();
        clone.timeStoodOn = 0;
        clone.playerIsOnTile = false;
        clone.isBroken = false;
        clone.lastUpdateTime = System.nanoTime();
        return clone;
    }

    @Override
    public TileComponent cloneWithParameters(Object... params) {
        if (params.length >= 1 && params[0] instanceof Double) {
            return new BreaksDecorator(decoratedTile.clone(), (Double) params[0]);
        }
        return clone();
    }

    @Override
    public String toString() {
        return decoratedTile.toString();
    }

    @Override
    public int getRadius() {
        return decoratedTile.getRadius();
    }

    @Override
    public String getTexture() {
        return decoratedTile.getTexture();
    }
    public boolean isCollideable() {
        return decoratedTile.isCollideable();
    }

    public void setCollideable(boolean newC){ decoratedTile.setCollideable(newC); }
}