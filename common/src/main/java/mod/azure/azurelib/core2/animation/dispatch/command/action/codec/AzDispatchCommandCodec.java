package mod.azure.azurelib.core2.animation.dispatch.command.action.codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

/**
 * The AzDispatchCommandCodec class provides a codec implementation for encoding and decoding {@link AzDispatchCommand}
 * objects, facilitating their transmission or storage in binary data formats. This codec is designed to work with the
 * {@link FriendlyByteBuf} stream provided by Minecraft's network API.
 * <br>
 * The AzDispatchCommandCodec class utilizes nested {@link AzDispatchAction} codecs to manage individual action encoding
 * and decoding, ensuring the integrity of the data structure during serialization and deserialization processes.
 * <br>
 * The encode method serializes an {@link AzDispatchCommand} instance, including all associated actions, into a data
 * stream. Conversely, the decode method reconstructs an {@link AzDispatchCommand} instance from a provided data stream,
 * identifying and deserializing its actions using the appropriate codecs.
 * <br>
 * This implementation is integral for transferring or storing animation commands within an animation system, ensuring
 * compatibility and proper handling of command actions.
 */
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
