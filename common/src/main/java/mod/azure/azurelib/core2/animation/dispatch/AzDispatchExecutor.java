package mod.azure.azurelib.core2.animation.dispatch;

import net.minecraft.world.entity.Entity;

import java.util.List;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.network.packet.AzEntityDispatchCommandPacket;
import mod.azure.azurelib.common.platform.Services;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;

public record AzDispatchExecutor(
    List<AzDispatchCommand> commands,
    AzDispatchSide origin
) {

    public void sendForEntity(Entity entity) {
        switch (origin) {
            case CLIENT -> dispatchFromClient(entity);
            case SERVER -> dispatchFromServer(entity);
        }
    }

    private <T> void dispatchFromClient(T animatable) {
        if (origin != AzDispatchSide.CLIENT) {
            AzureLib.LOGGER.warn("Dispatch origin mismatch - expected CLIENT, got {}.", origin);
            return;
        }

        var isClientSide = isClientSide(animatable);

        if (!isClientSide) {
            AzureLib.LOGGER.warn("Attempted client-side animation dispatch from server side.");
            return;
        }

        var animator = AzAnimatorAccessor.getOrNull(animatable);

        if (animator != null) {
            commands.forEach(command -> {
                command.getActions().forEach(action -> action.handle(animator));
            });
        }
    }

    private <T> void dispatchFromServer(T animatable) {
        if (origin != AzDispatchSide.SERVER) {
            AzureLib.LOGGER.warn("Dispatch origin mismatch - expected SERVER, got {}.", origin);
            return;
        }

        var isClientSide = isClientSide(animatable);

        if (isClientSide) {
            AzureLib.LOGGER.warn("Attempted server-side animation dispatch from client side.");
            return;
        }

        if (animatable instanceof Entity entity) {
            handleServerDispatchForEntity(entity);
        }
        // TODO: Armors
        // TODO: Blocks
        // TODO: Items
    }

    private void handleServerDispatchForEntity(Entity entity) {
        var entityId = entity.getId();

        commands.forEach(command -> {
            // TODO: Buffer commands together.
            var packet = new AzEntityDispatchCommandPacket(entityId, command, origin);
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
