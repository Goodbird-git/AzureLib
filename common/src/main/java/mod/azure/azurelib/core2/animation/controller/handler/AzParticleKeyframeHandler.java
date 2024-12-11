package mod.azure.azurelib.core2.animation.controller.handler;

import mod.azure.azurelib.core2.animation.event.AzParticleKeyframeEvent;

/**
 * A handler for when a predefined particle keyframe is hit. When the keyframe is encountered, the
 * {@link AzParticleKeyframeHandler#handle(AzParticleKeyframeEvent)} method will be called. Spawn the particles/effects
 * of your choice at this time.
 */
@FunctionalInterface
public interface AzParticleKeyframeHandler<A> {

    void handle(AzParticleKeyframeEvent<A> event);
}
