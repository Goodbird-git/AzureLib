package mod.azure.azurelib.animation.primitive;

import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.keyframe.AzBoneAnimation;

/**
 * A compiled animation instance for use by the {@link AzAnimationController}<br>
 * Modifications or extensions of a compiled Animation are not supported, and therefore an instance of
 * <code>Animation</code> is considered final and immutable.
 */
public class AzBakedAnimation {
    public final String name;
    public final double length;
    public final AzLoopType loopType;
    public final AzBoneAnimation[] boneAnimations;
    public final AzKeyframes keyframes;

    public AzBakedAnimation(String name, double length, AzLoopType loopType, AzBoneAnimation[] boneAnimations, AzKeyframes keyframes) {
        this.name = name;
        this.length = length;
        this.loopType = loopType;
        this.boneAnimations = boneAnimations;
        this.keyframes = keyframes;
    }

    public String name() {
        return name;
    }

    public double length() {
        return length;
    }

    public AzLoopType loopType() {
        return loopType;
    }

    public AzBoneAnimation[] boneAnimations() {
        return boneAnimations;
    }

    public AzKeyframes keyframes() {
        return keyframes;
    }
}
