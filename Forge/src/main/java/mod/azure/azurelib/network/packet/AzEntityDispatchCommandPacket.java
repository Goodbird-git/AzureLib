package mod.azure.azurelib.network.packet;

import com.sun.istack.internal.NotNull;
import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorAccessor;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.util.ClientUtils;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AzEntityDispatchCommandPacket implements IMessage, IMessageHandler<AzEntityDispatchCommandPacket, IMessage> {
    public int entityId;
    public AzCommand dispatchCommand;

    public AzEntityDispatchCommandPacket(){

    }

    public AzEntityDispatchCommandPacket(int entityId, AzCommand dispatchCommand){
        this.entityId = entityId;
        this.dispatchCommand = dispatchCommand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        dispatchCommand.toBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        entityId = buf.readInt();
        dispatchCommand = AzCommand.fromBytes(buf);
    }

    @Override
    public IMessage onMessage(AzEntityDispatchCommandPacket message, MessageContext ctx) {
        Entity entity = ClientUtils.getLevel().getEntityByID(this.entityId);

        if (entity == null) {
            return null;
        }

        AzAnimator<Entity> animator = AzAnimatorAccessor.getOrNull(entity);

        if (animator != null) {
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
        return null;
    }
}
