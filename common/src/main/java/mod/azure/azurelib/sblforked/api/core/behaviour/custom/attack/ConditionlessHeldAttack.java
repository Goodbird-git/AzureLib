/**
 * This class is a fork of the matching class found in the SmartBrainLib repository. Original source:
 * https://github.com/Tslat/SmartBrainLib Copyright © 2024 Tslat. Licensed under Mozilla Public License 2.0:
 * https://github.com/Tslat/SmartBrainLib/blob/1.21/LICENSE.
 */
package mod.azure.azurelib.sblforked.api.core.behaviour.custom.attack;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import mod.azure.azurelib.sblforked.api.core.behaviour.HeldBehaviour;
import mod.azure.azurelib.sblforked.util.BrainUtils;

/**
 * Attack behaviour for held attacks that doesn't require line of sight or proximity to target, or to even have a target
 * at all. This is useful for special attacks.<br>
 * Set the actual condition for activation via
 * {@link mod.azure.azurelib.sblforked.api.core.behaviour.ExtendedBehaviour#startCondition
 * ExtendedBehaviour.startCondition}
 *
 * @param <E> The entity
 */
public class ConditionlessHeldAttack<E extends LivingEntity> extends HeldBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
        Pair.of(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT)
    );

    protected boolean requireTarget = false;

    @Nullable
    protected LivingEntity target = null;

    /**
     * Set that the attack requires that the entity have an attack target set to activate.
     *
     * @return this
     */
    public ConditionlessHeldAttack<E> requiresTarget() {
        this.requireTarget = true;

        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (!this.requireTarget)
            return true;

        this.target = BrainUtils.getTargetOfEntity(entity);

        return this.target != null;
    }

    @Override
    protected void start(E entity) {
        entity.swing(InteractionHand.MAIN_HAND);

        if (this.requireTarget)
            BehaviorUtils.lookAtEntity(entity, this.target);
    }

    @Override
    protected void stop(ServerLevel level, E entity, long gameTime) {
        super.stop(level, entity, gameTime);

        this.target = null;
    }
}
