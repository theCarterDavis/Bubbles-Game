import javafx.scene.canvas.GraphicsContext;

public class ActivateDecorator extends TileDecorator {
    private String targetTileName;
    private boolean isPlayerInContact = false;
    private long lastToggleTime = 0;
    private static final long TOGGLE_COOLDOWN = 450; // 500ms cooldown between toggles

    public ActivateDecorator(TileComponent tile, String targetTileName) {
        super(tile);
        this.targetTileName = targetTileName;
    }

    @Override
    public void applyEffect(Player player) {
        super.applyEffect(player);

        if (isPlayerDirectlyOnTile(player)) {
            if (isActive()) {
                long currentTime = System.currentTimeMillis();

                // Only allow toggling if enough time has passed since last toggle
                if (currentTime - lastToggleTime >= TOGGLE_COOLDOWN) {
                    toggleTargetTiles();
                    lastToggleTime = currentTime;
                }
            }
        }
    }

    private void toggleTargetTiles() {
        //System.out.println("Toggling tiles with name: " + targetTileName);
        boolean anyTilesToggled = false;
        System.out.println(BaseTile.getTiles());
        for (TileComponent tile : BaseTile.getTiles()) {

            if (tile.getName().equals(targetTileName)) {
                boolean previousState = tile.isActive();
                tile.setActive(!previousState);

                boolean previousColl = tile.isCollideable();
                tile.setCollideable(!previousColl);

                //System.out.println("Toggled tile from " + previousState + " to " + tile.isActive());
                anyTilesToggled = true;
            }
        }

        if (!anyTilesToggled) {
            //System.out.println("Warning: No tiles found with name: " + targetTileName);
        }
    }

    private boolean isPlayerDirectlyOnTile(Player player) {
        double playerBottom = player.getY() + player.getSize();
        double playerTop = player.getY();
        double playerLeft = player.getX();
        double playerRight = player.getX() + player.getSize();

        double tileTop = getY();
        double tileBottom = getY() + decoratedTile.getSize();
        double tileLeft = getX();
        double tileRight = getX() + decoratedTile.getSize();

        // Check any overlap between player and tile
        return playerBottom >= tileTop &&
                playerTop <= tileBottom &&
                playerRight >= tileLeft &&
                playerLeft <= tileRight;
    }

    @Override
    public boolean intersects(Player player) {
        // Delegate to decorated tile's intersection check
        return decoratedTile.intersects(player);
    }

    @Override
    public TileComponent clone() {
        return new ActivateDecorator(decoratedTile.clone(), targetTileName);
    }

    @Override
    public TileComponent cloneWithParameters(Object... params) {
        if (params.length > 0 && params[0] instanceof String) {
            return new ActivateDecorator(decoratedTile.clone(), (String)params[0]);
        }
        return clone();
    }

    // Forward other required methods to the decorated tile
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
    public void setPosition(double x, double y) {
        decoratedTile.setPosition(x, y);
    }

    @Override
    public int getRadius() {
        return decoratedTile.getRadius();
    }

    @Override
    public String getName() {
        return decoratedTile.getName();
    }

    @Override
    public boolean isActive() {
        return decoratedTile.isActive();
    }

    @Override
    public void setActive(boolean active) {
        decoratedTile.setActive(active);
    }

    public boolean isCollideable() {
        return decoratedTile.isCollideable();
    }

    public void setCollideable(boolean newC){ decoratedTile.setCollideable(newC); }
}