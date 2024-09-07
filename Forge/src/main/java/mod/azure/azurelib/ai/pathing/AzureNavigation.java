package mod.azure.azurelib.ai.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

/* Credit to Bob Mowzie and pau101 for most of the code, 
 * code source for the base class can be found here: 
 * https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/server/ai/MMPathNavigateGround.java
 * */
public class AzureNavigation extends GroundPathNavigator {
    @Nullable
	protected BlockPos pathToPosition;

	public AzureNavigation(MobEntity entity, World world) {
		super(entity, world);
	}

	@Override
	protected PathFinder getPathFinder(int maxVisitedNodes) {
		this.nodeProcessor = new WalkNodeProcessor();
		this.nodeProcessor.setCanEnterDoors(true);
		return new AzurePathFinder(this.nodeProcessor, maxVisitedNodes);
	}

	/**
	 * Forces the entity to stop its current pathfinding by clearing both the {@code path} and {@code pathToPosition}.
	 * Unlike the normal {@code stop()} method, this ensures that {@code pathToPosition} is cleared as well,
	 * preventing potential pathfinding issues caused by lingering path data.
	 * <p>
	 * Special thanks to JayZX535 for contributing this method.
	 */
	public void hardStop() {
		this.currentPath = null;
		this.pathToPosition = null;
	}

	@Override
	protected void pathFollow() {
		Path path = Objects.requireNonNull(this.currentPath);
		Vec3d entityPos = this.getEntityPosition();
		int pathLength = path.getCurrentPathLength();
		for (int i = path.getCurrentPathIndex(); i < path.getCurrentPathLength(); i++) {
			if (path.getPathPointFromIndex(i).y != Math.floor(entityPos.y)) {
				pathLength = i;
				break;
			}
		}
		final Vec3d base = entityPos.add(-this.entity.getWidth() * 0.5F, 0.0F, -this.entity.getWidth() * 0.5F);
		final Vec3d max = base.add(this.entity.getWidth(), this.entity.getHeight(), this.entity.getWidth());
		if (this.tryShortcut(path, new Vec3d(this.entity.getPosX(), this.entity.getPosY(), this.entity.getPosZ()), pathLength, base, max)) {
			if (this.isAt(path, 0.5F) || this.atElevationChange(path) && this.isAt(path, this.entity.getWidth() * 0.5F)) {
				path.setCurrentPathIndex(path.getCurrentPathIndex() + 1);
			}
		}
		this.checkForStuck(entityPos);
	}

    @Override
    public Path getPathToPos(BlockPos blockPos, int i) {
        this.pathToPosition = blockPos;
        return super.getPathToPos(blockPos, i);
    }

    @Override
    public Path getPathToEntity(Entity entity, int i) {
        this.pathToPosition = entity.getPosition();
        return super.getPathToEntity(entity, i);
    }

    @Override
    public boolean tryMoveToEntityLiving(Entity entity, double d) {
        Path path = this.getPathToEntity(entity, 0);
        if (path != null) {
            return this.setPath(path, d);
        }
        this.pathToPosition = entity.getPosition();
        this.speed = d;
        return true;
    }

	@Override
	public void tick() {
		super.tick();
        if (this.noPath()) {
            if (this.pathToPosition != null) {
                if (this.pathToPosition.withinDistance(this.entity.getPositionVec(), this.entity.getWidth()) || this.entity.getPosY() > (double)this.pathToPosition.getY() && new BlockPos(this.pathToPosition.getX(), this.entity.getPosY(), this.pathToPosition.getZ()).withinDistance(this.entity.getPositionVec(), this.entity.getWidth())) {
                    this.pathToPosition = null;
                } else {
                    this.entity.getMoveHelper().setMoveTo(this.pathToPosition.getX(), this.pathToPosition.getY(), this.pathToPosition.getZ(), this.speed);
                }
            }
            return;
        }
		if (this.getTargetPos() != null)
			this.entity.getLookController().setLookPosition(this.getTargetPos().getX(), this.getTargetPos().getY(), this.getTargetPos().getZ());
	}

	private boolean isAt(Path path, float threshold) {
		final Vec3d pathPos = path.getPosition(this.entity);
		return MathHelper.abs((float) (this.entity.getPosX() - pathPos.x)) < threshold && MathHelper.abs((float) (this.entity.getPosZ() - pathPos.z)) < threshold && Math.abs(this.entity.getPosY() - pathPos.y) < 1.0D;
	}

	private boolean atElevationChange(Path path) {
		final int curr = path.getCurrentPathIndex();
		final int end = Math.min(path.getCurrentPathLength(), curr + MathHelper.ceil(this.entity.getWidth() * 0.5F) + 1);
		final int currY = path.getPathPointFromIndex(curr).y;
		for (int i = curr + 1; i < end; i++) {
			if (path.getPathPointFromIndex(i).y != currY) {
				return true;
			}
		}
		return false;
	}

