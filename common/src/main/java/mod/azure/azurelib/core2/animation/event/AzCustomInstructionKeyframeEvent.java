package mod.azure.azurelib.core2.animation.event;

import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;

/**
 * The {@link AzKeyFrameEvent} specific to the {@link AzAnimationController#customKeyframeHandler}.<br>
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
