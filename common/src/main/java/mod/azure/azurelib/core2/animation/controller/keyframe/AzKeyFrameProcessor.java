package mod.azure.azurelib.core2.animation.controller.keyframe;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import mod.azure.azurelib.core.keyframe.AnimationPoint;
import mod.azure.azurelib.core.keyframe.BoneAnimationQueue;
import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeLocation;
import mod.azure.azurelib.core.math.Constant;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core.object.Axis;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerState;
import mod.azure.azurelib.core2.model.AzBone;

public class AzKeyFrameProcessor<T> {

    private final AzAnimationController<T> animationController;

    public AzKeyFrameProcessor(AzAnimationController<T> animationController) {
        this.animationController = animationController;
    }

    /**
     * Handle the current animation's state modifications and translations
     *
     * @param boneAnimationQueues
     * @param adjustedTick          The controller-adjusted tick for animation purposes
     * @param seekTime              The lerped tick (current tick + partial tick)
     * @param crashWhenCantFindBone Whether the controller should throw an exception when unable to find the required
     *                              bone, or continue with the remaining bones
     */
    public void runCurrentAnimation(
        Map<String, BoneAnimationQueue> boneAnimationQueues,
        T animatable,
        double adjustedTick,
        double seekTime,
        boolean crashWhenCantFindBone
    ) {
        var animationQueue = animationController.getAnimationQueue();
        var animationState = animationController.getAnimationState();
        var currentAnimation = animationController.getCurrentAnimation();
        var keyFrameCallbackManager = animationController.getKeyFrameCallbackManager();
        var transitionLength = animationController.getTransitionLength();

        if (adjustedTick >= currentAnimation.animation().length()) {
            if (
                currentAnimation.loopType()
                    .shouldPlayAgain(animatable, animationController, currentAnimation.animation())
            ) {
                if (animationState != AzAnimationControllerState.PAUSED) {
                    animationController.setShouldResetTick(true);

                    adjustedTick = animationController.adjustTick(animatable, seekTime);
                    keyFrameCallbackManager.reset();
                }
            } else {
                var nextAnimation = animationQueue.peek();

                keyFrameCallbackManager.reset();

                if (nextAnimation == null) {
                    animationController.setAnimationState(AzAnimationControllerState.STOPPED);

                    return;
                } else {
                    animationController.setAnimationState(AzAnimationControllerState.TRANSITIONING);
                    animationController.setShouldResetTick(true);
                    animationController.setCurrentAnimation(nextAnimation);
                }
            }
        }

        final double finalAdjustedTick = adjustedTick;

        MolangParser.INSTANCE.setMemoizedValue(MolangQueries.ANIM_TIME, () -> finalAdjustedTick / 20d);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName());

            if (boneAnimationQueue == null) {
                if (crashWhenCantFindBone)
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                continue;
            }

            var rotationKeyFrames = boneAnimation.rotationKeyFrames();
            var positionKeyFrames = boneAnimation.positionKeyFrames();
            var scaleKeyFrames = boneAnimation.scaleKeyFrames();

            if (!rotationKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addRotations(
                    getAnimationPointAtTick(rotationKeyFrames.xKeyframes(), adjustedTick, true, Axis.X),
                    getAnimationPointAtTick(rotationKeyFrames.yKeyframes(), adjustedTick, true, Axis.Y),
                    getAnimationPointAtTick(rotationKeyFrames.zKeyframes(), adjustedTick, true, Axis.Z)
                );
            }

            if (!positionKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addPositions(
                    getAnimationPointAtTick(positionKeyFrames.xKeyframes(), adjustedTick, false, Axis.X),
                    getAnimationPointAtTick(positionKeyFrames.yKeyframes(), adjustedTick, false, Axis.Y),
                    getAnimationPointAtTick(positionKeyFrames.zKeyframes(), adjustedTick, false, Axis.Z)
                );
            }

