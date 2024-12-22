package mod.azure.azurelib.animation.controller.keyframe;

import mod.azure.azurelib.animation.controller.keyframe.handler.AzCustomKeyframeHandler;
import mod.azure.azurelib.animation.controller.keyframe.handler.AzParticleKeyframeHandler;
import mod.azure.azurelib.animation.controller.keyframe.handler.AzSoundKeyframeHandler;
import mod.azure.azurelib.animation.event.AzCustomInstructionKeyframeEvent;
import mod.azure.azurelib.animation.event.AzParticleKeyframeEvent;
import mod.azure.azurelib.animation.event.AzSoundKeyframeEvent;

/**
 * The AzKeyFrameCallbacks class manages callbacks for different types of keyframe events, enabling
 * the handling of sound, particle, and custom-defined keyframe instructions during
 * an animation sequence.
 *
 * @param <T> The type of entity or object this keyframe callback interacts with.
 */
public class AzKeyFrameCallbacks<T> {

    private static final AzKeyFrameCallbacks<?> NO_OP = new AzKeyFrameCallbacks<>(null, null, null);

    @SuppressWarnings("unchecked")
    public static <T> AzKeyFrameCallbacks<T> noop() {
        return (AzKeyFrameCallbacks<T>) NO_OP;
    }

    private final AzCustomKeyframeHandler<T> customKeyframeHandler;

    private final AzParticleKeyframeHandler<T> particleKeyframeHandler;

    private final AzSoundKeyframeHandler<T> soundKeyframeHandler;

    private AzKeyFrameCallbacks(
        AzCustomKeyframeHandler<T> customKeyframeHandler,
        AzParticleKeyframeHandler<T> particleKeyframeHandler,
        AzSoundKeyframeHandler<T> soundKeyframeHandler
    ) {
        this.customKeyframeHandler = customKeyframeHandler;
        this.particleKeyframeHandler = particleKeyframeHandler;
        this.soundKeyframeHandler = soundKeyframeHandler;
    }

    public AzCustomKeyframeHandler<T> getCustomKeyframeHandler() {
        return customKeyframeHandler;
    }

    public AzParticleKeyframeHandler<T> getParticleKeyframeHandler() {
        return particleKeyframeHandler;
    }

    public AzSoundKeyframeHandler<T> getSoundKeyframeHandler() {
        return soundKeyframeHandler;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private AzCustomKeyframeHandler<T> customKeyframeHandler;

        private AzParticleKeyframeHandler<T> particleKeyframeHandler;

        private AzSoundKeyframeHandler<T> soundKeyframeHandler;

        private Builder() {}

        /**
         * Applies the given {@link AzSoundKeyframeHandler} to this controller, for handling {@link AzSoundKeyframeEvent
         * sound keyframe instructions}.
         *
         * @return this
         */
        public Builder<T> setSoundKeyframeHandler(AzSoundKeyframeHandler<T> soundHandler) {
            this.soundKeyframeHandler = soundHandler;
            return this;
        }

        /**
         * Applies the given {@link AzParticleKeyframeHandler} to this controller, for handling
         * {@link AzParticleKeyframeEvent particle keyframe instructions}.
         *
         * @return this
         */
        public Builder<T> setParticleKeyframeHandler(AzParticleKeyframeHandler<T> particleHandler) {
            this.particleKeyframeHandler = particleHandler;
            return this;
        }

        /**
         * Applies the given {@link AzCustomKeyframeHandler} to this controller, for handling
         * {@link AzCustomInstructionKeyframeEvent sound keyframe instructions}.
         *
         * @return this
         */
        public Builder<T> setCustomInstructionKeyframeHandler(AzCustomKeyframeHandler<T> customInstructionHandler) {
            this.customKeyframeHandler = customInstructionHandler;
            return this;
        }

        public AzKeyFrameCallbacks<T> build() {
            return new AzKeyFrameCallbacks<>(customKeyframeHandler, particleKeyframeHandler, soundKeyframeHandler);
        }
    }
}
