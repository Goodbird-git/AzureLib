package mod.azure.azurelib.core2.animation.primitive;

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
public record AzQueuedAnimation(
    AzAnimation animation,
    AzLoopType loopType
) {}