	private boolean tryShortcut(Path path, Vec3d entityPos, int pathLength, Vec3d base, Vec3d max) {
		for (int i = pathLength; --i > path.getCurrentPathIndex();) {
			final Vec3d vec = path.getVectorFromIndex(this.entity, i).subtract(entityPos);
			if (this.sweep(vec, base, max)) {
				path.setCurrentPathIndex(i);
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d start, Vec3d end, int sizeX, int sizeY, int sizeZ) {
		return true;
	}

	static final float EPSILON = 1.0E-8F;

	// Based off of https://github.com/andyhall/voxel-aabb-sweep/blob/d3ef85b19c10e4c9d2395c186f9661b052c50dc7/index.js
	private boolean sweep(Vec3d vec, Vec3d base, Vec3d max) {
		float t = 0.0F;
		float max_t = (float) vec.length();
		if (max_t < EPSILON)
			return true;
		final float[] tr = new float[3];
		final int[] ldi = new int[3];
		final int[] tri = new int[3];
		final int[] step = new int[3];
		final float[] tDelta = new float[3];
		final float[] tNext = new float[3];
		final float[] normed = new float[3];
		for (int i = 0; i < 3; i++) {
			float value = element(vec, i);
			boolean dir = value >= 0.0F;
			step[i] = dir ? 1 : -1;
			float lead = element(dir ? max : base, i);
			tr[i] = element(dir ? base : max, i);
			ldi[i] = leadEdgeToInt(lead, step[i]);
			tri[i] = trailEdgeToInt(tr[i], step[i]);
			normed[i] = value / max_t;
			tDelta[i] = MathHelper.abs(max_t / value);
			float dist = dir ? (ldi[i] + 1 - lead) : (lead - ldi[i]);
			tNext[i] = tDelta[i] < Float.POSITIVE_INFINITY ? tDelta[i] * dist : Float.POSITIVE_INFINITY;
		}
		final BlockPos.Mutable pos = new BlockPos.Mutable();
		do {
			// stepForward
			int axis = (tNext[0] < tNext[1]) ? ((tNext[0] < tNext[2]) ? 0 : 2) : ((tNext[1] < tNext[2]) ? 1 : 2);
			float dt = tNext[axis] - t;
			t = tNext[axis];
			ldi[axis] += step[axis];
			tNext[axis] += tDelta[axis];
			for (int i = 0; i < 3; i++) {
				tr[i] += dt * normed[i];
				tri[i] = trailEdgeToInt(tr[i], step[i]);
			}
			// checkCollision
			int stepx = step[0];
			int x0 = (axis == 0) ? ldi[0] : tri[0];
			int x1 = ldi[0] + stepx;
			int stepy = step[1];
			int y0 = (axis == 1) ? ldi[1] : tri[1];
			int y1 = ldi[1] + stepy;
			int stepz = step[2];
			int z0 = (axis == 2) ? ldi[2] : tri[2];
			int z1 = ldi[2] + stepz;
			for (int x = x0; x != x1; x += stepx) {
				for (int z = z0; z != z1; z += stepz) {
					for (int y = y0; y != y1; y += stepy) {
						BlockState block = this.world.getBlockState(pos.setPos(x, y, z));
						if (!block.allowsMovement(this.world, pos, PathType.LAND))
							return false;
					}
					PathNodeType below = this.nodeProcessor.getPathNodeType(this.world, x, y0 - 1, z, this.entity, 1, 1, 1, true, true);
					if (below == PathNodeType.WATER || below == PathNodeType.LAVA || below == PathNodeType.OPEN)
						return false;
					PathNodeType in = this.nodeProcessor.getPathNodeType(this.world, x, y0, z, this.entity, 1, y1 - y0, 1, true, true);
					float priority = this.entity.getPathPriority(in);
					if (priority < 0.0F || priority >= 8.0F)
						return false;
					if (in == PathNodeType.DAMAGE_FIRE || in == PathNodeType.DANGER_FIRE || in == PathNodeType.DAMAGE_OTHER)
						return false;
				}
			}
		} while (t <= max_t);
		return true;
	}

	static int leadEdgeToInt(float coord, int step) {
		return MathHelper.floor(coord - step * EPSILON);
	}

	static int trailEdgeToInt(float coord, int step) {
		return MathHelper.floor(coord + step * EPSILON);
	}

	static float element(Vec3d v, int i) {
		switch (i) {
		case 0:
			return (float) v.x;
		case 1:
			return (float) v.y;
		case 2:
			return (float) v.z;
		default:
			return 0.0F;
		}
	}
}
