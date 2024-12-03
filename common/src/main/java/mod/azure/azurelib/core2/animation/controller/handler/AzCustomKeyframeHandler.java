package mod.azure.azurelib.core2.animation.controller.handler;

import mod.azure.azurelib.core2.animation.event.AzCustomInstructionKeyframeEvent;

/**
 * A handler for pre-defined custom instruction keyframes. When the keyframe is encountered, the
 * {@link AzCustomKeyframeHandler#handle(AzCustomInstructionKeyframeEvent)} method will be called. You can then take
 * whatever action you want at this point.
 */
@FunctionalInterface
public interface AzCustomKeyframeHandler<A> {

    void handle(AzCustomInstructionKeyframeEvent<A> event);
}