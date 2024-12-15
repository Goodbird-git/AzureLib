package mod.azure.azurelib.core2.animation.controller.keyframe;

import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzBoneAnimationQueueCache;

public class AzKeyFrameManager<T> {

    private final AzKeyFrameCallbackHandler<T> keyFrameCallbackHandler;

    private final AzKeyFrameProcessor<T> keyFrameProcessor;

    public AzKeyFrameManager(
        AzAnimationController<T> animationController,
        AzBoneAnimationQueueCache boneAnimationQueueCache,
        AzKeyFrameCallbacks<T> keyFrameCallbacks
    ) {
        this.keyFrameCallbackHandler = new AzKeyFrameCallbackHandler<>(animationController, keyFrameCallbacks);
        this.keyFrameProcessor = new AzKeyFrameProcessor<>(animationController, boneAnimationQueueCache);
    }

    public AzKeyFrameCallbackHandler<T> keyFrameCallbackHandler() {
        return keyFrameCallbackHandler;
    }

    public AzKeyFrameProcessor<T> getKeyFrameProcessor() {
        return keyFrameProcessor;
    }
}
