package mod.azure.azurelib.core2.animation.controller.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import mod.azure.azurelib.core.keyframe.event.data.KeyFrameData;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.event.AzCustomInstructionKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzParticleKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzSoundKeyframeEvent;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;

/**
 * AzKeyFrameCallbackHandler acts as a handler for managing animation keyframe events such as
 * sound, particle, or custom events during a specific animation. It works in conjunction with
 * an animation controller and a set of keyframe callbacks, executing them as appropriate
 * based on the animation's progress.
 * <br>
 * This class is generic and operates on a user-defined animatable type to handle various keyframe
 * events related to animations.
 *
 * @param <T> the type of the animatable object being handled
 */
// TODO: reduce the boilerplate of the specialized handle functions in this class.
public class AzKeyFrameCallbackHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzKeyFrameCallbackHandler.class);

    private final AzAnimationController<T> animationController;

    private final Set<KeyFrameData> executedKeyFrames;

    private final AzKeyFrameCallbacks<T> keyFrameCallbacks;

    public AzKeyFrameCallbackHandler(
        AzAnimationController<T> animationController,
        AzKeyFrameCallbacks<T> keyFrameCallbacks
    ) {
        this.animationController = animationController;
        this.executedKeyFrames = new ObjectOpenHashSet<>();
        this.keyFrameCallbacks = keyFrameCallbacks;
    }

    public void handle(T animatable, double adjustedTick) {
        handleSoundKeyframes(animatable, adjustedTick);
        handleParticleKeyframes(animatable, adjustedTick);
        handleCustomKeyframes(animatable, adjustedTick);
    }

    private void handleCustomKeyframes(T animatable, double adjustedTick) {
        var customKeyframeHandler = keyFrameCallbacks.getCustomKeyframeHandler();
        var customInstructions = getCurrentAnimation().animation().keyFrames().customInstructions();

        for (var keyframeData : customInstructions) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyFrames.add(keyframeData)) {
                if (customKeyframeHandler == null) {
                    LOGGER.warn(
                        "Custom Instruction Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        getName()
                    );
                    break;
                }

                customKeyframeHandler.handle(
                    new AzCustomInstructionKeyframeEvent<>(animatable, adjustedTick, animationController, keyframeData)
                );
            }
        }
    }

    private void handleParticleKeyframes(T animatable, double adjustedTick) {
        var particleKeyframeHandler = keyFrameCallbacks.getParticleKeyframeHandler();
        var particleInstructions = getCurrentAnimation().animation().keyFrames().particles();

        for (var keyframeData : particleInstructions) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyFrames.add(keyframeData)) {
                if (particleKeyframeHandler == null) {
                    LOGGER.warn(
                        "Particle Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        getName()
                    );
                    break;
                }

                particleKeyframeHandler.handle(
                    new AzParticleKeyframeEvent<>(animatable, adjustedTick, animationController, keyframeData)
                );
            }
        }
    }

    private void handleSoundKeyframes(T animatable, double adjustedTick) {
        var soundKeyframeHandler = keyFrameCallbacks.getSoundKeyframeHandler();
        var soundInstructions = getCurrentAnimation().animation().keyFrames().sounds();

        for (var keyframeData : soundInstructions) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyFrames.add(keyframeData)) {
                if (soundKeyframeHandler == null) {
                    LOGGER.warn(
                        "Sound Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        getName()
                    );
                    break;
                }

                soundKeyframeHandler.handle(
                    new AzSoundKeyframeEvent<>(animatable, adjustedTick, animationController, keyframeData)
                );
            }
        }
    }

    /**
     * Clear the {@link KeyFrameData} cache in preparation for the next animation
     */
    public void reset() {
        executedKeyFrames.clear();
    }

    private AzQueuedAnimation getCurrentAnimation() {
        return animationController.getCurrentAnimation();
    }

    private String getName() {
        return animationController.getName();
    }
}
