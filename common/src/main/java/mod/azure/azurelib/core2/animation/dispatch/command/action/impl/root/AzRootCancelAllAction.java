package mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

public class AzRootCancelAllAction implements AzDispatchAction {

    public static final StreamCodec<FriendlyByteBuf, AzRootCancelAllAction> CODEC = StreamCodec.unit(
        new AzRootCancelAllAction()
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/cancel_all");

    @Override
    public void handle(AzAnimator<?> animator) {
        var controllerContainer = animator.getAnimationControllerContainer();
        var controllers = controllerContainer.getAll();

        controllers.forEach(controller -> {
            controller.setCurrentAnimation(null);
        });
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
