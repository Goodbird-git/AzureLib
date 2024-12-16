package mod.azure.azurelib.core2.animation.controller.keyframe;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeStack;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core.object.Axis;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzBoneAnimationQueueCache;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;

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
     * @param crashWhenCantFindBone Whether the controller should throw an exception when unable to find the required
     *                              bone, or continue with the remaining bones
     */
    public void execute(@NotNull AzQueuedAnimation currentAnimation, T animatable, boolean crashWhenCantFindBone) {
        var keyFrameCallbackHandler = animationController.getKeyFrameManager().keyFrameCallbackHandler();
        var controllerTimer = animationController.getControllerTimer();
        var transitionLength = animationController.getTransitionLength();

        final double finalAdjustedTick = controllerTimer.getAdjustedTick();

        MolangParser.INSTANCE.setMemoizedValue(MolangQueries.ANIM_TIME, () -> finalAdjustedTick / 20d);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var boneAnimationQueue = boneAnimationQueueCache.getOrNull(boneAnimation.boneName());

            if (boneAnimationQueue == null) {
                if (crashWhenCantFindBone) {
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());
                }

                continue;
            }

            var rotationKeyFrames = boneAnimation.rotationKeyFrames();
            var positionKeyFrames = boneAnimation.positionKeyFrames();
            var scaleKeyFrames = boneAnimation.scaleKeyFrames();
            var adjustedTick = controllerTimer.getAdjustedTick();

            updateRotation(rotationKeyFrames, boneAnimationQueue, adjustedTick);
            updatePosition(positionKeyFrames, boneAnimationQueue, adjustedTick);
            updateScale(scaleKeyFrames, boneAnimationQueue, adjustedTick);
        }

        // TODO: Is this correct???
        controllerTimer.addToAdjustedTick(transitionLength);

        keyFrameCallbackHandler.handle(animatable, controllerTimer.getAdjustedTick());
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
