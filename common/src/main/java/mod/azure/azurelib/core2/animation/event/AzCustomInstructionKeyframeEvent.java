package mod.azure.azurelib.core2.animation.event;

import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyframeCallbacks;

/**
 * The {@link AzKeyframeEvent} specific to the {@link AzKeyframeCallbacks#customKeyframeHandler()}.<br>
 * Called when a custom instruction keyframe is encountered
 */
public class AzCustomInstructionKeyframeEvent<T> extends AzKeyframeEvent<T, CustomInstructionKeyframeData> {

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
