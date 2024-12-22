package mod.azure.azurelib.animation.primitive;

import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.keyframe.AzBoneAnimation;
import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;

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

    /**
     * Generates an AzBakedAnimation instance configured as a "WAIT" animation stage with a specified length. The animation
     * will play once and has no bone animations or keyframe data.
     *
     * @param length The duration of the animation in seconds.
     * @return An AzBakedAnimation instance representing the wait animation with the specified duration.
     */
    public static AzBakedAnimation generateWaitAnimation(double length) {
        return new AzBakedAnimation(
            AzStage.WAIT,
            length,
            AzLoopType.PLAY_ONCE,
            new AzBoneAnimation[0],
            new AzKeyframes(
                new SoundKeyframeData[0],
                new ParticleKeyframeData[0],
                new CustomInstructionKeyframeData[0]
            )
        );
    }
}
