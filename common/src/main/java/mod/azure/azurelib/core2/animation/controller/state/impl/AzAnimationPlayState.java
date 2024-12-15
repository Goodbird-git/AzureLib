package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;

public final class AzAnimationPlayState<T> extends AzAnimationState<T> {

    public AzAnimationPlayState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        super.onEnter(context);
        var controller = context.getAnimationController();
        var stateMachine = context.getStateMachine();
        var animContext = context.getAnimationContext();
        var animatable = animContext.animatable();
        var animTime = animContext.timer().getAnimTime();

        stateMachine.setShouldResetTick(true);
        context.adjustedTick = controller.adjustTick(animatable, animTime);
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.getAnimationController();
        var currentAnimation = controller.getCurrentAnimation();

        if (currentAnimation == null) {
            // If the current animation is null, we should try to play the next animation.
            tryPlayNextOrStop(context);
            return;
        }

        // At this point we have an animation currently playing. We need to query if that animation has finished.

        var animContext = context.getAnimationContext();
        var animatable = animContext.animatable();
        var hasAnimationFinished = context.adjustedTick >= currentAnimation.animation().length();

        if (hasAnimationFinished) {
            tryPlayAgain(context, currentAnimation);
            // Regardless of if we should play again, the animation has finished, so return.
            return;
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
        controller.setShouldResetTick(true);
        controller.setCurrentAnimation(nextAnimation);
    }

    private void tryPlayAgain(
        AzAnimationControllerStateMachine.Context<T> context,
        AzQueuedAnimation currentAnimation
    ) {
        var animatable = context.getAnimationContext().animatable();
        var controller = context.getAnimationController();

        // If it has, we then need to see if the animation should play again.
        var shouldPlayAgain = currentAnimation.loopType()
            .shouldPlayAgain(animatable, controller, currentAnimation.animation());

        if (shouldPlayAgain) {
            // If it should play again, then we simply play the animation again.
            playAgain(context);
        }
    }

    private void playAgain(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.getAnimationController();
        var keyFrameManager = controller.getKeyFrameManager();
        var keyFrameCallbackHandler = keyFrameManager.keyFrameCallbackHandler();

        var animContext = context.getAnimationContext();
        var timer = animContext.timer();
        var animatable = animContext.animatable();
        var animTime = timer.getAnimTime();

        controller.setShouldResetTick(true);
        context.adjustedTick = controller.adjustTick(animatable, animTime);
        keyFrameCallbackHandler.reset();
    }
}
