package mod.azure.azurelib.animation.dispatch;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorAccessor;
import mod.azure.azurelib.animation.dispatch.command.AzDispatchCommand;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AzDispatchExecutor {
    List<AzDispatchCommand> commands;
    AzDispatchSide origin;

    public AzDispatchExecutor(List<AzDispatchCommand> commands, AzDispatchSide origin) {
        this.commands = commands;
        this.origin = origin;
    }

    public List<AzDispatchCommand> commands() {
        return commands;
    }

    public AzDispatchSide origin() {
        return origin;
    }

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
            case CLIENT: dispatchFromClient(entity);
            case SERVER: handleServerDispatchForEntity(entity);
        }
    }

    /**
     * Sends animation commands for the specified block entity based on the configured dispatch origin. The method
     * determines whether the command should proceed, logs a warning if it cannot, and dispatches the animation commands
     * either from the client or the server side.
     *
     * @param entity the target {@link TileEntity} for which the animation commands are dispatched.
     */
    public void sendForBlockEntity(TileEntity entity) {
        if (!canNotProceed(entity)) {
            AzureLib.LOGGER.warn(
                "Could not dispatch animation command for block entity. Origin: {}, Block Entity: {}",
                origin,
                entity.getType()
            );
            return;
        }

        switch (origin) {
            case CLIENT: dispatchFromClient(entity);
            case SERVER: handleServerDispatchForBlockEntity(entity);
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
            case CLIENT: dispatchFromClient(itemStack);
            case SERVER: handleServerDispatchForItem(entity, itemStack);
        }
    }

    private <T> boolean canNotProceed(T animatable) {
        boolean isLogicalClientSide = isClientSide(animatable);
        return (origin == AzDispatchSide.CLIENT) == isLogicalClientSide;
    }

    private <T> void dispatchFromClient(T animatable) {
        AzAnimator<T> animator = AzAnimatorAccessor.getOrNull(animatable);

        if (animator != null) {
            commands.forEach(command -> command.getActions().forEach(action -> action.handle(animator)));
        }
    }

    private void handleServerDispatchForEntity(Entity entity) {
        int entityId = entity.getEntityId();

        commands.forEach(command -> {
            // TODO: Buffer commands together.
            var packet = new AzEntityDispatchCommandPacket(entityId, command, origin);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        });
    }

    private void handleServerDispatchForBlockEntity(TileEntity entity) {
        BlockPos entityBlockPos = entity.getPos();

        commands.forEach(command -> {
            // TODO: Batch commands together.
            var packet = new AzBlockEntityDispatchCommandPacket(entityBlockPos, command, origin);
            Services.NETWORK.sendToEntitiesTrackingChunk(packet, (ServerLevel) entity.getLevel(), entityBlockPos);
        });
    }

    private void handleServerDispatchForItem(Entity entity, ItemStack itemStack) {
        java.util.UUID uuid = itemStack.serializeNBT().getUniqueId("az_id");
        commands.forEach(command -> {
            // TODO: Batch commands together.
            var packet = new AzItemStackDispatchCommandPacket(uuid, command, origin);
            Services.NETWORK.sendToTrackingEntityAndSelf(packet, entity);
        });
    }

    private <T> boolean isClientSide(T levelHolder) {
        if (levelHolder instanceof Entity) {
            return ((Entity) levelHolder).world.isRemote;
        }

        if (levelHolder instanceof TileEntity) {
            return ((TileEntity) levelHolder).getWorld().isRemote;
        }

        throw new IllegalArgumentException("Unhandled animatable type: " + levelHolder);
    }
}
