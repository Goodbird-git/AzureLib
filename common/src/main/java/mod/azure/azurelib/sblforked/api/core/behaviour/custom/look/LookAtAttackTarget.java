/**
 * This class is a fork of the matching class found in the SmartBrainLib repository. Original source:
 * https://github.com/Tslat/SmartBrainLib Copyright © 2024 Tslat. Licensed under Mozilla Public License 2.0:
 * https://github.com/Tslat/SmartBrainLib/blob/1.21/LICENSE.
 */
package mod.azure.azurelib.sblforked.api.core.behaviour.custom.look;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.List;

import mod.azure.azurelib.sblforked.api.core.behaviour.ExtendedBehaviour;
import mod.azure.azurelib.sblforked.util.BrainUtils;

/**
 * Set the {@link MemoryModuleType#LOOK_TARGET} of the brain owner to the current
 * {@link MemoryModuleType#ATTACK_TARGET}, replacing the existing look target.<br>
 * This is mostly superceded by
 * {@link mod.azure.azurelib.sblforked.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget
 * SetWalkTargetToAttackTarget}, but can be useful if you want the brain owner to look at the target without pathing to
 * it
 *
 * @param <E> The entity
 */
public class LookAtAttackTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT),
        Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED)
    );

    private LivingEntity target = null;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = BrainUtils.getTargetOfEntity(entity);

        return !(BrainUtils.getMemory(entity, MemoryModuleType.LOOK_TARGET) instanceof EntityTracker entityTracker)
            || entityTracker.getEntity() != this.target;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.target, true));
    }

    @Override
    protected void stop(E entity) {
        this.target = null;
    }
}
