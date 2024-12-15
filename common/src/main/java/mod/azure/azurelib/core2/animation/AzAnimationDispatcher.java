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
