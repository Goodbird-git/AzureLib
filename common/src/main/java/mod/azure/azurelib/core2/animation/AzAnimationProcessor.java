package mod.azure.azurelib.core2.animation;

import mod.azure.azurelib.core2.animation.cache.AzBoneCache;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;

public class AzAnimationProcessor<T> {

    private final AzAnimator<T> animator;

    private final AzBoneCache boneCache;

    public boolean reloadAnimations;

    public AzAnimationProcessor(AzAnimator<T> animator) {
        this.animator = animator;
        this.boneCache = new AzBoneCache();
        this.reloadAnimations = false;
    }

    /**
     * Tick and apply transformations to the model based on the current state of the {@link AzAnimationController}
     *
     * @param animatable The animatable object relevant to the animation being played
     */
    public void update(T animatable) {
        var animTime = animator.getAnimTime();
        var shouldCrash = animator.config().crashIfBoneMissing();

        boneCache.updateBoneSnapshots();
        var boneSnapshots = boneCache.getBoneSnapshotsByName();
        var bonesByName = boneCache.getBonesByName();

        for (var controller : animator.getAnimationControllerContainer().getAll()) {
            var easingType = controller.getOverrideEasingTypeFunction().apply(animatable);

            if (this.reloadAnimations) {
                controller.forceAnimationReset();
                controller.getBoneAnimationQueues().clear();
            }

            controller.setJustStarting(animator.isFirstTick());

            controller.process(animatable, bonesByName, boneSnapshots, animTime, shouldCrash);

            // Progresses the current bones according to the animation queue.
            for (var boneAnimation : controller.getBoneAnimationQueues().values()) {
                var bone = boneAnimation.bone();
                var snapshot = boneSnapshots.get(bone.getName());
                var initialSnapshot = bone.getInitialSnapshot();

                AzBoneAnimationUpdateUtil.updateRotations(boneAnimation, bone, easingType, initialSnapshot, snapshot);
                AzBoneAnimationUpdateUtil.updatePositions(boneAnimation, bone, easingType, snapshot);
                AzBoneAnimationUpdateUtil.updateScale(boneAnimation, bone, easingType, snapshot);
            }
        }

        this.reloadAnimations = false;
        double resetTickLength = animator.config().boneResetTime();

        // Updates the cached bone snapshots (only if they have changed).
        for (var bone : boneCache.getRegisteredBones()) {
            AzCachedBoneUpdateUtil.updateCachedBoneRotation(bone, boneSnapshots, animTime, resetTickLength);
            AzCachedBoneUpdateUtil.updateCachedBonePosition(bone, boneSnapshots, animTime, resetTickLength);
            AzCachedBoneUpdateUtil.updateCachedBoneScale(bone, boneSnapshots, animTime, resetTickLength);
        }

        boneCache.resetBoneTransformationMarkers();
        animator.finishFirstTick();
    }

    public AzBoneCache getBoneCache() {
        return boneCache;
    }
}
