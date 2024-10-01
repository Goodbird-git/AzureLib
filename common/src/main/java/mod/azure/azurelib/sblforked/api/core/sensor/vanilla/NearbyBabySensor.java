/**
 * This class is a fork of the matching class found in the SmartBrainLib repository.
 * Original source: https://github.com/Tslat/SmartBrainLib
 * Copyright © 2024 Tslat.
 * Licensed under Mozilla Public License 2.0: https://github.com/Tslat/SmartBrainLib/blob/1.21/LICENSE.
 */
package mod.azure.azurelib.sblforked.api.core.sensor.vanilla;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import mod.azure.azurelib.sblforked.api.core.sensor.EntityFilteringSensor;
import mod.azure.azurelib.sblforked.api.core.sensor.ExtendedSensor;
import mod.azure.azurelib.sblforked.registry.SBLSensors;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * A sensor that sets the {@link MemoryModuleType#VISIBLE_VILLAGER_BABIES}
 * memory by checking the existing visible entities for nearby babies of the
 * same entity type. <br>
 * 
 * @see net.minecraft.world.entity.ai.sensing.VillagerBabiesSensor
 * @param <E> The entity
 */
public class NearbyBabySensor<E extends LivingEntity> extends EntityFilteringSensor<List<LivingEntity>, E> {
	@Override
	public MemoryModuleType<List<LivingEntity>> getMemory() {
		return MemoryModuleType.VISIBLE_VILLAGER_BABIES;
	}

	@Override
	public SensorType<? extends ExtendedSensor<?>> type() {
		return SBLSensors.NEARBY_BABY.get();
	}

	@Override
	protected BiPredicate<LivingEntity, E> predicate() {
		return (target, entity) -> target.getType() == entity.getType() && target.isBaby();
	}

	@Nullable
	@Override
	protected List<LivingEntity> findMatches(E entity, NearestVisibleLivingEntities matcher) {
		return ImmutableList.copyOf(matcher.findAll(target -> predicate().test(target, entity)));
	}
}
