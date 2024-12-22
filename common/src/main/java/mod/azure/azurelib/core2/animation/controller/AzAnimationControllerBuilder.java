package mod.azure.azurelib.core2.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbacks;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

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

    private final Map<String, AzRawAnimation> triggerableAnimations;

    private AzKeyFrameCallbacks<T> keyFrameCallbacks;

    public AzAnimationControllerBuilder(AzAnimator<T> animator, String name) {
        this.animator = animator;
        this.name = name;
        this.animationProperties = new AzAnimationProperties();
        this.keyFrameCallbacks = AzKeyFrameCallbacks.noop();
        this.triggerableAnimations = new Object2ObjectOpenHashMap<>(0);
    }

    public AzAnimationControllerBuilder<T> setAnimationSpeed(double animationSpeed) {
        animationProperties.setAnimationSpeed(animationSpeed);
        return this;
    }

    public AzAnimationControllerBuilder<T> setKeyFrameCallbacks(@NotNull AzKeyFrameCallbacks<T> keyFrameCallbacks) {
        Objects.requireNonNull(keyFrameCallbacks);
        this.keyFrameCallbacks = keyFrameCallbacks;
        return this;
    }

    public AzAnimationControllerBuilder<T> setOverrideEasingType(EasingType easingType) {
        animationProperties.setEasingType(easingType);
        return this;
    }

    public AzAnimationControllerBuilder<T> setTransitionLength(int transitionLength) {
        animationProperties.setTransitionLength(transitionLength);
        return this;
    }

    public AzAnimationControllerBuilder<T> triggerableAnim(String name, AzRawAnimation animation) {
        this.triggerableAnimations.put(name, animation);
        return this;
    }

    public AzAnimationController<T> build() {
        return new AzAnimationController<>(name, animator, animationProperties, keyFrameCallbacks, triggerableAnimations);
    }
}
