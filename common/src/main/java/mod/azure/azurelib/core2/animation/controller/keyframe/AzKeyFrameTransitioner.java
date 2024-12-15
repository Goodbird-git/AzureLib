package mod.azure.azurelib.core2.animation.controller.keyframe;

import java.util.Map;
import java.util.NoSuchElementException;

import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeStack;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core.object.Axis;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzBoneAnimationQueueCache;
import mod.azure.azurelib.core2.animation.controller.AzBoneSnapshotCache;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.model.AzBoneSnapshot;

public class AzKeyFrameTransitioner<T> extends AzAbstractKeyFrameExecutor {

    private final AzAnimationController<T> animationController;

    private final AzBoneAnimationQueueCache<T> boneAnimationQueueCache;

    private final AzBoneSnapshotCache boneSnapshotCache;

    public AzKeyFrameTransitioner(
        AzAnimationController<T> animationController,
        AzBoneAnimationQueueCache<T> boneAnimationQueueCache,
        AzBoneSnapshotCache boneSnapshotCache
    ) {
        this.animationController = animationController;
        this.boneAnimationQueueCache = boneAnimationQueueCache;
        this.boneSnapshotCache = boneSnapshotCache;
    }

    public void transition(Map<String, AzBone> bones, boolean crashWhenCantFindBone, double adjustedTick) {
        var currentAnimation = animationController.getCurrentAnimation();
        var transitionLength = animationController.getTransitionLength();

        MolangParser.INSTANCE.setValue(MolangQueries.ANIM_TIME, () -> 0);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var bone = bones.get(boneAnimation.boneName());

            if (bone == null) {
                if (crashWhenCantFindBone)
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                continue;
            }

            var queue = boneAnimationQueueCache.getOrNull(boneAnimation.boneName());
            var snapshot = boneSnapshotCache.getOrNull(boneAnimation.boneName());

            var rotationKeyFrames = boneAnimation.rotationKeyFrames();
            var positionKeyFrames = boneAnimation.positionKeyFrames();
            var scaleKeyFrames = boneAnimation.scaleKeyFrames();

            transitionRotation(adjustedTick, rotationKeyFrames, queue, transitionLength, snapshot, bone);
            transitionPosition(adjustedTick, positionKeyFrames, queue, transitionLength, snapshot);
            transitionScale(adjustedTick, scaleKeyFrames, queue, transitionLength, snapshot);
        }
    }

    private void transitionRotation(
        double adjustedTick,
        KeyframeStack<Keyframe<IValue>> keyFrames,
        AzBoneAnimationQueue queue,
        double transitionLength,
        AzBoneSnapshot snapshot,
        AzBone bone
    ) {
        if (keyFrames.xKeyframes().isEmpty()) {
            return;
        }

        var initialSnapshot = bone.getInitialAzSnapshot();
        var x = getAnimationPointAtTick(keyFrames.xKeyframes(), 0, true, Axis.X);
        var y = getAnimationPointAtTick(keyFrames.yKeyframes(), 0, true, Axis.Y);
        var z = getAnimationPointAtTick(keyFrames.zKeyframes(), 0, true, Axis.Z);

        queue.addNextRotation(null, adjustedTick, transitionLength, snapshot, initialSnapshot, x, y, z);
    }

    private void transitionPosition(
        double adjustedTick,
        KeyframeStack<Keyframe<IValue>> keyFrames,
        AzBoneAnimationQueue queue,
        double transitionLength,
        AzBoneSnapshot snapshot
    ) {
        if (keyFrames.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyFrames.xKeyframes(), 0, false, Axis.X);
        var y = getAnimationPointAtTick(keyFrames.yKeyframes(), 0, false, Axis.Y);
        var z = getAnimationPointAtTick(keyFrames.zKeyframes(), 0, false, Axis.Z);

        queue.addNextPosition(null, adjustedTick, transitionLength, snapshot, x, y, z);
    }

    private void transitionScale(
        double adjustedTick,
        KeyframeStack<Keyframe<IValue>> keyFrames,
        AzBoneAnimationQueue queue,
        double transitionLength,
        AzBoneSnapshot snapshot
    ) {
        if (keyFrames.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyFrames.xKeyframes(), 0, false, Axis.X);
        var y = getAnimationPointAtTick(keyFrames.yKeyframes(), 0, false, Axis.Y);
        var z = getAnimationPointAtTick(keyFrames.zKeyframes(), 0, false, Axis.Z);

        queue.addNextScale(null, adjustedTick, transitionLength, snapshot, x, y, z);
    }
}
