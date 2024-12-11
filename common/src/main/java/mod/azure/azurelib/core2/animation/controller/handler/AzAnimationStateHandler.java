package mod.azure.azurelib.core2.animation.controller.handler;

import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.core2.animation.AzAnimationState;

/**
 * Every render frame, the {@code AzAnimationController} will call this handler for <u>each</u> animatable that is being
 * rendered. This handler defines which animation should be currently playing, and returning a {@link PlayState} to tell
 * the controller what to do next.<br>
 * Example Usage:<br>
 *
 * <pre>{@code
 *
 * AzAnimationFrameHandler myIdleWalkHandler = state -> {
 *     if (state.isMoving()) {
 *         state.getController().setAnimation(myWalkAnimation);
 *     } else {
 *         state.getController().setAnimation(myIdleAnimation);
 *     }
 *
 *     return PlayState.CONTINUE;
 * };
 * }</pre>
 */
@FunctionalInterface
public interface AzAnimationStateHandler<A> {

    /**
     * The handling method, called each frame. Return {@link PlayState#CONTINUE} to tell the controller to continue
     * animating, or return {@link PlayState#STOP} to tell it to stop playing all animations and wait for the next
     * {@code PlayState.CONTINUE} return.
     */
    PlayState handle(AzAnimationState<A> state);
}
