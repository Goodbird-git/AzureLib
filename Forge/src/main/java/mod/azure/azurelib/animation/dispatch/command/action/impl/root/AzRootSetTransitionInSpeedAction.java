package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.dispatch.command.action.AzDispatchAction;
import net.minecraft.util.ResourceLocation;

/**
 * The {@code AzRootSetTransitionInSpeedAction} class implements the {@link AzDispatchAction} interface and represents
 * an action that modifies the transition in speed for an animator during an animation state. This action is intended
 * for use within the animation system to adjust the transition timing of animations. This class provides a unique
 * resource location identifier for this specific action and handles the logic required to apply the transition speed
 * modification to the target {@link AzAnimator}. It utilizes {@link StreamCodec} for serialization and deserialization
 * of this action.
 */
public class AzRootSetTransitionInSpeedAction implements AzDispatchAction {

    public static final StreamCodec<FriendlyByteBuf, AzRootSetTransitionInSpeedAction> CODEC = StreamCodec.unit(
        new AzRootSetTransitionInSpeedAction()
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/set_transition_in_speed");

    @Override
    public void handle(AzAnimator<?> animator) {
        // TODO: Modify animator transition length.
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
