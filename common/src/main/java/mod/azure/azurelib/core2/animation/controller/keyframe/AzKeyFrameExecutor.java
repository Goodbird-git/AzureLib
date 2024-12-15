package mod.azure.azurelib.core2.animation.controller.keyframe;

import java.util.NoSuchElementException;

import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeStack;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core.object.Axis;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzBoneAnimationQueueCache;

public class AzKeyFrameExecutor<T> extends AzAbstractKeyFrameExecutor {

    private final AzAnimationController<T> animationController;

    private final AzBoneAnimationQueueCache<T> boneAnimationQueueCache;

    public AzKeyFrameExecutor(
        AzAnimationController<T> animationController,
        AzBoneAnimationQueueCache<T> boneAnimationQueueCache
    ) {
        this.animationController = animationController;
        this.boneAnimationQueueCache = boneAnimationQueueCache;
    }

    /**
     * Handle the current animation's state modifications and translations
     *
     * @param seekTime              The lerped tick (current tick + partial tick)
     * @param crashWhenCantFindBone Whether the controller should throw an exception when unable to find the required
     *                              bone, or continue with the remaining bones
     */
    public void execute(T animatable, double seekTime, boolean crashWhenCantFindBone) {
        var animationQueue = animationController.getAnimationQueue();
        var currentAnimation = animationController.getCurrentAnimation();
        var keyFrameCallbackHandler = animationController.getKeyFrameManager().keyFrameCallbackHandler();
        var stateMachine = animationController.getStateMachine();
        var stateMachineContext = stateMachine.getContext();
        var transitionLength = animationController.getTransitionLength();

        var hasAnimationFinished = stateMachineContext.adjustedTick >= currentAnimation.animation().length();

        // TODO: This logic REALLY doesn't belong here... it belongs in the play state.
        if (hasAnimationFinished) {
            var shouldPlayAgain = currentAnimation.loopType()
                .shouldPlayAgain(animatable, animationController, currentAnimation.animation());

            if (shouldPlayAgain) {
                var isNotPaused = !stateMachine.isPaused();

                if (isNotPaused) {
                    animationController.setShouldResetTick(true);

                    stateMachineContext.adjustedTick = animationController.adjustTick(animatable, seekTime);
                    keyFrameCallbackHandler.reset();
                }
            } else {
                var nextAnimation = animationQueue.peek();

                keyFrameCallbackHandler.reset();

                if (nextAnimation == null) {
                    stateMachine.stop();

                    return;
                } else {
                    stateMachine.transition();
                    animationController.setShouldResetTick(true);
                    animationController.setCurrentAnimation(nextAnimation);
                }
            }
        }

        final double finalAdjustedTick = stateMachineContext.adjustedTick;

        MolangParser.INSTANCE.setMemoizedValue(MolangQueries.ANIM_TIME, () -> finalAdjustedTick / 20d);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var boneAnimationQueue = boneAnimationQueueCache.getOrNull(boneAnimation.boneName());

            if (boneAnimationQueue == null) {
                if (crashWhenCantFindBone)
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                continue;
            }

            var rotationKeyFrames = boneAnimation.rotationKeyFrames();
            var positionKeyFrames = boneAnimation.positionKeyFrames();
            var scaleKeyFrames = boneAnimation.scaleKeyFrames();
            var adjustedTick = stateMachineContext.adjustedTick;

            updateRotation(rotationKeyFrames, boneAnimationQueue, adjustedTick);
            updatePosition(positionKeyFrames, boneAnimationQueue, adjustedTick);
            updateScale(scaleKeyFrames, boneAnimationQueue, adjustedTick);
        }

        stateMachineContext.adjustedTick += transitionLength;

        keyFrameCallbackHandler.handle(animatable, stateMachineContext.adjustedTick);
    }

    private void updateRotation(
        KeyframeStack<Keyframe<IValue>> keyFrames,
        AzBoneAnimationQueue queue,
        double adjustedTick
    ) {
        if (keyFrames.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyFrames.xKeyframes(), adjustedTick, true, Axis.X);
        var y = getAnimationPointAtTick(keyFrames.yKeyframes(), adjustedTick, true, Axis.Y);
        var z = getAnimationPointAtTick(keyFrames.zKeyframes(), adjustedTick, true, Axis.Z);

        queue.addRotations(x, y, z);
    }

    private void updatePosition(
        KeyframeStack<Keyframe<IValue>> keyFrames,
        AzBoneAnimationQueue queue,
        double adjustedTick
    ) {
        if (keyFrames.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyFrames.xKeyframes(), adjustedTick, false, Axis.X);
        var y = getAnimationPointAtTick(keyFrames.yKeyframes(), adjustedTick, false, Axis.Y);
        var z = getAnimationPointAtTick(keyFrames.zKeyframes(), adjustedTick, false, Axis.Z);

        queue.addPositions(x, y, z);
    }

    private void updateScale(
        KeyframeStack<Keyframe<IValue>> keyFrames,
        AzBoneAnimationQueue queue,
        double adjustedTick
    ) {
        if (keyFrames.xKeyframes().isEmpty()) {
            return;
        }

        var x = getAnimationPointAtTick(keyFrames.xKeyframes(), adjustedTick, false, Axis.X);
        var y = getAnimationPointAtTick(keyFrames.yKeyframes(), adjustedTick, false, Axis.Y);
        var z = getAnimationPointAtTick(keyFrames.zKeyframes(), adjustedTick, false, Axis.Z);

        queue.addScales(x, y, z);
    }
}
