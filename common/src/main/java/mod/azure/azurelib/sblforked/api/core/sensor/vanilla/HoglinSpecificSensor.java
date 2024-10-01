/**
 * This class is a fork of the matching class found in the SmartBrainLib repository.
 * Original source: https://github.com/Tslat/SmartBrainLib
 * Copyright © 2024 Tslat.
 * Licensed under Mozilla Public License 2.0: https://github.com/Tslat/SmartBrainLib/blob/1.21/LICENSE.
 */
package mod.azure.azurelib.sblforked.api.core.sensor.vanilla;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import mod.azure.azurelib.sblforked.api.core.sensor.ExtendedSensor;
import mod.azure.azurelib.sblforked.registry.SBLSensors;
import mod.azure.azurelib.sblforked.util.BrainUtils;

import java.util.List;

/**
 * A replication of vanilla's
 * {@link net.minecraft.world.entity.ai.sensing.HoglinSpecificSensor}. Not
 * really useful, but included for completeness' sake and legibility. <br>
 * Handles most of Hoglin's memories at once
 * 
 * @param <E> The entity
 */
public class HoglinSpecificSensor<E extends LivingEntity> extends ExtendedSensor<E> {
	private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
			MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
			MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
			MemoryModuleType.NEAREST_REPELLENT);

	@Override
	public List<MemoryModuleType<?>> memoriesUsed() {
		return MEMORIES;
	}

	@Override
	public SensorType<? extends ExtendedSensor<?>> type() {
		return SBLSensors.HOGLIN_SPECIFIC.get();
	}

	@Override
	protected void doTick(ServerLevel level, E entity) {
		Brain<?> brain = entity.getBrain();

		BrainUtils.withMemory(brain, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, entities -> {
			int piglinCount = 0;
			Piglin nearestPiglin = null;
			List<Hoglin> hoglins = new ObjectArrayList<>();

			for (LivingEntity target : entities.findAll(mob -> !mob.isBaby())) {
				if (target instanceof Piglin piglin) {
					piglinCount++;

					if (nearestPiglin == null)
						nearestPiglin = piglin;
				}
				else if (target instanceof Hoglin hoglin) {
					hoglins.add(hoglin);
				}
			}

			BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, nearestPiglin);
			BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, hoglins);
			BrainUtils.setMemory(brain, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, piglinCount);
			BrainUtils.setMemory(brain, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, hoglins.size());
			BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_REPELLENT, BlockPos.findClosestMatch(entity.blockPosition(), 8, 4, pos -> level.getBlockState(pos).is(BlockTags.HOGLIN_REPELLENTS)).orElse(null));
		});
	}
}
