/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.animatable;

import javax.annotation.Nullable;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.network.AzureLibNetwork;
import mod.azure.azurelib.network.SerializableDataTicket;
import mod.azure.azurelib.network.packet.BlockEntityAnimDataSyncPacket;
import mod.azure.azurelib.network.packet.BlockEntityAnimTriggerPacket;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * The {@link GeoAnimatable} interface specific to {@link TileEntity BlockEntities}
 * 
 */
public interface GeoBlockEntity extends GeoAnimatable {
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
		return getAnimatableInstanceCache().getManagerForId(0).getData(dataTicket);
	}

	/**
	 * Saves an arbitrary piece of data to this animatable's {@link AnimatableManager}.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * 
	 * @param dataTicket The DataTicket to sync the data for
	 * @param data       The data to sync
	 */
	default <D> void setAnimData(SerializableDataTicket<D> dataTicket, D data) {
		TileEntity blockEntity = (TileEntity) this;
		World level = blockEntity.getLevel();

		if (level == null) {
			AzureLib.LOGGER.error("Attempting to set animation data for BlockEntity too early! Must wait until after the BlockEntity has been set in the world. (" + blockEntity.getClass().toString() + ")");

			return;
		}

		if (level.isClientSide()) {
			getAnimatableInstanceCache().getManagerForId(0).setData(dataTicket, data);
		} else {
			BlockPos pos = blockEntity.getBlockPos();

			AzureLibNetwork.send(new BlockEntityAnimDataSyncPacket<>(pos, dataTicket, data), PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)));
		}
	}

	/**
	 * Trigger an animation for this BlockEntity, based on the controller name and animation name.<br>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 * 
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName       The name of animation to trigger. This needs to have been registered with the controller via {@link mod.azure.azurelib.core.animation.AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	default void triggerAnim(@Nullable String controllerName, String animName) {
		TileEntity blockEntity = (TileEntity) this;
		World level = blockEntity.getLevel();

		if (level == null) {
			AzureLib.LOGGER.error("Attempting to trigger an animation for a BlockEntity too early! Must wait until after the BlockEntity has been set in the world. (" + blockEntity.getClass().toString() + ")");

			return;
		}

		if (level.isClientSide()) {
			getAnimatableInstanceCache().getManagerForId(0).tryTriggerAnimation(controllerName, animName);
		} else {
			BlockPos pos = blockEntity.getBlockPos();

			AzureLibNetwork.send(new BlockEntityAnimTriggerPacket<>(pos, controllerName, animName), PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)));
		}
	}

	/**
	 * Returns the current age/tick of the animatable instance.<br>
	 * By default this is just the animatable's age in ticks, but this method allows for non-ticking custom animatables to provide their own values
	 * 
	 * @param blockEntity The BlockEntity representing this animatable
	 * @return The current tick/age of the animatable, for animation purposes
	 */
	@Override
	default double getTick(Object blockEntity) {
		return RenderUtils.getCurrentTick();
	}
}
