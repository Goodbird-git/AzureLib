/**
 * This class is a fork of the matching class found in the SmartBrainLib repository.
 * Original source: https://github.com/Tslat/SmartBrainLib
 * Copyright © 2024 Tslat.
 * Licensed under the MIT License.
 */
package mod.azure.azurelib.sblforked.api.core.behaviour.custom.misc;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import mod.azure.azurelib.sblforked.api.core.behaviour.ExtendedBehaviour;

import java.util.List;

/**
 * Do nothing at all.
 * @param <E> The entity
 */
public class Idle<E extends LivingEntity> extends ExtendedBehaviour<E> {
	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
		return List.of();
	}
	
	@Override
	protected boolean shouldKeepRunning(E entity) {
		return true;
	}
}
