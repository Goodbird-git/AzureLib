package mod.azure.azurelib.core2.animation.dispatch;

import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.network.packet.AzEntityDispatchCommandPacket;
import mod.azure.azurelib.common.internal.common.network.packet.AzItemStackDispatchCommandPacket;
import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;

public record AzDispatchExecutor(
    List<AzDispatchCommand> commands,
    AzDispatchSide origin
) {

    public void sendForEntity(Entity entity) {
        if (!canNotProceed(entity)) {
            // TODO: Log here.
            return;
        }

        switch (origin) {
            case CLIENT -> dispatchFromClient(entity);
            case SERVER -> handleServerDispatchForEntity(entity);
        }
    }

    public void sendForItem(Entity entity, ItemStack itemStack) {
        if (!canNotProceed(entity)) {
            // TODO: Log here.
            return;
        }

        // TODO: What if this isn't a PatchedDataComponentMap?
        if (
            itemStack.getComponents() instanceof PatchedDataComponentMap components && !components.has(
                AzureLib.AZ_ID.get()
            )
        ) {
            components.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
        }

        switch (origin) {
            case CLIENT -> dispatchFromClient(itemStack);
            case SERVER -> handleServerDispatchForItem(entity, itemStack);
        }
    }

    private <T> boolean canNotProceed(T animatable) {
        var isLogicalClientSide = isClientSide(animatable);
        return (origin == AzDispatchSide.CLIENT) == isLogicalClientSide;
    }

    private <T> void dispatchFromClient(T animatable) {
        var animator = AzAnimatorAccessor.getOrNull(animatable);

        if (animator != null) {
            commands.forEach(command -> {
                command.getActions().forEach(action -> action.handle(animator));
            });
        }
    }

    private void handleServerDispatchForEntity(Entity entity) {
        var entityId = entity.getId();

        commands.forEach(command -> {
            // TODO: Buffer commands together.
            var packet = new AzEntityDispatchCommandPacket(entityId, command, origin);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        });
    }

    private void handleServerDispatchForItem(Entity entity, ItemStack itemStack) {
        var uuid = itemStack.get(AzureLib.AZ_ID.get());
        commands.forEach(command -> {
            // TODO: Buffer commands together.
            var packet = new AzItemStackDispatchCommandPacket(uuid, command, origin);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        });
    }

    private <T> boolean isClientSide(T animatable) {
        if (animatable instanceof Entity entity) {
            return entity.level().isClientSide();
        }

        throw new IllegalArgumentException("Unhandled animatable type: " + animatable);
    }
}
