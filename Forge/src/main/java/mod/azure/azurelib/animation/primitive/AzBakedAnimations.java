package mod.azure.azurelib.animation.primitive;

import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * Represents a container for baked animations in the AzureLib framework. This record holds mappings for precompiled
 * animation instances ({@link AzAnimation}) and resource includes ({@link ResourceLocation}) for use in
 * animation-driven content.
 * <br>
 * The `AzBakedAnimations` structure provides functionality for retrieving animations by name and supporting external
 * resource references via the includes mapping, enabling extensibility and reuse of animations across various contexts.
 * <br>
 * Immutable and designed for efficient storage and retrieval of animation data.
 */
public class AzBakedAnimations {

    public Map<String, AzAnimation> animations;
    public Map<String, ResourceLocation> includes;

    public AzBakedAnimations(Map<String, AzAnimation> animations, Map<String, ResourceLocation> includes){
        this.animations = animations;
        this.includes = includes;
    }

    public Map<String, AzAnimation> animations() {
        return animations;
    }

    public Map<String, ResourceLocation> includes() {
        return includes;
    }

    /**
     * Gets an {@link AzAnimation} by its name, if present
     */
    public AzAnimation getAnimation(String name) {
        return animations.get(name);
    }

}
