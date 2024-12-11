/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.common.internal.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.common.internal.common.network.AbstractPacket;
import mod.azure.azurelib.common.platform.services.AzureLibNetwork;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;

/**
 * Packet for syncing user-definable animations that can be triggered from the server for {@link Entity Entities}
 */
public record AzEntityAnimTriggerPacket(
    int entityId,
    String controllerName,
    String animName
) implements AbstractPacket {

    public static final Type<AzEntityAnimTriggerPacket> TYPE = new Type<>(
        AzureLibNetwork.AZ_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, AzEntityAnimTriggerPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        AzEntityAnimTriggerPacket::entityId,
        ByteBufCodecs.STRING_UTF8,
        AzEntityAnimTriggerPacket::controllerName,
        ByteBufCodecs.STRING_UTF8,
        AzEntityAnimTriggerPacket::animName,
        AzEntityAnimTriggerPacket::new
    );

    public void handle() {
        var entity = ClientUtils.getLevel().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        AzAnimatorAccessor.get(entity)
            .map(AzAnimator::getAnimationControllerContainer)
            .map(controllerContainer -> controllerContainer.getOrNull(controllerName))
            .ifPresent(controller -> controller.tryTriggerAnimation(animName));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
