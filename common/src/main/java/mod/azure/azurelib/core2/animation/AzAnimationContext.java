package mod.azure.azurelib.core2.animation;

import mod.azure.azurelib.core2.animation.cache.AzBoneCache;

public class AzAnimationContext<T> {

    private final AzBoneCache boneCache;

    private final AzAnimatorConfig config;

    private final AzAnimationTimer timer;

    // Package-private for mutability purposes.
    T animatable;

    public AzAnimationContext(
        AzBoneCache boneCache,
        AzAnimatorConfig config,
        AzAnimationTimer timer
    ) {
        this.boneCache = boneCache;
        this.config = config;
        this.timer = timer;
    }

    public T animatable() {
        return animatable;
    }

    public AzBoneCache boneCache() {
        return boneCache;
    }

    public AzAnimatorConfig config() {
        return config;
    }

    public AzAnimationTimer timer() {
        return timer;
    }
}
