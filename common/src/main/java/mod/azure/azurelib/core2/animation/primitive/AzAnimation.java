package mod.azure.azurelib.core2.animation.primitive;

import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzBoneAnimation;

/**
 * A compiled animation instance for use by the {@link AzAnimationController}<br>
 * Modifications or extensions of a compiled Animation are not supported, and therefore an instance of
 * <code>Animation</code> is considered final and immutable.
 */
public record AzAnimation(
    String name,
    double length,
    AzLoopType loopType,
    AzBoneAnimation[] boneAnimations,
    AzKeyframes keyFrames
) {

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
            new AzBoneAnimation[0],
            new AzKeyframes(
                new SoundKeyframeData[0],
                new ParticleKeyframeData[0],
                new CustomInstructionKeyframeData[0]
            )
        );
    }
}
