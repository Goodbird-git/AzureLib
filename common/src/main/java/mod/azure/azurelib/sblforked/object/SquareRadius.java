/**
 * This class is a fork of the matching class found in the SmartBrainLib repository. Original source:
 * https://github.com/Tslat/SmartBrainLib Copyright © 2024 Tslat. Licensed under Mozilla Public License 2.0:
 * https://github.com/Tslat/SmartBrainLib/blob/1.21/LICENSE.
 */
package mod.azure.azurelib.sblforked.object;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Helper class to store radius values without needlessly using 3 values. <br>
 * Also comes with some handy helper methods.
 *
 * @param xzRadius The lateral radius value (X/Z direction)
 * @param yRadius  The vertical radius value (Y direction)
 */
public record SquareRadius(
    double xzRadius,
    double yRadius
) {

    public Vec3i toVec3i() {
        return new Vec3i((int) this.xzRadius, (int) this.yRadius, (int) this.xzRadius);
    }

    public BlockPos toBlockPos() {
        return BlockPos.containing(this.xzRadius, this.yRadius, this.xzRadius);
    }

    public Vec3 toVec3() {
        return new Vec3(this.xzRadius, this.yRadius, this.xzRadius);
    }

    public AABB inflateAABB(AABB bounds) {
        return bounds.inflate(this.xzRadius, this.yRadius, this.xzRadius);
    }
}
