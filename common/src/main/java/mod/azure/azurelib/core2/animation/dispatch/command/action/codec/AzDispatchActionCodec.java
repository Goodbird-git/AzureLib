package mod.azure.azurelib.core2.animation.dispatch.command.action.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.registry.AzDispatchActionRegistry;

/**
 * The AzDispatchActionCodec class serves as an implementation of the {@link StreamCodec} interface specifically
 * designed for encoding and decoding {@link AzDispatchAction} objects. This codec encodes and decodes AzDispatchAction
 * instances using their associated resource locations and registered codecs within the
 * {@link AzDispatchActionRegistry}. </br>
 * </br>
 * This class provides the necessary functionality to serialize an AzDispatchAction to a {@link FriendlyByteBuf} and
 * deserialize it back, ensuring proper handling of resource location and associated data. It relies on the
 * AzDispatchActionRegistry to dynamically retrieve the appropriate codec and handle the serialization or
 * deserialization process. </br>
 * </br>
 * Use this implementation in scenarios where AzDispatchAction objects need to be serialized or deserialized for
 * efficient data transmission or storage.
 */
public class AzDispatchActionCodec implements StreamCodec<FriendlyByteBuf, AzDispatchAction> {

    @Override
    public @NotNull AzDispatchAction decode(@NotNull FriendlyByteBuf byteBuf) {
        var id = byteBuf.readShort();
        var codec = AzDispatchActionRegistry
            .<AzDispatchAction, StreamCodec<FriendlyByteBuf, AzDispatchAction>>getCodecOrNull(id);

        if (codec == null) {
            throw new NullPointerException(
                "Could not find action codec for a given action id while decoding data. ID: " + id
            );
        }

        return codec.decode(byteBuf);
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf byteBuf, @NotNull AzDispatchAction action) {
        var resourceLocation = action.getResourceLocation();
        var id = AzDispatchActionRegistry.getIdOrNull(resourceLocation);
        var codec = AzDispatchActionRegistry
            .<AzDispatchAction, StreamCodec<FriendlyByteBuf, AzDispatchAction>>getCodecOrNull(resourceLocation);

        if (id == null) {
            throw new NullPointerException(
                "Could not find action id for a given resource location while encoding data. Resource Location: "
                    + resourceLocation
            );
        }

        byteBuf.writeShort(id);

        if (codec == null) {
            throw new NullPointerException(
                "Could not find action codec for a given resource location while encoding data. Resource Location: "
                    + resourceLocation + ", ID: " + id
            );
        }

        codec.encode(byteBuf, action);
    }
}
