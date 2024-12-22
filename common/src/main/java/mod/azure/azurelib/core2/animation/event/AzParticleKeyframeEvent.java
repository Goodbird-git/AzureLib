/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core2.animation.event;

import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbacks;

/**
 * The {@link AzKeyFrameEvent} specific to the {@link AzKeyFrameCallbacks#particleKeyframeHandler()}.<br>
 * Called when a particle instruction keyframe is encountered
 */
public class AzParticleKeyframeEvent<T> extends AzKeyFrameEvent<T, ParticleKeyframeData> {

    public AzParticleKeyframeEvent(
        T animatable,
        double animationTick,
        AzAnimationController<T> controller,
        ParticleKeyframeData particleKeyFrameData
    ) {
        super(animatable, animationTick, controller, particleKeyFrameData);
    }

    /**
     * Get the {@link ParticleKeyframeData} relevant to this event call
     */
    @Override
    public ParticleKeyframeData getKeyframeData() {
        return super.getKeyframeData();
    }
}
