/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.animatable;

import javax.annotation.Nullable;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.network.AzureLibNetwork;
import mod.azure.azurelib.network.SerializableDataTicket;
import mod.azure.azurelib.network.packet.EntityAnimDataSyncPacket;
import mod.azure.azurelib.network.packet.EntityAnimTriggerPacket;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * The {@link GeoAnimatable} interface specific to {@link Entity Entities}. This also applies to Projectiles and other Entity subclasses.<br>
 * <b>NOTE:</b> This <u>cannot</u> be used for entities using the {@link mod.azure.azurelib.renderer.GeoReplacedEntityRenderer} as you aren't extending {@code Entity}. Use {@link GeoReplacedEntity} instead.
 */
public interface GeoEntity extends GeoAnimatable {
	/**
	 * Get server-synced animation data via its relevant {@link SerializableDataTicket}.<br>
	 * Should only be used on the <u>client-side</u>.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * 
	 * @param dataTicket The data ticket for the data to retrieve
	 * @return The synced data, or null if no data of that type has been synced
	 */
	@Nullable
	default <D> D getAnimData(SerializableDataTicket<D> dataTicket) {
		return getAnimatableInstanceCache().getManagerForId(((Entity) this).getEntityId()).getData(dataTicket);
	}

	/**
	 * Saves an arbitrary syncable piece of data to this animatable's {@link AnimatableManager}.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * 
	 * @param dataTicket The DataTicket to sync the data for
	 * @param data       The data to sync
	 */
	default <D> void setAnimData(SerializableDataTicket<D> dataTicket, D data) {
		Entity entity = (Entity) this;

		if (entity.getEntityWorld().isRemote()) {
			getAnimatableInstanceCache().getManagerForId(entity.getEntityId()).setData(dataTicket, data);
		} else {
			AzureLibNetwork.send(new EntityAnimDataSyncPacket<>(entity.getEntityId(), dataTicket, data), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity));
		}
	}

	/**
	 * Trigger an animation for this Entity, based on the controller name and animation name.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * 
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link mod.azure.azurelib.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	default void triggerAnim(@Nullable String controllerName, String animName) {
		Entity entity = (Entity) this;

		if (entity.getEntityWorld().isRemote()) {
			getAnimatableInstanceCache().getManagerForId(entity.getEntityId()).tryTriggerAnimation(controllerName, animName);
		} else {
			AzureLibNetwork.send(new EntityAnimTriggerPacket<>(entity.getEntityId(), controllerName, animName), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity));
		}
	}

	/**
	 * Returns the current age/tick of the animatable instance.<br>
	 * By default this is just the animatable's age in ticks, but this method allows for non-ticking custom animatables to provide their own values
	 * 
	 * @param entity The Entity representing this animatable
	 * @return The current tick/age of the animatable, for animation purposes
	 */
	@Override
	default double getTick(Object entity) {
		return RenderUtils.getCurrentTick();
	}
}
