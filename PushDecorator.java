import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PushDecorator extends TileDecorator {
    private final int radius;
    private final double amount;
    private final boolean defaultStatus;

    public PushDecorator(TileComponent tile, int r, double am, boolean ds) {
        super(tile);
        this.radius = r * 15;  // Match the scale used in PullDecorator
        this.amount = am;
        this.defaultStatus = ds;
    }

    @Override
    public void applyEffect(Player player) {
        super.applyEffect(player);
        if(isActive()) {
            // Calculate centers
            double tileCenterX = getX() + getSize() / 2;
            double tileCenterY = getY() + getSize() / 2;
            double playerCenterX = player.getX() + player.getSize() / 2;
            double playerCenterY = player.getY() + player.getSize() / 2;

            // Calculate distance between centers
            double distance = Math.sqrt(
                    Math.pow(tileCenterX - playerCenterX, 2) +
                            Math.pow(tileCenterY - playerCenterY, 2)
            );

            // Only apply effect if within radius and not too close to center
            double minDistance = 5.0;
            if (distance <= radius && distance > minDistance) {
                // Calculate force magnitude with inverse square falloff
                double forceMagnitude = amount * Math.pow(1.0 - distance / radius, 2);

                // Calculate normalized direction vectors (opposite direction from PullDecorator)
                double dirX = (playerCenterX - tileCenterX) / distance;
                double dirY = (playerCenterY - tileCenterY) / distance;

                // Apply force to player with proper scaling
                double baseForce = 0.5;
                double forceX = dirX * forceMagnitude * baseForce;
                double forceY = dirY * forceMagnitude * baseForce;

                player.addForce(forceX, -forceY);  // Negative Y to match game coordinates
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        // First draw the base tile
        decoratedTile.draw(gc);

        // Optional: Add visual indicator for push radius
       /* if (isActive()) {
            gc.setFill(Color.RED);  // Different color from pull for visual distinction
            gc.setGlobalAlpha(0.2);
            gc.fillOval(getX() - radius + getSize()/2,
                    getY() - radius + getSize()/2,
                    radius * 2, radius * 2);
            gc.setGlobalAlpha(1.0);
        }*/
    }

    // Implement all required TileComponent interface methods
    @Override
    public double getX() {
        return decoratedTile.getX();
    }

    @Override
    public double getY() {
        return decoratedTile.getY();
    }

    @Override
    public double getSize() {
        return decoratedTile.getSize();
    }

    @Override
    public String getName() {
        return decoratedTile.getName();
    }

    @Override
    public void setPosition(double x, double y) {
        decoratedTile.setPosition(x, y);
    }

    @Override
    public String getTexture() {
        return decoratedTile.getTexture();
    }

    @Override
    public boolean intersects(Player player) {
        return decoratedTile.intersects(player);
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public boolean isActive() {
        return decoratedTile.isActive();
    }

    @Override
    public void setActive(boolean active) {
        decoratedTile.setActive(active);
    }

    @Override
    public TileComponent clone() {
        return new PushDecorator(decoratedTile.clone(), radius/6, amount, defaultStatus);
    }

    @Override
    public TileComponent cloneWithParameters(Object... params) {
        if (params.length >= 3 && params[0] instanceof Integer) {
            return new PushDecorator(
                    decoratedTile.clone(),
                    (Integer)params[0],
                    (Double)params[1],
                    (Boolean)params[2]
            );
        }
        return clone();
    }

    @Override
    public String toString() {
        return decoratedTile.toString();
    }
    public boolean isCollideable() {
        return decoratedTile.isCollideable();
    }

    public void setCollideable(boolean newC){ decoratedTile.setCollideable(newC); }
}