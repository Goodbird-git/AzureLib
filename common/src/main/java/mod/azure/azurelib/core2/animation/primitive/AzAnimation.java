package mod.azure.azurelib.core2.animation.primitive;

import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;
import mod.azure.azurelib.core2.animation.AzBoneAnimation;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;

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
