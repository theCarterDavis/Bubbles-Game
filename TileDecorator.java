public abstract class TileDecorator implements TileComponent {
    protected TileComponent decoratedTile;

    public TileDecorator(TileComponent decoratedTile) {
        this.decoratedTile = decoratedTile;
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
    public boolean intersects(Player player) {
        return decoratedTile.intersects(player);
    }

    @Override
    public void applyEffect(Player player) {
        decoratedTile.applyEffect(player);
    }

    @Override
    public TileComponent clone() {
        try {
            TileDecorator clone = (TileDecorator) super.clone();
            clone.decoratedTile = decoratedTile.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public abstract TileComponent cloneWithParameters(Object... params);


    @Override
    public String toString() {
        return decoratedTile.toString();
    }

    @Override
    public int getRadius() {
        return decoratedTile.getRadius();  // Base tiles have no radius
    }

    public double getAmount() {
        return decoratedTile.getAmount();  // Base tiles have no radius
    }

    @Override
    public boolean isActive() {
        return decoratedTile.isActive();
    }

    @Override
    public void setActive(boolean active) {
        decoratedTile.setActive(active);
    }

    public float[] getColors() {
        return decoratedTile.getColors();
    }
}