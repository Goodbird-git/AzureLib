/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core.keyframe.event;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;

/**
 * The {@link KeyFrameEvent} specific to the {@link AnimationController#particleKeyframeHandler}.<br>
 * Called when a particle instruction keyframe is encountered
 *
 * @deprecated
 */
public class ParticleKeyframeEvent<T extends GeoAnimatable> extends KeyFrameEvent<T, ParticleKeyframeData> {

    public ParticleKeyframeEvent(
        T animatable,
        double animationTick,
        AnimationController<T> controller,
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
