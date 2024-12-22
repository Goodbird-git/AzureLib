package mod.azure.azurelib.core2.animation.controller;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

// TODO: This will eventually be usable in common-side code once animations are moved from assets to data.
public class AzAbstractAnimationController {

    private final String name;

    private final Map<String, AzRawAnimation> triggerableAnimations;

    protected AzRawAnimation currentRawAnimation;

    protected AzRawAnimation triggeredAnimation;

    protected AzAbstractAnimationController(
        String name,
        Map<String, AzRawAnimation> triggerableAnimations
    ) {
        this.name = name;
        this.triggerableAnimations = triggerableAnimations;
        this.triggeredAnimation = null;
    }

    public String name() {
        return name;
    }

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public @Nullable AzRawAnimation getTriggerableAnimationOrNull(String animationName) {
        return triggerableAnimations.get(animationName);
    }

    /**
     * Attempt to trigger an animation from the list of {@link AzAbstractAnimationController#triggerableAnimations
     * triggerable animations} this controller contains.
     *
     * @param animName The name of the animation to trigger
     * @return Whether the controller triggered an animation or not
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public boolean tryTriggerAnimation(String animName) {
        var anim = getTriggerableAnimationOrNull(animName);

        if (anim == null) {
            return false;
        }

        this.triggeredAnimation = anim;

        return true;
    }

    /**
     * Checks whether the last animation that was playing on this controller has finished or not.<br>
     * This will return true if the controller has had an animation set previously, and it has finished playing and
     * isn't going to loop or proceed to another animation.<br>
     *
     * @return Whether the previous animation finished or not
     */
    public boolean hasAnimationFinished() {
        return currentRawAnimation != null;
    }
}
