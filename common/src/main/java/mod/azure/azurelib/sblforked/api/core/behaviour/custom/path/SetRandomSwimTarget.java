/**
 * This class is a fork of the matching class found in the SmartBrainLib repository. Original source:
 * https://github.com/Tslat/SmartBrainLib Copyright © 2024 Tslat. Licensed under Mozilla Public License 2.0:
 * https://github.com/Tslat/SmartBrainLib/blob/1.21/LICENSE.
 */
package mod.azure.azurelib.sblforked.api.core.behaviour.custom.path;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import mod.azure.azurelib.sblforked.api.core.behaviour.ExtendedBehaviour;
import mod.azure.azurelib.sblforked.object.SquareRadius;
import mod.azure.azurelib.sblforked.util.BrainUtils;

/**
 * Set a random position to swim to. <br>
 * Defaults:
 * <ul>
 * <li>1x movespeed modifier</li>
 * <li>10-block lateral radius</li>
 * <li>10-block vertical radius</li>
 * </ul>
 *
 * @param <E>
 */
public class SetRandomSwimTarget<E extends PathfinderMob> extends ExtendedBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
        Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)
    );

    protected BiFunction<E, Vec3, Float> speedModifier = (entity, targetPos) -> 1f;

    protected SquareRadius radius = new SquareRadius(10, 7);

    protected BiPredicate<E, Vec3> positionPredicate = (entity, pos) -> true;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    /**
     * Set the radius in which to look for swim positions.
     *
     * @param radius The coordinate radius, in blocks
     * @return this
     */
    public SetRandomSwimTarget<E> setRadius(double radius) {
        return setRadius(radius, radius);
    }

    /**
     * Set the radius in which to look for swim positions.
     *
     * @param xz The X/Z coordinate radius, in blocks
     * @param y  The Y coordinate radius, in blocks
     * @return this
     */
    public SetRandomSwimTarget<E> setRadius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);

        return this;
    }

    /**
     * Set the movespeed modifier for the path when chosen.
     *
     * @param modifier The movespeed modifier/multiplier
     * @return this
     */
    public SetRandomSwimTarget<E> speedModifier(float modifier) {
        return speedModifier((entity, targetPos) -> modifier);
    }

    /**
     * Set the movespeed modifier for the path when chosen.
     *
     * @param function The movespeed modifier/multiplier function
     * @return this
     */
    public SetRandomSwimTarget<E> speedModifier(BiFunction<E, Vec3, Float> function) {
        this.speedModifier = function;

        return this;
    }

    /**
     * Sets a predicate to check whether the target movement position is valid or not
     *
     * @param predicate The predicate
     * @return this
     */
    public SetRandomSwimTarget<E> swimTargetPredicate(BiPredicate<E, Vec3> predicate) {
        this.positionPredicate = predicate;

        return this;
    }

    @Override
    protected void start(E entity) {
        Vec3 targetPos = getTargetPos(entity);

        if (!this.positionPredicate.test(entity, targetPos))
            targetPos = null;

        if (targetPos == null) {
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
        } else {
            BrainUtils.setMemory(
                entity,
                MemoryModuleType.WALK_TARGET,
                new WalkTarget(targetPos, this.speedModifier.apply(entity, targetPos), 0)
            );
        }
    }

    @Nullable
    protected Vec3 getTargetPos(E entity) {
        return BehaviorUtils.getRandomSwimmablePos(entity, (int) this.radius.xzRadius(), (int) this.radius.yRadius());
    }
}
