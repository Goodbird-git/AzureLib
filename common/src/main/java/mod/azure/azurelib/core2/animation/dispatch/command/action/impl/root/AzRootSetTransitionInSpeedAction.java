package mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

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
