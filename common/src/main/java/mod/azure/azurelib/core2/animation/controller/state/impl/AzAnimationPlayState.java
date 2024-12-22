package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;

/**
 * Represents a "play" state in an animation state machine. This state is responsible for managing the playing of
 * animations either by starting from the beginning or playing subsequent animations. It ensures that the animation
 * progresses based on the controller's timer and handles transitions when animations complete. <br/>
 * <br/>
 * Inherits general animation state behavior such as lifecycle management from {@link AzAnimationState}.
 *
 * @param <T> the type of animation being managed
 */
public class AzAnimationPlayState<T> extends AzAnimationState<T> {

    public AzAnimationPlayState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        super.onEnter(context);
        var controller = context.getAnimationController();
        var controllerTimer = controller.getControllerTimer();

        controllerTimer.reset();
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.getAnimationController();
        var controllerTimer = controller.getControllerTimer();
        var currentAnimation = controller.getCurrentAnimation();

        if (currentAnimation == null) {
            // If the current animation is null, we should try to play the next animation.
            tryPlayNextOrStop(context);
            return;
        }

        // At this point we have an animation currently playing. We need to query if that animation has finished.

        var animContext = context.getAnimationContext();
        var animatable = animContext.animatable();
        var hasAnimationFinished = controllerTimer.getAdjustedTick() >= currentAnimation.animation().length();

        if (hasAnimationFinished) {
            var shouldPlayAgain = shouldPlayAgain(context, currentAnimation);

            if (shouldPlayAgain) {
                // If it should play again, then we simply play the animation again.
                playAgain(context);
            } else {
                // Nothing more to do at this point since we can't play the animation again, so stop.
                context.getStateMachine().stop();
                return;
            }
        }

        // The animation is still running at this point, proceed with updating the bones according to keyframes.

        var keyFrameManager = controller.getKeyFrameManager();
        var keyFrameExecutor = keyFrameManager.getKeyFrameExecutor();
        var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();

        keyFrameExecutor.execute(currentAnimation, animatable, crashWhenCantFindBone);
    }

    private void tryPlayNextOrStop(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.getAnimationController();
        var stateMachine = context.getStateMachine();
        var keyFrameManager = controller.getKeyFrameManager();
        var keyFrameCallbackHandler = keyFrameManager.keyFrameCallbackHandler();

        keyFrameCallbackHandler.reset();

        var animationQueue = controller.getAnimationQueue();
        var nextAnimation = animationQueue.peek();
        var canPlayNextSuccessfully = nextAnimation != null;

        if (!canPlayNextSuccessfully) {
            // If we can't play the next animation for some reason, then there's nothing to play.
            // So we should put the state machine in the 'stop' state.
            stateMachine.stop();
            return;
        }

        // If we can play the next animation successfully, then let's do that.
        stateMachine.transition();
        controller.setCurrentAnimation(nextAnimation);
    }

    private boolean shouldPlayAgain(
        AzAnimationControllerStateMachine.Context<T> context,
        AzQueuedAnimation currentAnimation
    ) {
        var animatable = context.getAnimationContext().animatable();
        var controller = context.getAnimationController();

        // If it has, we then need to see if the animation should play again.
        return currentAnimation.loopType()
            .shouldPlayAgain(animatable, controller, currentAnimation.animation());
    }

    protected void playAgain(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.getAnimationController();
        var controllerTimer = controller.getControllerTimer();
        var keyFrameManager = controller.getKeyFrameManager();
        var keyFrameCallbackHandler = keyFrameManager.keyFrameCallbackHandler();

        controllerTimer.reset();
        keyFrameCallbackHandler.reset();
    }
}
