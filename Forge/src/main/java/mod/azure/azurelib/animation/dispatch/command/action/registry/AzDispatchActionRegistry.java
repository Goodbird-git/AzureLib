package mod.azure.azurelib.animation.dispatch.command.action.registry;

import it.unimi.dsi.fastutil.objects.Object2ShortArrayMap;
import mod.azure.azurelib.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.animation.dispatch.command.action.impl.root.*;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The AzDispatchActionRegistry class serves as a centralized registry for mapping {@link AzDispatchAction}
 * implementations to their associated {@link ResourceLocation} identifiers and codecs. This registry enables efficient
 * encoding, decoding, and dispatching of animation-related actions within the animation system.
 * <br>
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
        register(AzRootSetAnimationSpeedAction.RESOURCE_LOCATION, AzRootSetAnimationSpeedAction.CODEC);
        register(AzRootSetEasingTypeAction.RESOURCE_LOCATION, AzRootSetEasingTypeAction.CODEC);
        register(AzRootSetTransitionInSpeedAction.RESOURCE_LOCATION, AzRootSetTransitionInSpeedAction.CODEC);
        register(AzRootPlayAnimationAction.RESOURCE_LOCATION, AzRootPlayAnimationAction.CODEC);

        // Controller actions
        // TODO:

        // Animation actions
        // TODO:
    }

    public static <A, T extends StreamCodec<FriendlyByteBuf, A>> T getCodecOrNull(
        ResourceLocation resourceLocation
    ) {
        Short id = RESOURCE_LOCATION_TO_ID.get(resourceLocation);
        @SuppressWarnings("unchecked")
        T codec = (T) CODEC_BY_ID.get(id);
        return codec;
    }

    public static <A, T extends StreamCodec<FriendlyByteBuf, A>> T getCodecOrNull(short id) {
        @SuppressWarnings("unchecked")
        T codec = (T) CODEC_BY_ID.get(id);
        return codec;
    }

    public static Short getIdOrNull(ResourceLocation resourceLocation) {
        return RESOURCE_LOCATION_TO_ID.get(resourceLocation);
    }

    private static <A extends AzDispatchAction> void register(
        ResourceLocation resourceLocation,
        StreamCodec<FriendlyByteBuf, A> codec
    ) {
        Short id = RESOURCE_LOCATION_TO_ID.computeIfAbsent(resourceLocation, ($) -> NEXT_FREE_ID++);
        CODEC_BY_ID.put(id, codec);
    }
}
