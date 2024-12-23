package mod.azure.azurelib.common.internal.common.network.packet;


import mod.azure.azurelib.animation.AzAnimatorAccessor;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.util.ClientUtils;
import net.minecraft.util.math.BlockPos;

public record AzBlockEntityDispatchCommandPacket(
    BlockPos blockPos,
    AzDispatchCommand dispatchCommand
) implements AbstractPacket {

    public static final CustomPacketPayload.Type<AzBlockEntityDispatchCommandPacket> TYPE = new Type<>(
        AzureLibNetwork.AZ_BLOCKENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, AzBlockEntityDispatchCommandPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        AzBlockEntityDispatchCommandPacket::blockPos,
        AzDispatchCommand.CODEC,
        AzBlockEntityDispatchCommandPacket::dispatchCommand,
        AzBlockEntityDispatchCommandPacket::new
    );

    @Override
    public void handle() {
        var blockEntity = ClientUtils.getLevel().getBlockEntity(blockPos);

        if (blockEntity == null) {
            return;
        }

        var animator = AzAnimatorAccessor.getOrNull(blockEntity);

        if (animator != null) {
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
