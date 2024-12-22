package mod.azure.azurelib.animation.controller.state.impl;

import mod.azure.azurelib.animation.AzAnimationContext;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerTimer;
import mod.azure.azurelib.animation.controller.AzAnimationQueue;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyFrameCallbackHandler;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyFrameExecutor;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyFrameManager;
import mod.azure.azurelib.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.animation.controller.state.machine.Context;
import mod.azure.azurelib.animation.primitive.AzQueuedAnimation;

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
    public void onEnter(Context<T> context) {
        super.onEnter(context);
        AzAnimationController<T> controller = context.getAnimationController();
        AzAnimationControllerTimer<T> controllerTimer = controller.getControllerTimer();

        controllerTimer.reset();
    }

    @Override
    public void onUpdate(Context<T> context) {
        AzAnimationController<T> controller = context.getAnimationController();
        AzAnimationControllerTimer<T> controllerTimer = controller.getControllerTimer();
        AzQueuedAnimation currentAnimation = controller.getCurrentAnimation();

        if (currentAnimation == null) {
            // If the current animation is null, we should try to play the next animation.
            tryPlayNextOrStop(context);
            return;
        }

        // At this point we have an animation currently playing. We need to query if that animation has finished.

        AzAnimationContext<T> animContext = context.getAnimationContext();
        T animatable = animContext.animatable();
        boolean hasAnimationFinished = controllerTimer.getAdjustedTick() >= currentAnimation.animation().length();

        if (hasAnimationFinished) {
            boolean shouldPlayAgain = shouldPlayAgain(context, currentAnimation);

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

        AzKeyFrameManager<T> keyFrameManager = controller.getKeyFrameManager();
        AzKeyFrameExecutor<T> keyFrameExecutor = keyFrameManager.getKeyFrameExecutor();
        boolean crashWhenCantFindBone = animContext.config().crashIfBoneMissing();

        keyFrameExecutor.execute(currentAnimation, animatable, crashWhenCantFindBone);
    }

    private void tryPlayNextOrStop(Context<T> context) {
        AzAnimationController<T> controller = context.getAnimationController();
        AzAnimationControllerStateMachine<T> stateMachine = context.getStateMachine();
        AzKeyFrameManager<T> keyFrameManager = controller.getKeyFrameManager();
        AzKeyFrameCallbackHandler<T> keyFrameCallbackHandler = keyFrameManager.keyFrameCallbackHandler();

        keyFrameCallbackHandler.reset();

        AzAnimationQueue animationQueue = controller.getAnimationQueue();
        AzQueuedAnimation nextAnimation = animationQueue.peek();
        boolean canPlayNextSuccessfully = nextAnimation != null;

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
        Context<T> context,
        AzQueuedAnimation currentAnimation
    ) {
        T animatable = context.getAnimationContext().animatable();
        AzAnimationController<T> controller = context.getAnimationController();

        // If it has, we then need to see if the animation should play again.
        return currentAnimation.loopType()
            .shouldPlayAgain(animatable, controller, currentAnimation.animation());
    }

    protected void playAgain(Context<T> context) {
        AzAnimationController<T> controller = context.getAnimationController();
        AzAnimationControllerTimer<T> controllerTimer = controller.getControllerTimer();
        AzKeyFrameManager<T> keyFrameManager = controller.getKeyFrameManager();
        AzKeyFrameCallbackHandler<T> keyFrameCallbackHandler = keyFrameManager.keyFrameCallbackHandler();

        controllerTimer.reset();
        keyFrameCallbackHandler.reset();
    }
}
