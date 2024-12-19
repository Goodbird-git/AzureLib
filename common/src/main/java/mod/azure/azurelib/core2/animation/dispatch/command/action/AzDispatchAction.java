package mod.azure.azurelib.core2.animation.dispatch.command.action;

import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.command.action.codec.AzDispatchActionCodec;

public interface AzDispatchAction {

    AzDispatchActionCodec CODEC = new AzDispatchActionCodec();

    void handle(AzAnimator<?> animator);

    ResourceLocation getResourceLocation();
}
