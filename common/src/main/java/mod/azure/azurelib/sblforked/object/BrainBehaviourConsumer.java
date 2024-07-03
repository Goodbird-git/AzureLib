/**
 * This class is a fork of the matching class found in the SmartBrainLib repository.
 * Original source: https://github.com/Tslat/SmartBrainLib
 * Copyright © 2024 Tslat.
 * Licensed under the MIT License.
 */
package mod.azure.azurelib.sblforked.object;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;
import mod.azure.azurelib.sblforked.api.core.behaviour.GroupBehaviour;
import org.jetbrains.annotations.Nullable;

/**
 * Functional consumer for brain activity related functions
 */
@FunctionalInterface
public interface BrainBehaviourConsumer {
	/**
	 * Accepts the given behaviour and the information related to it
	 * @param priority The priority the behaviour is nested under
	 * @param activity The activity category the behaviour is under
	 * @param behaviour The behaviour
	 * @param parent The {@link net.minecraft.world.entity.ai.behavior.GateBehavior GateBehaviour} or {@link GroupBehaviour GroupBehaviour}
	 *                        the behaviour is a child of, if applicable
	 */
	void consume(int priority, Activity activity, BehaviorControl<?> behaviour, @Nullable BehaviorControl<?> parent);
}
