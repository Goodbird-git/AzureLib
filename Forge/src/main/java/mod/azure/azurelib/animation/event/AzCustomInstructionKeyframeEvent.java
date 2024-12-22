package mod.azure.azurelib.animation.event;

import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyFrameCallbacks;
import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;

/**
 * The {@link AzKeyFrameEvent} specific to the {@link AzKeyFrameCallbacks#getCustomKeyframeHandler()}.<br>
 * Called when a custom instruction keyframe is encountered
 */
public class AzCustomInstructionKeyframeEvent<T> extends AzKeyFrameEvent<T, CustomInstructionKeyframeData> {

    public AzCustomInstructionKeyframeEvent(
        T entity,
        double animationTick,
        AzAnimationController<T> controller,
        CustomInstructionKeyframeData customInstructionKeyframeData
    ) {
        super(entity, animationTick, controller, customInstructionKeyframeData);
    }

    /**
     * Get the {@link CustomInstructionKeyframeData} relevant to this event call
     */
    @Override
    public CustomInstructionKeyframeData getKeyframeData() {
        return super.getKeyframeData();
    }
}
