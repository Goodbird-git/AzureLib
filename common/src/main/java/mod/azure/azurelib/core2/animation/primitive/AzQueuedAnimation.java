package mod.azure.azurelib.core2.animation.primitive;

/**
 * {@link AzAnimation} and {@link AzLoopType} override pair.
 */
public record AzQueuedAnimation(
    AzAnimation animation,
    AzLoopType loopType
) {}
