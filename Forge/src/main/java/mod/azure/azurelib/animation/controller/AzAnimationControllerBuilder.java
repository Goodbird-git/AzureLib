package mod.azure.azurelib.animation.controller;

import com.sun.istack.internal.NotNull;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyframeCallbacks;
import mod.azure.azurelib.animation.easing.AzEasingType;
import mod.azure.azurelib.animation.primitive.AzRawAnimation;

import java.util.Map;
import java.util.Objects;

/**
 * A builder class to construct {@link AzAnimationController} instances for managing animations in {@link AzAnimator}.
 * This provides a fluent API to configure properties such as animation speed, keyframe callbacks, easing type,
 * transition length, and triggerable animations.
 *
 * @param <T> The type of object that the animation controller will operate on.
 */
public class AzAnimationControllerBuilder<T> {

    private final AzAnimator<T> animator;

    private final AzAnimationProperties animationProperties;

    private final String name;

    /**
     * @deprecated
     */
    @Deprecated()
    private final Map<String, AzRawAnimation> triggerableAnimations;

    private AzKeyframeCallbacks<T> keyframeCallbacks;

    public AzAnimationControllerBuilder(AzAnimator<T> animator, String name) {
        this.animator = animator;
        this.name = name;
        this.animationProperties = new AzAnimationProperties();
        this.keyframeCallbacks = AzKeyframeCallbacks.noop();
        this.triggerableAnimations = new Object2ObjectOpenHashMap<>(0);
    }

    public AzAnimationControllerBuilder<T> setAnimationSpeed(double animationSpeed) {
        animationProperties.setAnimationSpeed(animationSpeed);
        return this;
    }

    public AzAnimationControllerBuilder<T> setKeyframeCallbacks(@NotNull AzKeyframeCallbacks<T> keyframeCallbacks) {
        Objects.requireNonNull(keyframeCallbacks);
        this.keyframeCallbacks = keyframeCallbacks;
        return this;
    }

    public AzAnimationControllerBuilder<T> setEasingType(AzEasingType easingType) {
        animationProperties.setEasingType(easingType);
        return this;
    }

    public AzAnimationControllerBuilder<T> setTransitionLength(int transitionLength) {
        animationProperties.setTransitionLength(transitionLength);
        return this;
    }

    /**
     * @deprecated
     */
    @Deprecated()
    public AzAnimationControllerBuilder<T> triggerableAnim(String name, AzRawAnimation animation) {
        this.triggerableAnimations.put(name, animation);
        return this;
    }

    public AzAnimationController<T> build() {
        return new AzAnimationController<>(
            name,
            animator,
            animationProperties,
            keyframeCallbacks,
            triggerableAnimations
        );
    }
}
