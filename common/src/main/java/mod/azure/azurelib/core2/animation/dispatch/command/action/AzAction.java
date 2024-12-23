package mod.azure.azurelib.core2.animation.dispatch.command.action;

import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.core2.animation.dispatch.command.action.codec.AzActionCodec;

/**
 * The AzAction interface serves as a base contract for defining actions that can be dispatched within the animation
 * system. It provides methods for handling an action and retrieving its unique resource location identifier.
 * Implementations of this interface encapsulate specific animation-related behaviors, allowing for the modification or
 * control of animation states or properties within an {@link AzAnimator}.
 */
public interface AzAction {

    AzActionCodec CODEC = new AzActionCodec();

    void handle(AzDispatchSide originSide, AzAnimator<?> animator);

    ResourceLocation getResourceLocation();
}
