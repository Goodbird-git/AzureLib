package mod.azure.azurelib.core2.animation.dispatch.command.action.codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

public class AzDispatchCommandCodec implements StreamCodec<FriendlyByteBuf, AzDispatchCommand> {

    @Override
    public @NotNull AzDispatchCommand decode(@NotNull FriendlyByteBuf byteBuf) {
        var actionCount = byteBuf.readByte();
        var actions = new ObjectArrayList<AzDispatchAction>(actionCount);

        for (int i = 0; i < actionCount; i++) {
            var action = AzDispatchAction.CODEC.decode(byteBuf);
            actions.add(action);
        }

        return new AzDispatchCommand(actions);
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf byteBuf, @NotNull AzDispatchCommand command) {
        var actions = command.getActions();
        var actionCount = actions.size();
        byteBuf.writeByte(actionCount);
        actions.forEach(action -> AzDispatchAction.CODEC.encode(byteBuf, action));
    }
}
