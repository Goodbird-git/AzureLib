package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;

/**
 * Represents a transition state in an animation state machine. This state is responsible for managing the transition
 * between animations, including handling setup, updates, and transitioning to the appropriate play state when the
 * transition is complete. The `AzAnimationTransitionState` extends the functionality of `AzAnimationState` to implement
 * the behavior specific to transitioning between animations. It resets timers, initializes animations, and updates
 * keyframes to create smooth transitions.
 *
 * @param <T> the type of the animation context associated with this state
 */
public final class AzAnimationTransitionState<T> extends AzAnimationState<T> {

    public AzAnimationTransitionState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        super.onEnter(context);
        var controller = context.animationController();
        var controllerTimer = controller.controllerTimer();
        controllerTimer.reset();
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        var controller = context.animationController();
        var controllerTimer = controller.controllerTimer();
        var boneSnapshotCache = controller.boneSnapshotCache();
        var animContext = context.animationContext();

        var stateMachine = context.stateMachine();
        var boneCache = animContext.boneCache();

        if (controllerTimer.getAdjustedTick() == 0) {
            controller.setCurrentAnimation(controller.animationQueue().next());

            controller.keyframeManager().keyframeCallbackHandler().reset();

            if (controller.currentAnimation() == null) {
                return;
            }

            var snapshots = boneCache.getBoneSnapshotsByName();

            boneSnapshotCache.put(controller.currentAnimation(), snapshots.values());
        }

        var hasFinishedTransitioning = controllerTimer.getAdjustedTick() >= controller.animationProperties()
            .transitionLength();

        if (hasFinishedTransitioning) {
            // If we've exceeded the amount of time we should be transitioning, then switch to play state.
            stateMachine.play();
            return;
        }

        if (controller.currentAnimation() != null) {
            var bones = boneCache.getBakedModel().getBonesByName();
            var crashWhenCantFindBone = animContext.config().crashIfBoneMissing();
            var keyframeTransitioner = controller.keyframeManager().keyframeTransitioner();

            keyframeTransitioner.transition(bones, crashWhenCantFindBone, controllerTimer.getAdjustedTick());
        }
    }
}
