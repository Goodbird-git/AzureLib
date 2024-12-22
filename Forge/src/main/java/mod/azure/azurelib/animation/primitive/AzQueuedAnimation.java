package mod.azure.azurelib.animation.primitive;

/**
 * Represents an entry in an animation queue, combining an animation and its looping behavior. This record defines a
 * queued animation to be played, including its associated {@link AzAnimation} instance and the {@link AzLoopType} that
 * determines how the animation behaves once it reaches the end of its sequence. <br/>
 * <br/>
 * Instances of AzQueuedAnimation are immutable by design, ensuring that queued animations, once defined, cannot be
 * modified, preserving their behavior within the animation controller. <br/>
 * <br/>
 * Fields:
 * <ul>
 * <li>{@code animation}: The {@link AzAnimation} instance that contains the actual animation data to be played.</li>
 * <li>{@code loopType}: The {@link AzLoopType} that dictates the looping behavior or termination handling for the
 * animation.</li>
 * </ul>
 */
public class AzQueuedAnimation {
    public AzAnimation animation;
    public AzLoopType loopType;

    public AzQueuedAnimation(AzAnimation animation, AzLoopType loopType) {
        this.animation = animation;
        this.loopType = loopType;
    }

    public AzAnimation animation() {
        return animation;
    }

    public AzLoopType loopType() {
        return loopType;
    }
}
