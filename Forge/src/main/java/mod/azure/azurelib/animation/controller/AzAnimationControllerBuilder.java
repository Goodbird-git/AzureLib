package mod.azure.azurelib.animation.controller;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyframeCallbacks;
import mod.azure.azurelib.animation.easing.AzEasingType;
import mod.azure.azurelib.animation.property.AzAnimationProperties;

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

    private final String name;

    private AzAnimationProperties animationProperties;

    private AzKeyframeCallbacks<T> keyframeCallbacks;

    public AzAnimationControllerBuilder(AzAnimator<T> animator, String name) {
        this.animator = animator;
        this.name = name;
        this.animationProperties = AzAnimationProperties.DEFAULT;
        this.keyframeCallbacks = AzKeyframeCallbacks.noop();
    }

    public AzAnimationControllerBuilder<T> setAnimationSpeed(double animationSpeed) {
        animationProperties = animationProperties.withAnimationSpeed(animationSpeed);
        return this;
    }

    public AzAnimationControllerBuilder<T> setKeyframeCallbacks(AzKeyframeCallbacks<T> keyframeCallbacks) {
        Objects.requireNonNull(keyframeCallbacks);
        this.keyframeCallbacks = keyframeCallbacks;
        return this;
    }

    public AzAnimationControllerBuilder<T> setEasingType(AzEasingType easingType) {
        animationProperties = animationProperties.withEasingType(easingType);
        return this;
    }

    public AzAnimationControllerBuilder<T> setTransitionLength(int transitionLength) {
        animationProperties = animationProperties.withTransitionLength(transitionLength);
        return this;
    }

    public AzAnimationController<T> build() {
        return new AzAnimationController<>(
            name,
            animator,
            animationProperties,
            keyframeCallbacks
        );
    }
}