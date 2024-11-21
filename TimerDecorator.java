import javafx.scene.canvas.GraphicsContext;

public class TimerDecorator extends TileDecorator {
    private double intervalSeconds;
    private long startTime;
    private boolean isRunning;

    public TimerDecorator(TileComponent tile, double intervalSeconds) {
        super(tile);
        this.intervalSeconds = intervalSeconds;
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
        setActive(true); // Start with tile active
    }

    @Override
    public void applyEffect(Player player) {
        super.applyEffect(player);

        if (!isRunning) return;

        long currentTime = System.currentTimeMillis();
        double elapsedSeconds = (currentTime - startTime) / 1000.0;

        if (elapsedSeconds >= intervalSeconds) {
            // Toggle the active state
            setActive(!isActive());
            startTime = currentTime; // Reset timer
        }
    }

    @Override
    public TileComponent clone() {
        return new TimerDecorator(decoratedTile.clone(), intervalSeconds);
    }

    @Override
    public TileComponent cloneWithParameters(Object... params) {
        if (params.length > 0 && params[0] instanceof Double) {
            return new TimerDecorator(decoratedTile.clone(), (Double)params[0]);
        }
        return clone();
    }

    @Override
    public void draw(GraphicsContext gc) {
        decoratedTile.draw(gc);
    }

    @Override
    public String getTexture() {
        return decoratedTile.getTexture();
    }

    public void stopTimer() {
        this.isRunning = false;
    }

    public void startTimer() {
        this.isRunning = true;
        this.startTime = System.currentTimeMillis();
    }

    public double getRemainingSeconds() {
        if (!isRunning) return 0.0;
        long currentTime = System.currentTimeMillis();
        double elapsedSeconds = (currentTime - startTime) / 1000.0;
        return Math.max(0.0, intervalSeconds - elapsedSeconds);
    }
    public boolean isCollideable() {
        return decoratedTile.isCollideable();
    }

    public void setCollideable(boolean newC){ decoratedTile.setCollideable(newC); }
}