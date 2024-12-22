package mod.azure.azurelib.animation.controller.state.impl;

import mod.azure.azurelib.animation.AzAnimationContext;
import mod.azure.azurelib.animation.cache.AzBoneCache;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerTimer;
import mod.azure.azurelib.animation.controller.AzBoneSnapshotCache;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyFrameTransitioner;
import mod.azure.azurelib.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.animation.controller.state.machine.Context;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.model.AzBoneSnapshot;

import java.util.Map;

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
        AzBoneSnapshotCache boneSnapshotCache = controller.getBoneSnapshotCache();
        AzAnimationContext<T> animContext = context.getAnimationContext();

        AzAnimationControllerStateMachine<T> stateMachine = context.getStateMachine();
        AzBoneCache boneCache = animContext.boneCache();

        if (controllerTimer.getAdjustedTick() == 0) {
            controller.setCurrentAnimation(controller.getAnimationQueue().next());

            controller.getKeyFrameManager().keyFrameCallbackHandler().reset();

            if (controller.getCurrentAnimation() == null) {
                return;
            }

            Map<String, AzBoneSnapshot> snapshots = boneCache.getBoneSnapshotsByName();

            boneSnapshotCache.put(controller.getCurrentAnimation(), snapshots.values());
        }

        boolean hasFinishedTransitioning = controllerTimer.getAdjustedTick() >= controller.getTransitionLength();

        if (hasFinishedTransitioning) {
            // If we've exceeded the amount of time we should be transitioning, then switch to play state.
            stateMachine.play();
            return;
        }

        if (controller.getCurrentAnimation() != null) {
            Map<String, AzBone> bones = boneCache.getBakedModel().getBonesByName();
            boolean crashWhenCantFindBone = animContext.config().crashIfBoneMissing();
            AzKeyFrameTransitioner<T> keyFrameTransitioner = controller.getKeyFrameManager().getKeyFrameTransitioner();

            keyFrameTransitioner.transition(bones, crashWhenCantFindBone, controllerTimer.getAdjustedTick());
        }
    }
}
