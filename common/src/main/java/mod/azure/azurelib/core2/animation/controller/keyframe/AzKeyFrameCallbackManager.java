package mod.azure.azurelib.core2.animation.controller.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import mod.azure.azurelib.core.keyframe.event.data.KeyFrameData;
import mod.azure.azurelib.core2.animation.event.AzCustomInstructionKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzParticleKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzSoundKeyframeEvent;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;

public class AzKeyFrameCallbackManager<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzKeyFrameCallbackManager.class);

    private final AzAnimationController<T> animationController;

    private final Set<KeyFrameData> executedKeyFrames;

    public AzKeyFrameCallbackManager(AzAnimationController<T> animationController) {
        this.animationController = animationController;
        this.executedKeyFrames = new ObjectOpenHashSet<>();
    }

    public void handle(T animatable, double adjustedTick) {
        handleSoundKeyframes(animatable, adjustedTick);
        handleParticleKeyframes(animatable, adjustedTick);
        handleCustomKeyframes(animatable, adjustedTick);
    }

    private void handleCustomKeyframes(T animatable, double adjustedTick) {
        for (
            var keyframeData : getCurrentAnimation().animation()
                .keyFrames()
                .customInstructions()
        ) {
            var customKeyframeHandler = getCallbacks().getCustomKeyframeHandler();

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
        for (var keyframeData : getCurrentAnimation().animation().keyFrames().particles()) {
            var particleKeyframeHandler = getCallbacks().getParticleKeyframeHandler();

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
        for (var keyframeData : getCurrentAnimation().animation().keyFrames().sounds()) {
            var soundKeyframeHandler = getCallbacks().getSoundKeyframeHandler();

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

    private AzKeyFrameCallbacks<T> getCallbacks() {
        return animationController.getKeyFrameCallbacks();
    }

    private AzQueuedAnimation getCurrentAnimation() {
        return animationController.getCurrentAnimation();
    }

    private String getName() {
        return animationController.getName();
    }
}
