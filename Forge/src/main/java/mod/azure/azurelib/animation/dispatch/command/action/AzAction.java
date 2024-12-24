package mod.azure.azurelib.animation.dispatch.command.action;

import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import net.minecraft.util.ResourceLocation;

/**
 * The AzAction interface serves as a base contract for defining actions that can be dispatched within the
 * animation system. It provides methods for handling an action and retrieving its unique resource location identifier.
 * Implementations of this interface encapsulate specific animation-related behaviors, allowing for the modification or
 * control of animation states or properties within an {@link AzAnimator}.
 */
public interface AzAction {

    void handle(AzDispatchSide originSide, AzAnimator<?> animator);

    ResourceLocation getResourceLocation();

    void toBytes(ByteBuf buf);

    void fromBytes(ByteBuf buf);
}
