package mod.azure.azurelib.network.packet;

import com.sun.istack.internal.NotNull;
import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.animation.cache.AzIdentifiableItemStackAnimatorCache;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.AzCommand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class AzItemStackDispatchCommandPacket implements IMessage, IMessageHandler<AzItemStackDispatchCommandPacket, IMessage> {
    public UUID itemStackId;
    public AzCommand dispatchCommand;

    public AzItemStackDispatchCommandPacket(){

    }

    public AzItemStackDispatchCommandPacket(UUID itemStackId, AzCommand dispatchCommand){
        this.itemStackId = itemStackId;
        this.dispatchCommand = dispatchCommand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, itemStackId.toString());
        dispatchCommand.toBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        itemStackId = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        dispatchCommand = AzCommand.fromBytes(buf);
    }

    @Override
    public IMessage onMessage(AzItemStackDispatchCommandPacket message, MessageContext ctx) {
        mod.azure.azurelib.animation.impl.AzItemAnimator animator = AzIdentifiableItemStackAnimatorCache.getInstance().getOrNull(itemStackId);

        if (animator != null) {
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
        return null;
    }
}
