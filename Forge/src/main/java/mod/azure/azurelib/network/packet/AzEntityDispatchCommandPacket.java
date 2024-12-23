package mod.azure.azurelib.common.internal.common.network.packet;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.animation.AzAnimatorAccessor;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.util.ClientUtils;

public record AzEntityDispatchCommandPacket(
    int entityId,
    AzDispatchCommand dispatchCommand
) implements AbstractPacket {

    public static final Type<AzEntityDispatchCommandPacket> TYPE = new Type<>(
        AzureLibNetwork.AZ_ENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, AzEntityDispatchCommandPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        AzEntityDispatchCommandPacket::entityId,
        AzDispatchCommand.CODEC,
        AzEntityDispatchCommandPacket::dispatchCommand,
        AzEntityDispatchCommandPacket::new
    );

    public void handle() {
        var entity = ClientUtils.getLevel().getEntity(this.entityId);

        if (entity == null) {
            return;
        }

        var animator = AzAnimatorAccessor.getOrNull(entity);

        if (animator != null) {
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
