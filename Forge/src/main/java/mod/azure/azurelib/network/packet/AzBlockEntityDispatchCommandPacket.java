package mod.azure.azurelib.common.internal.common.network.packet;

import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.common.platform.services.AzureLibNetwork;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AzBlockEntityDispatchCommandPacket(
    BlockPos blockPos,
    AzDispatchCommand dispatchCommand,
    AzDispatchSide origin
) implements AbstractPacket {

    public static final CustomPacketPayload.Type<AzBlockEntityDispatchCommandPacket> TYPE = new Type<>(
        AzureLibNetwork.AZ_BLOCKENTITY_DISPATCH_COMMAND_SYNC_PACKET_ID
    );

    public static final StreamCodec<FriendlyByteBuf, AzBlockEntityDispatchCommandPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        AzBlockEntityDispatchCommandPacket::blockPos,
        AzDispatchCommand.CODEC,
        AzBlockEntityDispatchCommandPacket::dispatchCommand,
        AzDispatchSide.CODEC,
        AzBlockEntityDispatchCommandPacket::origin,
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
            dispatchCommand.getActions().forEach(action -> action.handle(animator));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
