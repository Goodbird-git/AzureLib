package mod.azure.azurelib.core2.animation;

import java.util.Map;

import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.model.AzBoneSnapshot;

public class AzAnimationProcessor<T> {

    private final AzAnimator<T> animator;

    public boolean reloadAnimations;

    public AzAnimationProcessor(AzAnimator<T> animator) {
        this.animator = animator;
        this.reloadAnimations = false;
    }

    /**
     * Tick and apply transformations to the model based on the current state of the {@link AzAnimationController}
     *
     * @param context An animation context provided by the animator.
     */
    public void update(AzAnimationContext<T> context) {
        var animatable = context.animatable();
        var timer = context.timer();
        var boneCache = context.boneCache();

        boneCache.snapshot();
        var boneSnapshots = boneCache.getBoneSnapshotsByName();

        for (var controller : animator.getAnimationControllerContainer().getAll()) {
            var easingType = controller.getOverrideEasingTypeFunction().apply(animatable);

            if (this.reloadAnimations) {
                controller.forceAnimationReset();
                controller.getBoneAnimationQueues().clear();
            }

            controller.update(context);

            updateBoneSnapshots(controller, boneSnapshots, easingType);
        }

        this.reloadAnimations = false;

        boneCache.update(context);
        timer.finishFirstTick();
    }

    private void updateBoneSnapshots(
        AzAnimationController<T> controller,
        Map<String, AzBoneSnapshot> boneSnapshots,
        EasingType easingType
    ) {
        // Progresses the current bones according to the animation queue.
        for (var boneAnimation : controller.getBoneAnimationQueues().values()) {
            var bone = boneAnimation.bone();
            var snapshot = boneSnapshots.get(bone.getName());
            var initialSnapshot = bone.getInitialAzSnapshot();

            AzBoneAnimationUpdateUtil.updateRotations(boneAnimation, bone, easingType, initialSnapshot, snapshot);
            AzBoneAnimationUpdateUtil.updatePositions(boneAnimation, bone, easingType, snapshot);
            AzBoneAnimationUpdateUtil.updateScale(boneAnimation, bone, easingType, snapshot);
        }
    }
}
