package mod.azure.azurelib.core2.animation.dispatch;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.network.packet.AzBlockEntityDispatchCommandPacket;
import mod.azure.azurelib.common.internal.common.network.packet.AzEntityDispatchCommandPacket;
import mod.azure.azurelib.common.internal.common.network.packet.AzItemStackDispatchCommandPacket;
import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.dispatch.command.AzCommand;

public record AzDispatchExecutor(
    List<AzCommand> commands,
    AzDispatchSide origin
) {

    /**
     * Sends animation commands for the specified entity based on the configured dispatch origin. The method determines
     * whether the command should proceed, logs a warning if it cannot, and dispatches the animation commands either
     * from the client or the server side.
     *
     * @param entity the target {@link Entity} for which the animation commands are dispatched.
     */
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

    /**
     * Sends animation commands for the specified block entity based on the configured dispatch origin. The method
     * determines whether the command should proceed, logs a warning if it cannot, and dispatches the animation commands
     * either from the client or the server side.
     *
     * @param entity the target {@link BlockEntity} for which the animation commands are dispatched.
     */
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

    /**
     * Sends animation commands for the specified item based on the configured dispatch origin. The method determines
     * whether the command can proceed, assigns a unique identifier to the item if required, and dispatches the
     * animation commands either from the client or the server side.
     *
     * @param entity    the {@link Entity} associated with the {@link ItemStack}.
     * @param itemStack the {@link ItemStack} on which the animation commands are dispatched.
     */
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
            commands.forEach(command -> command.actions().forEach(action -> action.handle(origin, animator)));
        }
    }

    private void handleServerDispatchForEntity(Entity entity) {
        var entityId = entity.getId();

        commands.forEach(command -> {
            // TODO: Buffer commands together.
            var packet = new AzEntityDispatchCommandPacket(entityId, command);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        });
    }

    private void handleServerDispatchForBlockEntity(BlockEntity entity) {
        var entityBlockPos = entity.getBlockPos();

        commands.forEach(command -> {
            // TODO: Batch commands together.
            var packet = new AzBlockEntityDispatchCommandPacket(entityBlockPos, command);
            Services.NETWORK.sendToEntitiesTrackingChunk(packet, (ServerLevel) entity.getLevel(), entityBlockPos);
        });
    }

    private void handleServerDispatchForItem(Entity entity, ItemStack itemStack) {
        var uuid = itemStack.get(AzureLib.AZ_ID.get());

        if (uuid == null) {
            AzureLib.LOGGER.warn(
                "Could not find item stack UUID during dispatch. Did you forget to register an identity for the item? Item: {}, Item Stack: {}",
                itemStack.getItem(),
                itemStack
            );
            return;
        }

        commands.forEach(command -> {
            // TODO: Batch commands together.
            var packet = new AzItemStackDispatchCommandPacket(uuid, command);
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
