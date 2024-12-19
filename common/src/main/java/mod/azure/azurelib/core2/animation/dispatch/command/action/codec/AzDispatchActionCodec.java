package mod.azure.azurelib.core2.animation.dispatch.command.action.codec;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.registry.AzDispatchActionRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class AzDispatchActionCodec implements StreamCodec<FriendlyByteBuf, AzDispatchAction> {

    @Override
    public @NotNull AzDispatchAction decode(@NotNull FriendlyByteBuf byteBuf) {
        var id = byteBuf.readShort();
        var codec = AzDispatchActionRegistry.<AzDispatchAction, StreamCodec<FriendlyByteBuf, AzDispatchAction>>getCodecOrNull(id);

        if (codec == null) {
            throw new NullPointerException("Could not find action codec for a given action id while decoding data. ID: " + id);
        }

        return codec.decode(byteBuf);
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf byteBuf, @NotNull AzDispatchAction action) {
        var resourceLocation = action.getResourceLocation();
        var id = AzDispatchActionRegistry.getIdOrNull(resourceLocation);
        var codec = AzDispatchActionRegistry.<AzDispatchAction, StreamCodec<FriendlyByteBuf, AzDispatchAction>>getCodecOrNull(resourceLocation);

        if (id == null) {
            throw new NullPointerException("Could not find action id for a given resource location while encoding data. Resource Location: " + resourceLocation);
        }

        byteBuf.writeShort(id);

        if (codec == null) {
            throw new NullPointerException("Could not find action codec for a given resource location while encoding data. Resource Location: " + resourceLocation + ", ID: " + id);
        }

        codec.encode(byteBuf, action);
    }
}
