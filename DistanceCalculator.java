public class DistanceCalculator {
    private static final boolean DEBUG = false; // Set to false to disable debug prints

    /**
     * Calculates distance between player and tile in block units
     * @param tile The tile to check distance to
     * @param player The player
     * @return Distance in block units (1 block = 30 pixels)
     */
    public static double getDistance(TileComponent tile, Player player) {
        // Get center points of both objects
        double tileCenterX = tile.getX() + tile.getSize() / 2;
        double tileCenterY = tile.getY() + tile.getSize() / 2;
        double playerCenterX = player.getX() + player.getSize() / 2;
        double playerCenterY = player.getY() + player.getSize() / 2;

        if (DEBUG) {
            System.out.printf("Tile position: (%.2f, %.2f)%n", tile.getX(), tile.getY());
            System.out.printf("Player position: (%.2f, %.2f)%n", player.getX(), player.getY());
            System.out.printf("Tile center: (%.2f, %.2f)%n", tileCenterX, tileCenterY);
            System.out.printf("Player center: (%.2f, %.2f)%n", playerCenterX, playerCenterY);

        }

        // Calculate pixel distance
        double pixelDistance = Math.sqrt(
                Math.pow(tileCenterX - playerCenterX, 2) +
                        Math.pow(tileCenterY - playerCenterY, 2)
        );

        // Convert to block units (assuming 30 pixels per block)
        double blockDistance = pixelDistance / 30.0;

        if (DEBUG) {
            System.out.printf("Pixel distance: %.2f%n", pixelDistance);
            System.out.printf("Block distance: %.2f%n", blockDistance);
            System.out.println("------------------------");
        }

        return blockDistance;
    }

    /**
     * Checks if player is within specified distance of a tile
     * @param tile The tile to check
     * @param player The player
     * @param blockDistance Distance threshold in block units
     * @return true if within specified distance
     */
    public static boolean isWithinDistance(TileComponent tile, Player player, double blockDistance) {
        return getDistance(tile, player) <= blockDistance;
    }
}