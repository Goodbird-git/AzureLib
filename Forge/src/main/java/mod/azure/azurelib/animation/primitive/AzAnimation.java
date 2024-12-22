package mod.azure.azurelib.animation.primitive;

import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.core.keyframe.BoneAnimation;
import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;

/**
 * A compiled animation instance for use by the {@link AzAnimationController}<br>
 * Modifications or extensions of a compiled Animation are not supported, and therefore an instance of
 * <code>Animation</code> is considered final and immutable.
 */
public class AzAnimation {
    public final String name;
    public final double length;
    public final AzLoopType loopType;
    public final BoneAnimation[] boneAnimations;
    public final AzKeyframes keyFrames;

    public AzAnimation(String name, double length, AzLoopType loopType, BoneAnimation[] boneAnimations, AzKeyframes keyFrames) {
        this.name = name;
        this.length = length;
        this.loopType = loopType;
        this.boneAnimations = boneAnimations;
        this.keyFrames = keyFrames;
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

    public BoneAnimation[] boneAnimations() {
        return boneAnimations;
    }

    public AzKeyframes keyFrames() {
        return keyFrames;
    }

    /**
     * Generates an AzAnimation instance configured as a "WAIT" animation stage with a specified length. The animation
     * will play once and has no bone animations or keyframe data.
     *
     * @param length The duration of the animation in seconds.
     * @return An AzAnimation instance representing the wait animation with the specified duration.
     */
    public static AzAnimation generateWaitAnimation(double length) {
        return new AzAnimation(
            AzStage.WAIT,
            length,
            AzLoopType.PLAY_ONCE,
            new BoneAnimation[0],
            new AzKeyframes(
                new SoundKeyframeData[0],
                new ParticleKeyframeData[0],
                new CustomInstructionKeyframeData[0]
            )
        );
    }
}
