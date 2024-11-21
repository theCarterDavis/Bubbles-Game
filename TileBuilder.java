import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class TileBuilder {
    private final TileComponent baseTile;
    private final Set<TileType> appliedTypes;

    private TileBuilder(String name, double x, double y, double size, float[] colors, String texture, boolean collideable) {
        this.baseTile = new BaseTile(name, x, y, size, colors, texture, collideable);
        this.appliedTypes = new HashSet<>();
    }

    // Constructor for prototype tiles (without position)
    private TileBuilder(String name, float[] colors, String texture, boolean collideable) {
        this.baseTile = new BaseTile(name, colors, texture, collideable);
        this.appliedTypes = new HashSet<>();
    }

    // Factory method for creating a complete tile
    public static TileBuilder create(String name, double x, double y, double size, float[] colors, String texture, boolean collideable) {
        return new TileBuilder(name, x, y, size, colors, texture, collideable);
    }

    // Factory method for creating a prototype tile
    public static TileBuilder createPrototype(String name, float[] colors, String texture, boolean collideable) {
        return new TileBuilder(name, colors, texture, collideable);
    }

    // Method to set the name and position of a prototype tile
    public TileBuilder setNameAndPosition(String name, double x, double y) {
        BaseTile tile = (BaseTile) baseTile;
        tile.setName(name);
        tile.setPosition(x, y);
        return this;
    }

    public TileBuilder addType(TileType type) {
        if (!appliedTypes.contains(type)) {
            appliedTypes.add(type);
        }
        return this;
    }

    public TileComponent build() {
        TileComponent tile = baseTile;

        // Apply each decorator in the order they were added
        for (TileType type : appliedTypes) {
            tile = type.decorate(tile);
        }

        return tile;
    }

    public Set<TileType> getAppliedTypes() {
        return Collections.unmodifiableSet(appliedTypes);
    }
}