import java.util.*;
public class CoordinateTileMap {
    private Map<CoordinateKey, List<TileComponent>> tileMap;

    public CoordinateTileMap() {
        this.tileMap = new HashMap<>();
    }

    // Custom key class for coordinates
    private static class CoordinateKey {
        private final int x;
        private final int y;

        public CoordinateKey(int[] coordinate) {
            if (coordinate == null || coordinate.length != 2) {
                throw new IllegalArgumentException("Coordinate must be an array of 2 integers");
            }
            this.x = coordinate[0];
            this.y = coordinate[1];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoordinateKey that = (CoordinateKey) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    // Add tiles to a coordinate
    public void put(int[] coordinate, List<TileComponent> tiles) {
        tileMap.put(new CoordinateKey(coordinate), new ArrayList<>(tiles));
    }

    // Add a single tile to a coordinate
    public void addTile(int[] coordinate, TileComponent tile) {
        CoordinateKey key = new CoordinateKey(coordinate);
        tileMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tile);
    }

    // Get tiles at a coordinate
    public List<TileComponent> get(int[] coordinate) {
        List<TileComponent> tiles = tileMap.get(new CoordinateKey(coordinate));
        return tiles != null ? new ArrayList<>(tiles) : new ArrayList<>();
    }

    // Remove all tiles at a coordinate
    public List<TileComponent> remove(int[] coordinate) {
        return tileMap.remove(new CoordinateKey(coordinate));
    }

    // Delete one specific tile at a coordinate
    public void deleteTile(int[] coordinate, TileComponent tileToDelete) {
        CoordinateKey key = new CoordinateKey(coordinate);
        List<TileComponent> tiles = tileMap.get(key);
        if (tiles != null) {
            tiles.removeIf(tile ->
                    tile.getX() == tileToDelete.getX() &&
                            tile.getY() == tileToDelete.getY()
            );
            // Remove the coordinate key if the list becomes empty
            if (tiles.isEmpty()) {
                tileMap.remove(key);
            }
        }
    }

    // Check if coordinate exists in map
    public boolean containsCoordinate(int[] coordinate) {
        return tileMap.containsKey(new CoordinateKey(coordinate));
    }

    // Get all coordinates
    public Set<int[]> getCoordinates() {
        Set<int[]> coordinates = new HashSet<>();
        for (CoordinateKey key : tileMap.keySet()) {
            coordinates.add(new int[]{key.x, key.y});
        }
        return coordinates;
    }

    // Get all tiles in the map
    public List<TileComponent> getAllTiles() {
        List<TileComponent> allTiles = new ArrayList<>();
        for (List<TileComponent> tileList : tileMap.values()) {
            allTiles.addAll(tileList);
        }
        return allTiles;
    }

    // Clear the map
    public void clear() {
        tileMap.clear();
    }

    public int size() {
        return tileMap.size();
    }

    public void printAllTiles() {
        tileMap.forEach((coords, tile) -> {
            System.out.println("Tile: " + tile);
        });
    }
}