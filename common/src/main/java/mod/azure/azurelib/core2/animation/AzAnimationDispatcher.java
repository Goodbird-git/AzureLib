package mod.azure.azurelib.core2.animation;

import mod.azure.azurelib.common.internal.common.network.packet.EntityAnimTriggerPacket;
import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class AzAnimationDispatcher<T extends Entity> {

    private final T entity;

    public AzAnimationDispatcher(T entity) {
        this.entity = entity;
    }

    public void dispatch(@Nullable String controllerName, String animationName) {
        if (entity.level().isClientSide()) {
            @SuppressWarnings("unchecked")
            var entityAnimatorCache = (AzAnimatorAccessor<T>) entity;
            var cachedEntityAnimator = (AzEntityAnimator<T>) entityAnimatorCache.getAnimator();

            if (cachedEntityAnimator == null) {
                return;
            }

            var controller = cachedEntityAnimator.getAnimationControllerContainer().getOrNull(controllerName);

            if (controller != null) {
                controller.tryTriggerAnimation(animationName);
            }
        } else {
            var entityId = entity.getId();
            var entityAnimTriggerPacket = new EntityAnimTriggerPacket(
                entityId,
                false,
                controllerName,
                animationName
            );
            Services.NETWORK.sendToTrackingEntityAndSelf(entityAnimTriggerPacket, entity);
        }
    }
}
