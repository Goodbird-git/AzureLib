/**
 * This class is a fork of the matching class found in the SmartBrainLib repository.
 * Original source: https://github.com/Tslat/SmartBrainLib
 * Copyright © 2024 Tslat.
 * Licensed under the MIT License.
 */
package mod.azure.azurelib.sblforked;

import com.mojang.serialization.Codec;
import mod.azure.azurelib.sblforked.api.core.sensor.ExtendedSensor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Supplier;

public interface SBLLoader {
	void init(Object eventBus);
	boolean isDevEnv();

	@ApiStatus.Internal
	<T> Supplier<MemoryModuleType<T>> registerMemoryType(String id);
	@ApiStatus.Internal
	<T> Supplier<MemoryModuleType<T>> registerMemoryType(String id, Optional<Codec<T>> codec);
	@ApiStatus.Internal
	<T extends ExtendedSensor<?>> Supplier<SensorType<T>> registerSensorType(String id, Supplier<T> sensor);
}
