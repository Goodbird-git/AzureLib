package mod.azure.azurelib.animation.controller.keyframe;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerTimer;
import mod.azure.azurelib.animation.controller.AzBoneAnimationQueueCache;
import mod.azure.azurelib.animation.primitive.AzQueuedAnimation;
import mod.azure.azurelib.core.keyframe.AnimationPoint;
import mod.azure.azurelib.core.keyframe.BoneAnimation;
import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeStack;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core.object.Axis;

import java.util.NoSuchElementException;

/**
 * AzKeyFrameExecutor is a specialized implementation of {@link AzAbstractKeyFrameExecutor}, designed to handle
 * keyframe-based animations for animatable objects. It delegates animation control to an
 * {@link AzAnimationController} and manages bone animation queues through an {@link AzBoneAnimationQueueCache}.
 * <br>
 * This class processes and applies transformations such as rotation, position, and scale to bone animations,
 * based on the current tick time and the keyframes associated with each bone animation.
 *
 * @param <T> The type of the animatable object to which the keyframe animations will be applied
 */
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
        AzKeyFrameCallbackHandler<T> keyFrameCallbackHandler = animationController.getKeyFrameManager().keyFrameCallbackHandler();
        AzAnimationControllerTimer<T> controllerTimer = animationController.getControllerTimer();
        double transitionLength = animationController.getTransitionLength();

        final double finalAdjustedTick = controllerTimer.getAdjustedTick();

        MolangParser.INSTANCE.setMemoizedValue(MolangQueries.ANIM_TIME, () -> finalAdjustedTick / 20d);

        for (BoneAnimation boneAnimation : currentAnimation.animation().boneAnimations()) {
            AzBoneAnimationQueue boneAnimationQueue = boneAnimationQueueCache.getOrNull(boneAnimation.boneName());

            if (boneAnimationQueue == null) {
                if (crashWhenCantFindBone) {
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());
                }

                continue;
            }

            KeyframeStack<Keyframe<IValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames();
            KeyframeStack<Keyframe<IValue>> positionKeyFrames = boneAnimation.positionKeyFrames();
            KeyframeStack<Keyframe<IValue>> scaleKeyFrames = boneAnimation.scaleKeyFrames();
            double adjustedTick = controllerTimer.getAdjustedTick();

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

        AnimationPoint x = getAnimationPointAtTick(keyFrames.xKeyframes(), adjustedTick, true, Axis.X);
        AnimationPoint y = getAnimationPointAtTick(keyFrames.yKeyframes(), adjustedTick, true, Axis.Y);
        AnimationPoint z = getAnimationPointAtTick(keyFrames.zKeyframes(), adjustedTick, true, Axis.Z);

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

        AnimationPoint x = getAnimationPointAtTick(keyFrames.xKeyframes(), adjustedTick, false, Axis.X);
        AnimationPoint y = getAnimationPointAtTick(keyFrames.yKeyframes(), adjustedTick, false, Axis.Y);
        AnimationPoint z = getAnimationPointAtTick(keyFrames.zKeyframes(), adjustedTick, false, Axis.Z);

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

        AnimationPoint x = getAnimationPointAtTick(keyFrames.xKeyframes(), adjustedTick, false, Axis.X);
        AnimationPoint y = getAnimationPointAtTick(keyFrames.yKeyframes(), adjustedTick, false, Axis.Y);
        AnimationPoint z = getAnimationPointAtTick(keyFrames.zKeyframes(), adjustedTick, false, Axis.Z);

        queue.addScales(x, y, z);
    }
}
