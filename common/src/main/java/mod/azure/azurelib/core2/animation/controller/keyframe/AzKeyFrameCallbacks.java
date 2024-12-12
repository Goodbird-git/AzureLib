package mod.azure.azurelib.core2.animation.controller.keyframe;

import mod.azure.azurelib.core2.animation.controller.keyframe.handler.AzCustomKeyframeHandler;
import mod.azure.azurelib.core2.animation.controller.keyframe.handler.AzParticleKeyframeHandler;
import mod.azure.azurelib.core2.animation.controller.keyframe.handler.AzSoundKeyframeHandler;
import mod.azure.azurelib.core2.animation.event.AzCustomInstructionKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzParticleKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzSoundKeyframeEvent;

public class AzKeyFrameCallbacks<T> {

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
        public Builder<T> setCustomInstructionKeyframeHandler(
            AzCustomKeyframeHandler<T> customInstructionHandler
        ) {
            this.customKeyframeHandler = customInstructionHandler;

            return this;
        }

        public AzKeyFrameCallbacks<T> build() {
            return new AzKeyFrameCallbacks<>(
                customKeyframeHandler,
                particleKeyframeHandler,
                soundKeyframeHandler
            );
        }
    }
}
