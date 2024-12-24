package mod.azure.azurelib.network.packet;


import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorAccessor;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.util.ClientUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AzBlockEntityDispatchCommandPacket implements IMessage, IMessageHandler<AzBlockEntityDispatchCommandPacket, IMessage> {
    public BlockPos blockPos;
    public AzCommand dispatchCommand;

    public AzBlockEntityDispatchCommandPacket(){

    }

    public AzBlockEntityDispatchCommandPacket(BlockPos blockPos, AzCommand dispatchCommand){
        this.blockPos = blockPos;
        this.dispatchCommand = dispatchCommand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buf.writeLong(blockPos.toLong());
        dispatchCommand.toBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        blockPos = BlockPos.fromLong(buf.readLong());
        dispatchCommand = AzCommand.fromBytes(buf);
    }

    @Override
    public IMessage onMessage(AzBlockEntityDispatchCommandPacket message, MessageContext ctx) {
        TileEntity blockEntity = ClientUtils.getLevel().getTileEntity(message.blockPos);

        if (blockEntity == null) {
            return null;
        }

        AzAnimator<TileEntity> animator = AzAnimatorAccessor.getOrNull(blockEntity);

        if (animator != null) {
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
        return null;
    }
}
