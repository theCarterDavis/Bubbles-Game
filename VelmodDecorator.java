import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class VelmodDecorator extends TileDecorator {
    private final double speedMultiplierX;
    private final double speedMultiplierY;
    private final int radius;

    public VelmodDecorator(TileComponent tile, int r, double valX, double valY) {
        super(tile);
        radius = r * 25; // Match the scale used in PushDecorator
        speedMultiplierX = valX;
        speedMultiplierY = valY;
    }

    @Override
    public void applyEffect(Player player) {
        super.applyEffect(player);
            // Calculate centers (same as PushDecorator)
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
                double forceMagnitude = Math.pow(1.0 - distance/radius, 2);

                // Convert velocity modifications to forces with distance-based scaling
                double forceX = speedMultiplierX * 0.4 * forceMagnitude;
                double forceY = speedMultiplierY * 0.4 * forceMagnitude;

                player.addForce(forceX, forceY);
            }
    }

    @Override
    public void draw(GraphicsContext gc) {
        decoratedTile.draw(gc);

        // Add visual indicator for velocity modification radius
        /*if (isActive()) {
            gc.setFill(Color.YELLOW); // Different color for velmod
            gc.setGlobalAlpha(0.2);
            gc.fillOval(getX() - radius + getSize()/2,
                    getY() - radius + getSize()/2,
                    radius * 2, radius * 2);
            gc.setGlobalAlpha(1.0);
        }*/
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
    public String getTexture() {
        return decoratedTile.getTexture();
    }

    @Override
    public TileComponent clone() {
        return new VelmodDecorator(decoratedTile.clone(), radius/15, speedMultiplierX, speedMultiplierY);
    }

    @Override
    public TileComponent cloneWithParameters(Object... params) {
        if (params.length >= 3 && params[0] instanceof Integer) {
            return new VelmodDecorator(
                    decoratedTile.clone(),
                    (Integer)params[0],
                    (double)params[1],
                    (double)params[2]
            );
        }
        return clone();
    }

    @Override
    public int getRadius() {
        return radius;
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
