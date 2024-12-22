package mod.azure.azurelib.core2.animation;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.network.packet.AzEntityAnimTriggerPacket;
import mod.azure.azurelib.common.platform.Services;

public class AzAnimationDispatcher {

    private final Entity entity;

    public AzAnimationDispatcher(Entity entity) {
        this.entity = entity;
    }

    /**
     * Dispatches an animation from the client side to the specified animation controller and triggers the animation.
     * Logs a warning if the method is called from the server side, as the action is intended for client-side execution
     * only.
     *
     * @param controllerName The name of the animation controller to target. Can be null if no specific controller is
     *                       required.
     * @param animationName  The name of the animation to trigger. Must not be null.
     */
    public void dispatchFromClient(@Nullable String controllerName, String animationName) {
        if (!entity.level().isClientSide) {
            AzureLib.LOGGER.warn(
                "Attempted client-side animation dispatch from server side. Animation will not play! Controller: {}, Animation: {}",
                controllerName,
                animationName
            );
            return;
        }

        AzAnimatorAccessor.get(entity)
            .map(AzAnimator::getAnimationControllerContainer)
            .map(controllerContainer -> controllerContainer.getOrNull(controllerName))
            .ifPresent(controller -> controller.tryTriggerAnimation(animationName));
    }

    /**
     * Dispatches an animation from the server to the specified animation controller and triggers the animation for the
     * given entity. If called from the client side, will log a warning and take no action.
     *
     * @param controllerName The name of the animation controller to target. Can be null if no specific controller is
     *                       required.
     * @param animationName  The name of the animation to trigger. Must not be null.
     */
    public void dispatchFromServer(@Nullable String controllerName, String animationName) {
        if (entity.level().isClientSide) {
            AzureLib.LOGGER.warn(
                "Attempted server-side animation dispatch from client side. Controller: {}, Animation: {}",
                controllerName,
                animationName
            );
            return;
        }

        var entityId = entity.getId();
        var packet = new AzEntityAnimTriggerPacket(entityId, controllerName, animationName);
        Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
    }
}
