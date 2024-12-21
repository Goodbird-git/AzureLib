package mod.azure.azurelib.core2.animation.dispatch;

import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.UUID;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.network.packet.AzBlockEntityDispatchCommandPacket;
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
            AzureLib.LOGGER.warn(
                "Could not dispatch animation command for entity. Origin: {}, Entity: {}",
                origin,
                entity.getType().toShortString()
            );
            return;
        }

        switch (origin) {
            case CLIENT -> dispatchFromClient(entity);
            case SERVER -> handleServerDispatchForEntity(entity);
        }
    }

    public void sendForBlockEntity(BlockEntity entity) {
        if (!canNotProceed(entity)) {
            AzureLib.LOGGER.warn(
                "Could not dispatch animation command for block entity. Origin: {}, Block Entity: {}",
                origin,
                entity.getType()
            );
            return;
        }

        switch (origin) {
            case CLIENT -> dispatchFromClient(entity);
            case SERVER -> handleServerDispatchForBlockEntity(entity);
        }
    }

    public void sendForItem(Entity entity, ItemStack itemStack) {
        if (!canNotProceed(entity)) {
            AzureLib.LOGGER.warn(
                "Could not dispatch animation command for item. Origin: {}, Entity: {}, Item: {}",
                origin,
                entity.getType().toShortString(),
                itemStack.getItem()
            );
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
            commands.forEach(command -> command.getActions().forEach(action -> action.handle(animator)));
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

    private void handleServerDispatchForBlockEntity(BlockEntity entity) {
        var entityBlockPos = entity.getBlockPos();

        commands.forEach(command -> {
            // TODO: Batch commands together.
            var packet = new AzBlockEntityDispatchCommandPacket(entityBlockPos, command, origin);
            Services.NETWORK.sendToEntitiesTrackingChunk(packet, (ServerLevel) entity.getLevel(), entityBlockPos);
        });
    }

    private void handleServerDispatchForItem(Entity entity, ItemStack itemStack) {
        var uuid = itemStack.get(AzureLib.AZ_ID.get());
        commands.forEach(command -> {
            // TODO: Batch commands together.
            var packet = new AzItemStackDispatchCommandPacket(uuid, command, origin);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        });
    }

    private <T> boolean isClientSide(T levelHolder) {
        if (levelHolder instanceof Entity entity) {
            return entity.level().isClientSide();
        }

        if (levelHolder instanceof BlockEntity entity) {
            return entity.getLevel().isClientSide();
        }

        throw new IllegalArgumentException("Unhandled animatable type: " + levelHolder);
    }
}
