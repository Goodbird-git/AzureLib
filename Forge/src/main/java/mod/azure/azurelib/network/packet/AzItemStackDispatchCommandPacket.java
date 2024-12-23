package mod.azure.azurelib.common.internal.common.network.packet;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.animation.cache.AzIdentifiableItemStackAnimatorCache;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.AzDispatchCommand;

import java.util.UUID;

public record AzItemStackDispatchCommandPacket(
    UUID itemStackId,
    AzDispatchCommand dispatchCommand
) implements AbstractPacket {

    public static final Type<AzItemStackDispatchCommandPacket> TYPE = new Type<>(
        AzureLibNetwork.AZ_ITEM_STACK_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, AzItemStackDispatchCommandPacket> CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC,
        AzItemStackDispatchCommandPacket::itemStackId,
        AzDispatchCommand.CODEC,
        AzItemStackDispatchCommandPacket::dispatchCommand,
        AzItemStackDispatchCommandPacket::new
    );

    public void handle() {
        var animator = AzIdentifiableItemStackAnimatorCache.getInstance().getOrNull(itemStackId);

        if (animator != null) {
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
