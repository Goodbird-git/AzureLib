package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationPlayState<T> extends AzAnimationState<T> {

    public AzAnimationPlayState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        super.onEnter(context);
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.getAnimationController();
        var keyFrameProcessor = controller.getKeyFrameProcessor();
        var animContext = context.getAnimationContext();
        var timer = animContext.timer();
        var animatable = animContext.animatable();
        var animTime = timer.getAnimTime();
        var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();

        var stateMachine = context.getStateMachine();

        // Run the current animation.
        keyFrameProcessor.runCurrentAnimation(animatable, animTime, crashWhenCantFindBone);

        // Can we transition?
        var canTransition = controller.getTransitionLength() == 0 && stateMachine.shouldResetTick();

        // TODO: Remove the transition state check here, potentially.
        if (canTransition && stateMachine.isTransitioning()) {
            // Then transition.
            controller.setCurrentAnimation(controller.getAnimationQueue().next());
        }
    }

    @Override
    public void onExit(AzAnimationControllerStateMachine.Context<T> context) {
        super.onExit(context);
    }
}
