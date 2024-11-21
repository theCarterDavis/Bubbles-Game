import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.List;

public class Player {
    private double x, y;
    private double xvelocity = 0;
    private double yvelocity = 0;
    private double playerSize = 30;
    private double jump = 0;
    private int leftover = 0;
    private boolean isOnGround = false;
    GraphicsContext gc;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return playerSize; }

    public void moveLeft() {
        // This will be handled in the update method
    }

    public void moveRight() {
        // This will be handled in the update method
    }

    public void jump() {
        if (isOnGround) {
            jump = 10; // Initial jump value
            isOnGround = false;
        }
    }

    public void update(CoordinateTileMap tiles, int leftright, double timeElapsed) {
        // Update physics in 10ms increments
        leftover += (int)(timeElapsed * 100000);
        int whole = leftover / 1000;
        leftover = leftover % 1000;

        // Update jump physics
        for (int i = 0; i < whole; i++) {
            jump *= 0.975f;
        }
        if (jump < 0) {
            jump = 0;
        }

        // Update horizontal movement
        for (int i = 0; i < whole; i++) {
            xvelocity += (leftright * 0.4f);
        }

        // Apply max horizontal speed
        if (xvelocity > 5) {
            xvelocity = 5;
        } else if (xvelocity < -5) {
            xvelocity = -5;
        }

        // Apply friction when not moving
        if (leftright == 0) {
            for (int i = 0; i < whole; i++) {
                xvelocity *= 0.9f;
            }
        }

        // Handle jumping and gravity
        if (jump > 1 && yvelocity < -0.1f) {
            yvelocity = 0;
        }
        if (jump > 1) {
            yvelocity = jump;
        }
        for (int i = 0; i < whole; i++) {
            yvelocity -= 0.15;
        }

        // Apply terminal velocity
        if (yvelocity < -12) {
            yvelocity = -12;
        }

        // Clean up small velocities
        if (Math.abs(yvelocity) < 0.01) {
            yvelocity = 0;
        }
        if (Math.abs(xvelocity) < 0.01) {
            xvelocity = 0;
        }

        //System.out.println(yvelocity);
        // Move and handle collisions
        moveAndCollide(tiles);
    }

    private void moveAndCollide(CoordinateTileMap tiles) {
        // Move horizontally and check collisions
        x += xvelocity;
        handleHorizontalCollisions(tiles);

        // Move vertically and check collisions
        y -= yvelocity;  // Subtract because screen coordinates increase downward
        handleVerticalCollisions(tiles);
    }

    private void handleHorizontalCollisions(CoordinateTileMap tiles) {
        int xChunk = (int) x/800;
        int yChunk = (int) y/450;

        for (int xC = xChunk - 1; xC <= xChunk+1; xC++) {
            for (int yC = yChunk - 1; yC <= yChunk+1; yC++) {
                List<TileComponent> tilesAtSpot = tiles.get(new int[]{xC, yC});
                for (TileComponent tile : tilesAtSpot) {
                    if (tile.intersects(this)) {
                        if(tile instanceof ActivateDecorator) {
                            tile.applyEffect(this);
                        }

                        if (tile instanceof BaseTile || tile instanceof TileDecorator) {
                            if (xvelocity > 0) {
                                x = tile.getX() - playerSize;
                            } else if (xvelocity < 0) {
                                x = tile.getX() + tile.getSize();
                            }
                            xvelocity = 0;
                        }

                        handleSpecialTiles(tile);
                    }
                }
            }
        }
    }

    private void handleVerticalCollisions(CoordinateTileMap tiles) {
        int xChunk = (int) x/800;
        int yChunk = (int) y/450;
        isOnGround = false;

        for (int xC = xChunk - 1; xC <= xChunk+1; xC++) {
            for (int yC = yChunk - 1; yC <= yChunk+1; yC++) {
                List<TileComponent> tilesAtSpot = tiles.get(new int[]{xC, yC});
                for (TileComponent tile : tilesAtSpot) {
                    // Check for breaking tiles first
                    if (tile instanceof BreaksDecorator && tile.intersects(this)) {
                        tile.applyEffect(this);
                    }

                    if (tile.intersects(this)) {
                        if(tile instanceof ActivateDecorator) {
                            tile.applyEffect(this);
                        }

                        if (tile instanceof BaseTile || tile instanceof TileDecorator) {
                            if (yvelocity < 0) { // Moving downward in screen coordinates
                                y = tile.getY() - playerSize;
                                yvelocity = 0;
                                isOnGround = true;
                            } else if (yvelocity > 0) { // Moving upward in screen coordinates
                                y = tile.getY() + tile.getSize();
                                yvelocity = 0;
                                jump = 0; // Hit ceiling, stop jump
                            }
                        }

                        handleSpecialTiles(tile);
                    }
                }
            }
        }
    }


    private void handleSpecialTiles(TileComponent tile) {
        if(tile instanceof GoalDecorator || tile instanceof KillDecorator) {
            Client.loadLevelEditor();
            LevelPlayer.end();
        }

            tile.applyEffect(this);
    }

    public void draw(GraphicsContext gc) {
        this.gc = gc;
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, playerSize, playerSize);


        Image image = new Image("assets/player.png"); // Replace with your image path



        // Draw a scaled image (optional)
        gc.drawImage(image, x, y, 30, 30); // x, y, width, height


    }

    public int[] getChunk() {
        int xc = (int) x/800;
        int yc = (int) y/450;
        return new int[]{xc, yc};
    }

    public void velmod(int x, int y) {
        yvelocity = yvelocity + y;
        xvelocity = xvelocity + x;
    }

    public void addForce(double forceX, double forceY) {
        double maxForce = 10.0;
        xvelocity += Math.max(-maxForce, Math.min(maxForce, forceX));
        yvelocity += Math.max(-maxForce, Math.min(maxForce, forceY));

        double maxVelocity = 15.0;
        xvelocity = Math.max(-maxVelocity, Math.min(maxVelocity, xvelocity));
        yvelocity = Math.max(-maxVelocity, Math.min(maxVelocity, yvelocity));
    }
}