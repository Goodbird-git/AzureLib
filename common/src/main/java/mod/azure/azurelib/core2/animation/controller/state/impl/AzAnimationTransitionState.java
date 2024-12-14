package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationTransitionState<T> extends AzAnimationState<T> {

    public AzAnimationTransitionState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        super.onEnter(context);
        context.getStateMachine().setShouldResetTick(true);
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.getAnimationController();
        var boneSnapshotCache = controller.getBoneSnapshotCache();
        var animContext = context.getAnimationContext();
        var timer = animContext.timer();
        var animatable = animContext.animatable();
        var animTime = timer.getAnimTime();

        var stateMachine = context.getStateMachine();
        var boneCache = animContext.boneCache();

        if (context.adjustedTick >= controller.getTransitionLength()) {
            // If we've exceeded the amount of time we should be transitioning, then switch to play state.

            stateMachine.setShouldResetTick(true);
            stateMachine.play();
            context.adjustedTick = controller.adjustTick(animatable, animTime);
            return;
        }

        if (stateMachine.shouldResetTick() /* || justStopped */) {
            // TODO:
            context.adjustedTick = controller.adjustTick(animatable, animTime);
        }

        if (context.adjustedTick == 0 || stateMachine.isJustStarting()) {
            // FIXME: POTENTIAL REGRESSION
            // this.justStartedTransition = false;
            controller.setCurrentAnimation(controller.getAnimationQueue().next());

            controller.getKeyFrameCallbackManager().reset();

            if (controller.getCurrentAnimation() == null) {
                return;
            }

            var snapshots = boneCache.getBoneSnapshotsByName();

            boneSnapshotCache.put(controller.getCurrentAnimation(), snapshots.values());
        }

        if (controller.getCurrentAnimation() != null) {
            var bones = boneCache.getBakedModel().getBonesByName();
            var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();
            var keyFrameProcessor = controller.getKeyFrameProcessor();
            keyFrameProcessor.transitionFromCurrentAnimation(bones, crashWhenCantFindBone, context.adjustedTick);
        }
    }

    @Override
    public void onExit(AzAnimationControllerStateMachine.Context<T> context) {
        super.onExit(context);
    }
}
