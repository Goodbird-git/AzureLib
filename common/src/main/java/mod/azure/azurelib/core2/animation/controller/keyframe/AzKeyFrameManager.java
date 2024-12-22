package mod.azure.azurelib.core2.animation.controller.keyframe;

import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzBoneAnimationQueueCache;
import mod.azure.azurelib.core2.animation.controller.AzBoneSnapshotCache;

/**
 * AzKeyFrameManager is responsible for managing the keyframe-related operations in an animation system. It coordinates
 * the execution, transition, and callback handling of animation keyframes through its associated components.
 *
 * @param <T> the type of the animatable object being handled
 */
public class AzKeyFrameManager<T> {

    private final AzKeyFrameCallbackHandler<T> keyFrameCallbackHandler;

    private final AzKeyFrameExecutor<T> keyFrameExecutor;

    private final AzKeyFrameTransitioner<T> keyFrameTransitioner;

    public AzKeyFrameManager(
        AzAnimationController<T> animationController,
        AzBoneAnimationQueueCache<T> boneAnimationQueueCache,
        AzBoneSnapshotCache boneSnapshotCache,
        AzKeyFrameCallbacks<T> keyFrameCallbacks
    ) {
        this.keyFrameCallbackHandler = new AzKeyFrameCallbackHandler<>(animationController, keyFrameCallbacks);
        this.keyFrameExecutor = new AzKeyFrameExecutor<>(animationController, boneAnimationQueueCache);
        this.keyFrameTransitioner = new AzKeyFrameTransitioner<>(
            animationController,
            boneAnimationQueueCache,
            boneSnapshotCache
        );
    }

    public AzKeyFrameCallbackHandler<T> keyFrameCallbackHandler() {
        return keyFrameCallbackHandler;
    }

    public AzKeyFrameExecutor<T> getKeyFrameExecutor() {
        return keyFrameExecutor;
    }

    public AzKeyFrameTransitioner<T> getKeyFrameTransitioner() {
        return keyFrameTransitioner;
    }
}
