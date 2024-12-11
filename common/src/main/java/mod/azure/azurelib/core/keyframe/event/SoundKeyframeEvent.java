/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core.keyframe.event;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;

/**
 * The {@link KeyFrameEvent} specific to the {@link AnimationController#soundKeyframeHandler}.<br>
 * Called when a sound instruction keyframe is encountered
 *
 * @deprecated
 */
public class SoundKeyframeEvent<T extends GeoAnimatable> extends KeyFrameEvent<T, SoundKeyframeData> {

    /**
     * This stores all the fields that are needed in the AnimationTestEvent
     *
     * @param entity        the entity
     * @param animationTick The amount of ticks that have passed in either the current transition or animation,
     *                      depending on the controller's AnimationState.
     * @param controller    the controller
     */
    public SoundKeyframeEvent(
        T entity,
        double animationTick,
        AnimationController<T> controller,
        SoundKeyframeData keyFrameData
    ) {
        super(entity, animationTick, controller, keyFrameData);
    }
}
