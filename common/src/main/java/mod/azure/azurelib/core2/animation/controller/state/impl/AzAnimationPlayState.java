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
        var keyFrameProcessor = controller.getKeyFrameManager().getKeyFrameProcessor();
        var animContext = context.getAnimationContext();
        var timer = animContext.timer();

        var animatable = animContext.animatable();
        var animTime = timer.getAnimTime();
        var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();

        // Run the current animation.
        keyFrameProcessor.runCurrentAnimation(animatable, animTime, crashWhenCantFindBone);
    }

    @Override
    public void onExit(AzAnimationControllerStateMachine.Context<T> context) {
        super.onExit(context);
    }
}
