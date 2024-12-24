package mod.azure.azurelib.animation.dispatch.command.action.registry;

import it.unimi.dsi.fastutil.objects.Object2ShortArrayMap;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import mod.azure.azurelib.animation.dispatch.command.action.impl.root.*;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * The AzActionRegistry class serves as a centralized registry for mapping {@link AzAction}
 * implementations to their associated {@link ResourceLocation} identifiers and codecs. This registry enables efficient
 * encoding, decoding, and dispatching of animation-related actions within the animation system.
 * <br>
 * Key Responsibilities:
 * <ul>
 * <li>Maintain a bidirectional mapping between {@link ResourceLocation} identifiers and short integer IDs for efficient
 * serialization/deserialization.</li>
 * <li>Register {@link AzAction} implementations and their corresponding {@link StreamCodec} instances.</li>
 * <li>Provide methods for retrieving codecs and IDs based on resource locations or integer IDs.
 * </ul>
 */
public class AzActionRegistry {

    private static final Map<ResourceLocation, Short> RESOURCE_LOCATION_TO_ID = new Object2ShortArrayMap<>();

    private static final Map<Short, Class<? extends AzAction>> CLASS_BY_ID =
        new HashMap<>();

    private static short NEXT_FREE_ID = 0;

    static {
        // Root actions
        register(AzRootCancelAction.RESOURCE_LOCATION, AzRootCancelAction.class);
        register(AzRootCancelAllAction.RESOURCE_LOCATION, AzRootCancelAllAction.class);
        register(AzRootPlayAnimationSequenceAction.RESOURCE_LOCATION, AzRootPlayAnimationSequenceAction.class);
        register(AzRootSetAnimationSpeedAction.RESOURCE_LOCATION, AzRootSetAnimationSpeedAction.class);
        register(AzRootSetEasingTypeAction.RESOURCE_LOCATION, AzRootSetEasingTypeAction.class);
        register(AzRootSetTransitionSpeedAction.RESOURCE_LOCATION, AzRootSetTransitionSpeedAction.class);

        // Controller actions
        // TODO:

        // Animation actions
        // TODO:
    }

    public static Class<? extends AzAction> getClassOrNull(
        ResourceLocation resourceLocation
    ) {
        Short id = RESOURCE_LOCATION_TO_ID.get(resourceLocation);
        return CLASS_BY_ID.get(id);
    }

    public static Class<? extends AzAction> getClassOrNull(short id) {
        return CLASS_BY_ID.get(id);
    }

    public static Short getIdOrNull(ResourceLocation resourceLocation) {
        return RESOURCE_LOCATION_TO_ID.get(resourceLocation);
    }

    private static <A extends AzAction> void register(ResourceLocation resourceLocation, Class<? extends AzAction> clazz) {
        Short id = RESOURCE_LOCATION_TO_ID.computeIfAbsent(resourceLocation, ($) -> NEXT_FREE_ID++);
        CLASS_BY_ID.put(id, clazz);
    }
}