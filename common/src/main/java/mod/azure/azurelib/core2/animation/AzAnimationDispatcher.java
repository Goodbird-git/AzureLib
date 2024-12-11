package mod.azure.azurelib.core2.animation;

import mod.azure.azurelib.common.internal.common.network.packet.AzEntityAnimTriggerPacket;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.platform.Services;

public class AzAnimationDispatcher<T extends Entity> {

    private final T entity;

    public AzAnimationDispatcher(T entity) {
        this.entity = entity;
    }

    public void dispatch(@Nullable String controllerName, String animationName) {
        if (entity.level().isClientSide()) {
            AzAnimatorAccessor.get(entity)
                .map(AzAnimator::getAnimationControllerContainer)
                .map(container -> container.getOrNull(controllerName))
                .ifPresent(controller -> controller.tryTriggerAnimation(animationName));
        } else {
            var entityId = entity.getId();
            var entityAnimTriggerPacket = new AzEntityAnimTriggerPacket(
                entityId,
                controllerName,
                animationName
            );
            Services.NETWORK.sendToTrackingEntityAndSelf(entityAnimTriggerPacket, entity);
        }
    }
}
