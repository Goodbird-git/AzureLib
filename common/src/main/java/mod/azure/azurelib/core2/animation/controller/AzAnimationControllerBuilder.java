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

public class AzAnimationControllerBuilder<T> {

    private final AzAnimator<T> animator;

    private final String name;

    private final Map<String, AzRawAnimation> triggerableAnimations;

    private ToDoubleFunction<T> animationSpeedModifier;

    private AzKeyFrameCallbacks<T> keyFrameCallbacks;

    private Function<T, EasingType> overrideEasingTypeFunction;

    private int transitionLength;

    public AzAnimationControllerBuilder(AzAnimator<T> animator, String name) {
        this.animator = animator;
        this.name = name;
        this.animationSpeedModifier = obj -> 1d;
        this.keyFrameCallbacks = AzKeyFrameCallbacks.noop();
        this.overrideEasingTypeFunction = obj -> null;
        this.transitionLength = 0;
        this.triggerableAnimations = new Object2ObjectOpenHashMap<>(0);
    }

    public AzAnimationControllerBuilder<T> setAnimationSpeed(double speed) {
        return setAnimationSpeedHandler(obj -> speed);
    }

    public AzAnimationControllerBuilder<T> setAnimationSpeedHandler(ToDoubleFunction<T> speedModFunction) {
        this.animationSpeedModifier = speedModFunction;
        return this;
    }

    public AzAnimationControllerBuilder<T> setKeyFrameCallbacks(@NotNull AzKeyFrameCallbacks<T> keyFrameCallbacks) {
        Objects.requireNonNull(keyFrameCallbacks);
        this.keyFrameCallbacks = keyFrameCallbacks;
        return this;
    }

    public AzAnimationControllerBuilder<T> setOverrideEasingType(EasingType easingTypeFunction) {
        return setOverrideEasingTypeFunction(obj -> easingTypeFunction);
    }

    public AzAnimationControllerBuilder<T> setOverrideEasingTypeFunction(Function<T, EasingType> easingType) {
        this.overrideEasingTypeFunction = easingType;
        return this;
    }

    public AzAnimationControllerBuilder<T> setTransitionLength(int transitionLength) {
        this.transitionLength = transitionLength;
        return this;
    }

    public AzAnimationControllerBuilder<T> triggerableAnim(String name, AzRawAnimation animation) {
        this.triggerableAnimations.put(name, animation);
        return this;
    }

    public AzAnimationController<T> build() {
        return new AzAnimationController<>(
            name,
            animator,
            transitionLength,
            animationSpeedModifier,
            keyFrameCallbacks,
            overrideEasingTypeFunction,
            triggerableAnimations
        );
    }
}
