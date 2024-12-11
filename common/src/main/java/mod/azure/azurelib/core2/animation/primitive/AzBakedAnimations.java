package mod.azure.azurelib.core2.animation.primitive;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Container object that holds a deserialized map of {@link AzAnimation Animations}.<br>
 * Kept as a unique object so that it can be registered as a {@link com.google.gson.JsonDeserializer deserializer} for
 * {@link com.google.gson.Gson Gson}
 */
public record AzBakedAnimations(
    Map<String, AzAnimation> animations,
    Map<String, ResourceLocation> includes
) {

    /**
     * Gets an {@link AzAnimation} by its name, if present
     */
    @Nullable
    public AzAnimation getAnimation(String name) {
        return animations.get(name);
    }

}
