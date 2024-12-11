package mod.azure.azurelib.core2.animation.controller.handler;

import mod.azure.azurelib.core2.animation.event.AzSoundKeyframeEvent;

/**
 * A handler for when a predefined sound keyframe is hit. When the keyframe is encountered, the
 * {@link AzSoundKeyframeHandler#handle(AzSoundKeyframeEvent)} method will be called. Play the sound(s) of your choice
 * at this time.
 */
@FunctionalInterface
public interface AzSoundKeyframeHandler<A> {

    void handle(AzSoundKeyframeEvent<A> event);
}
