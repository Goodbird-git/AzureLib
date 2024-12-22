package mod.azure.azurelib.core2.animation.dispatch.command.action.registry;

import it.unimi.dsi.fastutil.objects.Object2ShortArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAllAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootPlayAnimationAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootSetTransitionInSpeedAction;

/**
 * The AzDispatchActionRegistry class serves as a centralized registry for mapping {@link AzDispatchAction}
 * implementations to their associated {@link ResourceLocation} identifiers and codecs. This registry enables efficient
 * encoding, decoding, and dispatching of animation-related actions within the animation system. </br>
 * </br>
 * Key Responsibilities:
 * <ul>
 * <li>Maintain a bidirectional mapping between {@link ResourceLocation} identifiers and short integer IDs for efficient
 * serialization/deserialization.</li>
 * <li>Register {@link AzDispatchAction} implementations and their corresponding {@link StreamCodec} instances.</li>
 * <li>Provide methods for retrieving codecs and IDs based on resource locations or integer IDs.
 * </ul>
 */
public class AzDispatchActionRegistry {

    private static final Map<ResourceLocation, Short> RESOURCE_LOCATION_TO_ID = new Object2ShortArrayMap<>();

    private static final Map<Short, StreamCodec<FriendlyByteBuf, ? extends AzDispatchAction>> CODEC_BY_ID =
        new HashMap<>();

    private static short NEXT_FREE_ID = 0;

    static {
        // Root actions
        register(AzRootCancelAction.RESOURCE_LOCATION, AzRootCancelAction.CODEC);
        register(AzRootCancelAllAction.RESOURCE_LOCATION, AzRootCancelAllAction.CODEC);
        register(AzRootSetTransitionInSpeedAction.RESOURCE_LOCATION, AzRootSetTransitionInSpeedAction.CODEC);
        register(AzRootPlayAnimationAction.RESOURCE_LOCATION, AzRootPlayAnimationAction.CODEC);

        // Controller actions
        // TODO:

        // Animation actions
        // TODO:
    }

    public static @Nullable <A, T extends StreamCodec<FriendlyByteBuf, A>> T getCodecOrNull(
        ResourceLocation resourceLocation
    ) {
        var id = RESOURCE_LOCATION_TO_ID.get(resourceLocation);
        @SuppressWarnings("unchecked")
        var codec = (T) CODEC_BY_ID.get(id);
        return codec;
    }

    public static @Nullable <A, T extends StreamCodec<FriendlyByteBuf, A>> T getCodecOrNull(short id) {
        @SuppressWarnings("unchecked")
        var codec = (T) CODEC_BY_ID.get(id);
        return codec;
    }

    public static @Nullable Short getIdOrNull(ResourceLocation resourceLocation) {
        return RESOURCE_LOCATION_TO_ID.get(resourceLocation);
    }

    private static <A extends AzDispatchAction> void register(
        ResourceLocation resourceLocation,
        StreamCodec<FriendlyByteBuf, A> codec
    ) {
        var id = RESOURCE_LOCATION_TO_ID.computeIfAbsent(resourceLocation, ($) -> NEXT_FREE_ID++);
        CODEC_BY_ID.put(id, codec);
    }
}
