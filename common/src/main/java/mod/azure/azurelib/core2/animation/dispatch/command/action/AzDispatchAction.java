package mod.azure.azurelib.core2.animation.dispatch.command.action;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.command.action.codec.AzDispatchActionCodec;
import net.minecraft.resources.ResourceLocation;

public interface AzDispatchAction {

    AzDispatchActionCodec CODEC = new AzDispatchActionCodec();

    void handle(AzAnimator<?> animator);

    ResourceLocation getResourceLocation();
}
