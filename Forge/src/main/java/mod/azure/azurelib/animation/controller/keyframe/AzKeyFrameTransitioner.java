package mod.azure.azurelib.animation.controller.keyframe;

import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzBoneAnimationQueueCache;
import mod.azure.azurelib.animation.controller.AzBoneSnapshotCache;
import mod.azure.azurelib.animation.primitive.AzQueuedAnimation;
import mod.azure.azurelib.core.keyframe.AnimationPoint;
import mod.azure.azurelib.core.keyframe.BoneAnimation;
import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeStack;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core.object.Axis;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.model.AzBoneSnapshot;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * AzKeyFrameTransitioner is a specialized class for executing smooth animations
 * and transitions between keyframes for bones in an animation system. It utilizes
 * animation controllers, bone animation queue caches, and bone snapshot caches
 * to manage and apply transitions for rotation, position, and scale of bones.
 *
 * @param <T> The type of the animation data handled by the associated animation controller.
 */
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
        AzQueuedAnimation currentAnimation = animationController.getCurrentAnimation();
        double transitionLength = animationController.getTransitionLength();

        MolangParser.INSTANCE.setValue(MolangQueries.ANIM_TIME, () -> 0);

        for (BoneAnimation boneAnimation : currentAnimation.animation().boneAnimations()) {
            AzBone bone = bones.get(boneAnimation.boneName());

            if (bone == null) {
                if (crashWhenCantFindBone)
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                continue;
            }

            AzBoneAnimationQueue queue = boneAnimationQueueCache.getOrNull(boneAnimation.boneName());
            AzBoneSnapshot snapshot = boneSnapshotCache.getOrNull(boneAnimation.boneName());

            KeyframeStack<Keyframe<IValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames();
            KeyframeStack<Keyframe<IValue>> positionKeyFrames = boneAnimation.positionKeyFrames();
            KeyframeStack<Keyframe<IValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames();

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

        AzBoneSnapshot initialSnapshot = bone.getInitialAzSnapshot();
        AnimationPoint x = getAnimationPointAtTick(keyFrames.xKeyframes(), 0, true, Axis.X);
        AnimationPoint y = getAnimationPointAtTick(keyFrames.yKeyframes(), 0, true, Axis.Y);
        AnimationPoint z = getAnimationPointAtTick(keyFrames.zKeyframes(), 0, true, Axis.Z);

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

        AnimationPoint x = getAnimationPointAtTick(keyFrames.xKeyframes(), 0, false, Axis.X);
        AnimationPoint y = getAnimationPointAtTick(keyFrames.yKeyframes(), 0, false, Axis.Y);
        AnimationPoint z = getAnimationPointAtTick(keyFrames.zKeyframes(), 0, false, Axis.Z);

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

        AnimationPoint x = getAnimationPointAtTick(keyFrames.xKeyframes(), 0, false, Axis.X);
        AnimationPoint y = getAnimationPointAtTick(keyFrames.yKeyframes(), 0, false, Axis.Y);
        AnimationPoint z = getAnimationPointAtTick(keyFrames.zKeyframes(), 0, false, Axis.Z);

        queue.addNextScale(null, adjustedTick, transitionLength, snapshot, x, y, z);
    }
}
