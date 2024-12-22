package mod.azure.azurelib.animation.primitive;

/**
 * Represents an entry in an animation queue, combining an animation and its looping behavior. This record defines a
 * queued animation to be played, including its associated {@link AzBakedAnimation} instance and the {@link AzLoopType} that
 * determines how the animation behaves once it reaches the end of its sequence. <br/>
 * <br/>
 * Instances of AzQueuedAnimation are immutable by design, ensuring that queued animations, once defined, cannot be
 * modified, preserving their behavior within the animation controller. <br/>
 * <br/>
 * Fields:
 * <ul>
 * <li>{@code animation}: The {@link AzBakedAnimation} instance that contains the actual animation data to be played.</li>
 * <li>{@code loopType}: The {@link AzLoopType} that dictates the looping behavior or termination handling for the
 * animation.</li>
 * </ul>
 *
 * @deprecated
 */
@Deprecated()
public class AzQueuedAnimation {
    public AzBakedAnimation animation;
    public AzLoopType loopType;

    public AzQueuedAnimation(AzBakedAnimation animation, AzLoopType loopType) {
        this.animation = animation;
        this.loopType = loopType;
    }

    public AzBakedAnimation animation() {
        return animation;
    }

    public AzLoopType loopType() {
        return loopType;
    }
}
