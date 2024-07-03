/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.network.packet;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.network.AzureLibNetwork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet for syncing user-definable animations that can be triggered from the server
 */
public class AnimTriggerPacket<D> {
	private final String syncableId;
	private final long instanceId;
	private final String controllerName;
	private final String animName;

	public AnimTriggerPacket(String syncableId, long instanceId, @Nullable String controllerName, String animName) {
		this.syncableId = syncableId;
		this.instanceId = instanceId;
		this.controllerName = controllerName == null ? "" : controllerName;
		this.animName = animName;
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeString(this.syncableId);
		buffer.writeVarLong(this.instanceId);
		buffer.writeString(this.controllerName);
		buffer.writeString(this.animName);
	}

	public static <D> AnimTriggerPacket<D> decode(PacketBuffer buffer) {
		return new AnimTriggerPacket<>(buffer.readString(), buffer.readVarLong(), buffer.readString(), buffer.readString());
	}

	public void receivePacket(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context handler = context.get();

		handler.enqueueWork(() -> {
			GeoAnimatable animatable = AzureLibNetwork.getSyncedAnimatable(this.syncableId);

			if (animatable != null) {
				AnimatableManager<?> manager = animatable.getAnimatableInstanceCache().getManagerForId(this.instanceId);

				manager.tryTriggerAnimation(this.controllerName, this.animName);
			}
		});
		handler.setPacketHandled(true);
	}
}
