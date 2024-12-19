package mod.azure.azurelib.common.internal.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.common.internal.common.network.AbstractPacket;
import mod.azure.azurelib.common.platform.services.AzureLibNetwork;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;

public record AzEntityDispatchCommandPacket(
    int entityId,
    AzDispatchCommand dispatchCommand,
    AzDispatchSide origin
) implements AbstractPacket {

    public static final Type<AzEntityDispatchCommandPacket> TYPE = new Type<>(
        AzureLibNetwork.AZ_ENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, AzEntityDispatchCommandPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        AzEntityDispatchCommandPacket::entityId,
        AzDispatchCommand.CODEC,
        AzEntityDispatchCommandPacket::dispatchCommand,
        AzDispatchSide.CODEC,
        AzEntityDispatchCommandPacket::origin,
        AzEntityDispatchCommandPacket::new
    );

    public void handle() {
        var entity = ClientUtils.getLevel().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        var animator = AzAnimatorAccessor.getOrNull(entity);

        if (animator != null) {
            dispatchCommand.getActions().forEach(action -> action.handle(animator));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
