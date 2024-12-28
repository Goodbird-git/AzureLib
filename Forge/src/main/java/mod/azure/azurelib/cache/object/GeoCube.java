/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.cache.object;

import mod.azure.azurelib.model.AzBone;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Baked cuboid for a {@link AzBone}
 */
public class GeoCube {
    private final GeoQuad[] quads;
    private final Vector3f pivot;
    private final Vector3f rotation;
    private final Vector3d size;
    private final double inflate;
    private final boolean mirror;

    public GeoCube(GeoQuad[] quads, Vector3f pivot, Vector3f rotation, Vector3d size, double inflate, boolean mirror) {
        this.quads = quads;
        this.pivot = pivot;
        this.rotation = rotation;
        this.size = size;
        this.inflate = inflate;
        this.mirror = mirror;
    }

    public GeoQuad[] quads() {
        return quads;
    }

    public Vector3f pivot() {
        return pivot;
    }

    public Vector3f rotation() {
        return rotation;
    }

    public Vector3d size() {
        return size;
    }

    public double inflate() {
        return inflate;
    }

    public boolean mirror() {
        return mirror;
    }
}
