package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationTransitionState<T> extends AzAnimationState<T> {

    public AzAnimationTransitionState() {}

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
        var boneSnapshotCache = controller.getBoneSnapshotCache();
        var animContext = context.getAnimationContext();

        var stateMachine = context.getStateMachine();
        var boneCache = animContext.boneCache();

        if (controllerTimer.getAdjustedTick() == 0) {
            controller.setCurrentAnimation(controller.getAnimationQueue().next());

            controller.getKeyFrameManager().keyFrameCallbackHandler().reset();

            if (controller.getCurrentAnimation() == null) {
                return;
            }

            var snapshots = boneCache.getBoneSnapshotsByName();

            boneSnapshotCache.put(controller.getCurrentAnimation(), snapshots.values());
        }

        var hasFinishedTransitioning = controllerTimer.getAdjustedTick() >= controller.getTransitionLength();

        if (hasFinishedTransitioning) {
            // If we've exceeded the amount of time we should be transitioning, then switch to play state.
            stateMachine.play();
            return;
        }

        if (controller.getCurrentAnimation() != null) {
            var bones = boneCache.getBakedModel().getBonesByName();
            var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();
            var keyFrameTransitioner = controller.getKeyFrameManager().getKeyFrameTransitioner();

            keyFrameTransitioner.transition(bones, crashWhenCantFindBone, controllerTimer.getAdjustedTick());
        }
    }
}
