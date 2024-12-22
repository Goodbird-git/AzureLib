package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.dispatch.command.action.AzDispatchAction;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

/**
 * The AzRootCancelAllAction class implements the AzDispatchAction interface and defines an action that cancels all
 * ongoing animations within an animator by setting the current animation of all controllers to null.
 * <br>
 * This class is designed to work within a system that manages animations for objects using animation controllers. Once
 * this action is handled, all animation controllers associated with a specific animator will have their current
 * animations cleared.
 */
public class AzRootCancelAllAction implements AzDispatchAction {

    public static final StreamCodec<FriendlyByteBuf, AzRootCancelAllAction> CODEC = StreamCodec.unit(
        new AzRootCancelAllAction()
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/cancel_all");

    @Override
    public void handle(AzAnimator<?> animator) {
        AzAnimationControllerContainer controllerContainer = animator.getAnimationControllerContainer();
        Collection<AzAnimationController> controllers = controllerContainer.getAll();

        controllers.forEach(controller -> {
            controller.setCurrentAnimation(null);
        });
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
