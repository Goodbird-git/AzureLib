package mod.azure.azurelib.model.factory.primitive;


import mod.azure.azurelib.cache.object.GeoVertex;
import net.minecraft.util.math.Vec3d;

/**
 * Holder class to make it easier to store and refer to vertices for a given cube
 */
public class VertexSet {
    private final GeoVertex bottomLeftBack;
    private final GeoVertex bottomRightBack;
    private final GeoVertex topLeftBack;
    private final GeoVertex topRightBack;
    private final GeoVertex topLeftFront;
    private final GeoVertex topRightFront;
    private final GeoVertex bottomLeftFront;
    private final GeoVertex bottomRightFront;

    // Constructor initializing all fields
    public VertexSet(
            GeoVertex bottomLeftBack,
            GeoVertex bottomRightBack,
            GeoVertex topLeftBack,
            GeoVertex topRightBack,
            GeoVertex topLeftFront,
            GeoVertex topRightFront,
            GeoVertex bottomLeftFront,
            GeoVertex bottomRightFront
    ) {
        this.bottomLeftBack = bottomLeftBack;
        this.bottomRightBack = bottomRightBack;
        this.topLeftBack = topLeftBack;
        this.topRightBack = topRightBack;
        this.topLeftFront = topLeftFront;
        this.topRightFront = topRightFront;
        this.bottomLeftFront = bottomLeftFront;
        this.bottomRightFront = bottomRightFront;
    }

    // Convenience constructor
    public VertexSet(Vec3d origin, Vec3d vertexSize, double inflation) {
        this(
                new GeoVertex(origin.x - inflation, origin.y - inflation, origin.z - inflation),
                new GeoVertex(origin.x - inflation, origin.y - inflation, origin.z + vertexSize.z + inflation),
                new GeoVertex(origin.x - inflation, origin.y + vertexSize.y + inflation, origin.z - inflation),
                new GeoVertex(
                        origin.x - inflation,
                        origin.y + vertexSize.y + inflation,
                        origin.z + vertexSize.z + inflation
                ),
                new GeoVertex(
                        origin.x + vertexSize.x + inflation,
                        origin.y + vertexSize.y + inflation,
                        origin.z - inflation
                ),
                new GeoVertex(
                        origin.x + vertexSize.x + inflation,
                        origin.y + vertexSize.y + inflation,
                        origin.z + vertexSize.z + inflation
                ),
                new GeoVertex(origin.x + vertexSize.x + inflation, origin.y - inflation, origin.z - inflation),
                new GeoVertex(
                        origin.x + vertexSize.x + inflation,
                        origin.y - inflation,
                        origin.z + vertexSize.z + inflation
                )
        );
    }

    // Getters for each field
    public GeoVertex bottomLeftBack() {
        return bottomLeftBack;
    }

    public GeoVertex bottomRightBack() {
        return bottomRightBack;
    }

    public GeoVertex topLeftBack() {
        return topLeftBack;
    }

    public GeoVertex topRightBack() {
        return topRightBack;
    }

    public GeoVertex topLeftFront() {
        return topLeftFront;
    }

    public GeoVertex topRightFront() {
        return topRightFront;
    }

    public GeoVertex bottomLeftFront() {
        return bottomLeftFront;
    }

    public GeoVertex bottomRightFront() {
        return bottomRightFront;
    }

    // Methods to return vertex arrays for each quad
    public GeoVertex[] quadWest() {
        return new GeoVertex[] { this.topRightBack, this.topLeftBack, this.bottomLeftBack, this.bottomRightBack };
    }

    public GeoVertex[] quadEast() {
        return new GeoVertex[] {
                this.topLeftFront,
                this.topRightFront,
                this.bottomRightFront,
                this.bottomLeftFront
        };
    }

    public GeoVertex[] quadNorth() {
        return new GeoVertex[] { this.topLeftBack, this.topLeftFront, this.bottomLeftFront, this.bottomLeftBack };
    }

    public GeoVertex[] quadSouth() {
        return new GeoVertex[] {
                this.topRightFront,
                this.topRightBack,
                this.bottomRightBack,
                this.bottomRightFront
        };
    }

    public GeoVertex[] quadUp() {
        return new GeoVertex[] { this.topRightBack, this.topRightFront, this.topLeftFront, this.topLeftBack };
    }

    public GeoVertex[] quadDown() {
        return new GeoVertex[] {
                this.bottomLeftBack,
                this.bottomLeftFront,
                this.bottomRightFront,
                this.bottomRightBack
        };
    }

    // Method to handle dynamic quad selection
    public GeoVertex[] verticesForQuad(Direction direction, boolean boxUv, boolean mirror) {
        return switch (direction) {
            case WEST -> mirror ? quadEast() : quadWest();
            case EAST -> mirror ? quadWest() : quadEast();
            case NORTH -> quadNorth();
            case SOUTH -> quadSouth();
            case UP -> mirror && !boxUv ? quadDown() : quadUp();
            case DOWN -> mirror && !boxUv ? quadUp() : quadDown();
        };
    }
}