            if (!scaleKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addScales(
                    getAnimationPointAtTick(scaleKeyFrames.xKeyframes(), adjustedTick, false, Axis.X),
                    getAnimationPointAtTick(scaleKeyFrames.yKeyframes(), adjustedTick, false, Axis.Y),
                    getAnimationPointAtTick(scaleKeyFrames.zKeyframes(), adjustedTick, false, Axis.Z)
                );
            }
        }

        adjustedTick += transitionLength;

        keyFrameCallbackManager.handle(animatable, adjustedTick);
    }

    public void transitionFromCurrentAnimation(
        Map<String, BoneAnimationQueue> boneAnimationQueues,
        Map<String, AzBone> bones,
        boolean crashWhenCantFindBone,
        double adjustedTick
    ) {
        var boneSnapshotCache = animationController.getBoneSnapshotCache();
        var currentAnimation = animationController.getCurrentAnimation();
        var transitionLength = animationController.getTransitionLength();

        MolangParser.INSTANCE.setValue(MolangQueries.ANIM_TIME, () -> 0);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName());
            var boneSnapshot = boneSnapshotCache.getOrNull(boneAnimation.boneName());
            var bone = bones.get(boneAnimation.boneName());

            if (bone == null) {
                if (crashWhenCantFindBone)
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                continue;
            }

            var rotationKeyFrames = boneAnimation.rotationKeyFrames();
            var positionKeyFrames = boneAnimation.positionKeyFrames();
            var scaleKeyFrames = boneAnimation.scaleKeyFrames();

            if (!rotationKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addNextRotation(
                    null,
                    adjustedTick,
                    transitionLength,
                    boneSnapshot,
                    bone.getInitialSnapshot(),
                    getAnimationPointAtTick(rotationKeyFrames.xKeyframes(), 0, true, Axis.X),
                    getAnimationPointAtTick(rotationKeyFrames.yKeyframes(), 0, true, Axis.Y),
                    getAnimationPointAtTick(rotationKeyFrames.zKeyframes(), 0, true, Axis.Z)
                );
            }

            if (!positionKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addNextPosition(
                    null,
                    adjustedTick,
                    transitionLength,
                    boneSnapshot,
                    getAnimationPointAtTick(positionKeyFrames.xKeyframes(), 0, false, Axis.X),
                    getAnimationPointAtTick(positionKeyFrames.yKeyframes(), 0, false, Axis.Y),
                    getAnimationPointAtTick(positionKeyFrames.zKeyframes(), 0, false, Axis.Z)
                );
            }

            if (!scaleKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addNextScale(
                    null,
                    adjustedTick,
                    transitionLength,
                    boneSnapshot,
                    getAnimationPointAtTick(scaleKeyFrames.xKeyframes(), 0, false, Axis.X),
                    getAnimationPointAtTick(scaleKeyFrames.yKeyframes(), 0, false, Axis.Y),
                    getAnimationPointAtTick(scaleKeyFrames.zKeyframes(), 0, false, Axis.Z)
                );
            }
        }
    }

    /**
     * Convert a {@link KeyframeLocation} to an {@link AnimationPoint}
     */
    private AnimationPoint getAnimationPointAtTick(
        List<Keyframe<IValue>> frames,
        double tick,
        boolean isRotation,
        Axis axis
    ) {
        var location = getCurrentKeyFrameLocation(frames, tick);
        var currentFrame = location.keyframe();
        var startValue = currentFrame.startValue().get();
        var endValue = currentFrame.endValue().get();

        if (isRotation) {
            if (!(currentFrame.startValue() instanceof Constant)) {
                startValue = Math.toRadians(startValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    startValue *= -1;
                }
            }

            if (!(currentFrame.endValue() instanceof Constant)) {
                endValue = Math.toRadians(endValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    endValue *= -1;
                }
            }
        }

        return new AnimationPoint(currentFrame, location.startTick(), currentFrame.length(), startValue, endValue);
    }

    /**
     * Returns the {@link Keyframe} relevant to the current tick time
     *
     * @param frames     The list of {@code KeyFrames} to filter through
     * @param ageInTicks The current tick time
     * @return A new {@code KeyFrameLocation} containing the current {@code KeyFrame} and the tick time used to find it
     */
    protected KeyframeLocation<Keyframe<IValue>> getCurrentKeyFrameLocation(
        List<Keyframe<IValue>> frames,
        double ageInTicks
    ) {
        var totalFrameTime = 0.0;

        for (var frame : frames) {
            totalFrameTime += frame.length();

            if (totalFrameTime > ageInTicks) {
                return new KeyframeLocation<>(frame, (ageInTicks - (totalFrameTime - frame.length())));
            }
        }

        return new KeyframeLocation<>(frames.get(frames.size() - 1), ageInTicks);
    }
}
