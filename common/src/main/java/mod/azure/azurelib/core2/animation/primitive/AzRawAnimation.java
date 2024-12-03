package mod.azure.azurelib.core2.animation.primitive;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;

import java.util.List;
import java.util.Objects;

/**
 * A builder class for a raw/unbaked animation. These are constructed to pass to the {@link AzAnimationController} to
 * build into full-fledged animations for usage. <br>
 * <br>
 * Animations added to this builder are added <u>in order of insertion</u> - the animations will play in the order that
 * you define them.<br>
 * AzRawAnimation instances should be cached statically where possible to reduce overheads and improve efficiency. <br>
 * <br>
 * Example usage: <br>
 *
 * <pre>{@code
 * AzRawAnimation.begin().thenPlay("action.open_box").thenLoop("state.stay_open")
 * }</pre>
 */
public final class AzRawAnimation {

    private final List<AzStage> animationList;

    // Private constructor to force usage of factory for logical operations
    private AzRawAnimation() {
        this.animationList = new ObjectArrayList<>();
    }

    /**
     * Start a new RawAnimation instance. This is the start point for creating an animation chain.
     *
     * @return A new RawAnimation instance
     */
    public static AzRawAnimation begin() {
        return new AzRawAnimation();
    }

    /**
     * Create a new RawAnimation instance based on an existing RawAnimation instance. The new instance will be a shallow
     * copy of the other instance, and can then be appended to or otherwise modified
     *
     * @param other The existing RawAnimation instance to copy
     * @return A new instance of RawAnimation
     */
    public static AzRawAnimation copyOf(AzRawAnimation other) {
        AzRawAnimation newInstance = AzRawAnimation.begin();

        newInstance.animationList.addAll(other.animationList);

        return newInstance;
    }

    /**
     * Append an animation to the animation chain, playing the named animation and stopping or progressing to the next
     * chained animation depending on the loop type set in the animation json
     *
     * @param animationName The name of the animation to play once
     */
    public AzRawAnimation thenPlay(String animationName) {
        return then(animationName, AzLoopType.DEFAULT);
    }

    /**
     * Append an animation to the animation chain, playing the named animation and repeating it continuously until the
     * animation is stopped by external sources.
     *
     * @param animationName The name of the animation to play on a loop
     */
    public AzRawAnimation thenLoop(String animationName) {
        return then(animationName, AzLoopType.LOOP);
    }

    /**
     * Appends a 'wait' animation to the animation chain.<br>
     * This causes the animatable to do nothing for a set period of time before performing the next animation.
     *
     * @param ticks The number of ticks to 'wait' for
     */
    public AzRawAnimation thenWait(int ticks) {
        this.animationList.add(new AzStage(AzStage.WAIT, AzLoopType.PLAY_ONCE, ticks));

        return this;
    }

    /**
     * Appends an animation to the animation chain, then has the animatable hold the pose at the end of the animation
     * until it is stopped by external sources.
     *
     * @param animation The name of the animation to play and hold
     */
    public AzRawAnimation thenPlayAndHold(String animation) {
        return then(animation, AzLoopType.HOLD_ON_LAST_FRAME);
    }

    /**
     * Append an animation to the animation chain, playing the named animation <code>playCount</code> times, then
     * stopping or progressing to the next chained animation depending on the loop type set in the animation json
     *
     * @param animationName The name of the animation to play X times
     * @param playCount     The number of times to repeat the animation before proceeding
     */
    public AzRawAnimation thenPlayXTimes(String animationName, int playCount) {
        for (int i = 0; i < playCount; i++) {
            then(animationName, i == playCount - 1 ? AzLoopType.DEFAULT : AzLoopType.PLAY_ONCE);
        }

        return this;
    }

    /**
     * Append an animation to the animation chain, playing the named animation and proceeding based on the
     * <code>loopType</code> parameter provided.
     *
     * @param animationName The name of the animation to play. <u>MUST</u> match the name of the animation in the
     *                      <code>.animation.json</code> file.
     * @param loopType      The loop type handler for the animation, overriding the default value set in the animation
     *                      json
     */
    public AzRawAnimation then(String animationName, AzLoopType loopType) {
        this.animationList.add(new AzStage(animationName, loopType));

        return this;
    }

    public List<AzStage> getAnimationStages() {
        return this.animationList;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.animationList);
    }


}
